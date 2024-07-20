package com.zerobase.user.service;

import com.zerobase.common.dto.JwtResponse;
import com.zerobase.common.dto.OAuth2UserDto;
import com.zerobase.common.enums.Provider;
import com.zerobase.common.exception.CustomException;
import com.zerobase.common.exception.ErrorCode;
import com.zerobase.common.util.JwtUtil;
import com.zerobase.user.dto.LoginForm;
import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.enums.MemberLevel;
import com.zerobase.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final SecurityMemberService securityMemberService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public JwtResponse authenticate(LoginForm loginForm) throws Exception {
        //loginId와 비밀번호 일치여부 확인 (불일치 시 예외 발생)
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginForm.getUsername(),
                            loginForm.getPassword()
                    )
            );
            log.debug("Authentication successful");
        } catch (AuthenticationException e) {
            throw new CustomException(ErrorCode.INVALID_LOGIN);
        }

        final UserDetails userDetails = securityMemberService.loadUserByUsername(loginForm.getUsername());
        log.debug("UserService.loadUserByUsername(loginForm.getUsername()) successful");
        List<GrantedAuthority> authorities =
                new ArrayList<>(userDetails.getAuthorities());
        log.debug("Authorities : {}", authorities);
        final String accessJwt =
                jwtUtil.generateToken(userDetails.getUsername(), authorities);
        final String refreshJwt = jwtUtil.generateRefreshToken(userDetails.getUsername());

        return new JwtResponse(accessJwt, refreshJwt);
    }

    public JwtResponse refreshToken(String accessToken, String refreshToken) throws Exception {
        String username = jwtUtil.extractUsername(accessToken);
        if (!jwtUtil.validateToken(username,
                refreshToken)
        ) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        UserDetails userDetails = securityMemberService.loadUserByUsername(username);
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        String newAccessToken = jwtUtil.generateToken(username, authorities);
        return new JwtResponse(newAccessToken, refreshToken);
    }

    public JwtResponse oauthLogin(OAuth2UserDto oAuth2UserDto) {
        log.info("OAuth2UserDto attributes: {}", oAuth2UserDto.getAttributes());

        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        Provider provider = oAuth2UserDto.getProvider();
        String providerId = oAuth2UserDto.getProviderId();

        // 사용자 정보 처리 및 JWT 생성
        String accessToken =
                jwtUtil.generateToken(
                        providerId,
                        authorities);
        String refreshToken =
                jwtUtil.generateRefreshToken(providerId);

        // 사용자 정보 저장 또는 업데이트
        Optional<MemberEntity> memberOptional =
                memberRepository.findByProviderAndProviderId(provider, providerId);
        MemberEntity memberEntity =
                memberOptional.map(existingMember -> {
                    log.info("Existing MemberEntity found: name {}", existingMember.getName());
                    return existingMember;
                }).orElseGet(() -> {MemberEntity newMember = MemberEntity.builder()
                        .provider(provider)
                        .providerId(providerId)
                        .name(oAuth2UserDto.getName())
                        .level(MemberLevel.BEGINNER)
                        .points(0L)
                        .roles(oAuth2UserDto.getRoles())
                        .build();
                    memberRepository.save(newMember);
                    log.info("New MemberEntity saved: name {}", newMember.getName());
                    return newMember;
                });
        return JwtResponse.builder()
                .accessJwt(accessToken)
                .refreshJwt(refreshToken)
                .build();
    }
}
