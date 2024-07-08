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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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
        final String jwt =
                jwtUtil.generateToken(userDetails.getUsername(), authorities);

        return new JwtResponse(jwt);
    }
}
