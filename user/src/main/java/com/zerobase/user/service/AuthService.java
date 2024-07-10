package com.zerobase.user.service;

import com.zerobase.user.dto.JwtResponse;
import com.zerobase.user.dto.LoginForm;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final SecurityMemberService securityMemberService;
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
//        log.info("refresh access token issued at: {}", jwtUtil.extractAllClaims(refreshToken).getIssuedAt());
//        log.info("refresh access token expiration: {}", jwtUtil.extractAllClaims(refreshToken).getExpiration());
//        log.info("newAccessToken issued at: {}", jwtUtil.extractAllClaims(newAccessToken).getIssuedAt());
//        log.info("newAccessToken Expiration: {}", jwtUtil.extractAllClaims(newAccessToken).getExpiration());
        return new JwtResponse(newAccessToken, refreshToken);
    }
}
