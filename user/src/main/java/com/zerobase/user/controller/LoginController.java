package com.zerobase.user.controller;

import com.zerobase.user.dto.JwtResponse;
import com.zerobase.user.dto.OAuth2UserDto;
import com.zerobase.user.util.JwtUtil;
import com.zerobase.user.dto.LoginForm;
import com.zerobase.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
//    private final CustomOAuth2UserService customOAuth2UserService;

    @PostMapping("/users/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody LoginForm loginForm
    ) throws Exception {
        log.info("{} 님이 로그인하였습니다: ", loginForm.getUsername());
        return ResponseEntity.ok(authService.authenticate(loginForm));
    }

    @PostMapping("/users/refresh")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<JwtResponse> refreshToken(
            @RequestHeader(name = "Authorization") String accessToken,
            @RequestHeader(name = "Refresh") String refreshToken
    ) throws Exception {
        refreshToken = refreshToken.replace("Bearer ", "");
        accessToken = accessToken.replace("Bearer ", "");
        return ResponseEntity.ok(authService.refreshToken(accessToken, refreshToken));
    }

    @PostMapping("/users/oauth2")
    public Mono<JwtResponse> getToken(
            @RequestBody OAuth2UserDto oAuth2UserDto
    ) {
        return Mono.just(authService.oauthLogin(oAuth2UserDto));
    }

}
