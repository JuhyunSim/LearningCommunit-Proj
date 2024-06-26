package com.zerobase.user.controller;

import com.zerobase.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final MemberRepository memberRepository;

    //로그인 성공 후 반환
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> home(@AuthenticationPrincipal OAuth2User principal) {

        Map<String, Object> response = new HashMap<>();
        response.put("name", principal.getAttribute("name"));
        response.put("email", principal.getAttribute("email"));

        return ResponseEntity.ok(response);
    }
}
