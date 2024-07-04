package com.zerobase.user.controller;

import com.zerobase.user.dto.RegisterForm;
import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/register")
    public ResponseEntity<MemberEntity> registerUser(
            @RequestBody @Valid RegisterForm registerForm
    ) throws Exception {
        MemberEntity memberEntity = memberService.registerUser(registerForm);
        return ResponseEntity.ok(memberEntity);
    }
}
