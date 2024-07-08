package com.zerobase.user.controller;

import com.zerobase.user.dto.JwtResponse;
import com.zerobase.user.dto.LoginForm;
import com.zerobase.user.repository.MemberRepository;
import com.zerobase.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final MemberRepository memberRepository;
    private final AuthService authService;

    //로그인 성공 후 반환
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> home(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        return ResponseEntity.ok(principal.getAttributes());
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody LoginForm loginForm
    ) throws Exception {
        log.info("{} 님이 로그인하였습니다: ", loginForm.getUsername());
        return ResponseEntity.ok(authService.authenticate(loginForm));
    }
}
