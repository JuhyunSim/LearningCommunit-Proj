package com.zerobase.user.controller;

import com.zerobase.user.dto.MemberDto;
import com.zerobase.user.dto.RegisterForm;
import com.zerobase.user.dto.UpdateMemberForm;
import com.zerobase.user.service.MemberService;
import com.zerobase.common.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<MemberDto> registerUser(
            @RequestBody @Valid RegisterForm registerForm
    ) throws Exception {
        MemberDto registeredMember = memberService.registerUser(registerForm);
        return ResponseEntity.ok(registeredMember);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MemberDto> myInfo(@RequestHeader("Authorization") String token) throws Exception {
        String username = jwtUtil.extractUsername(token.substring(7));
        MemberDto myInfo = memberService.getMyInfo(username);
        return ResponseEntity.ok(myInfo);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MemberDto> updateMyInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid UpdateMemberForm updateMemberForm) throws Exception {
        String loginId = jwtUtil.extractUsername(token.substring(7));
        MemberDto updatedMember =
                memberService.updateMyInfo(loginId, updateMemberForm);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAccount(
            @RequestHeader("Authorization") String token
    ) throws Exception {
        String loginId = jwtUtil.extractUsername(token.substring(7));
        memberService.deleteAccount(loginId);
        log.info(loginId + "님이 회원탈퇴하였습니다.");
        return ResponseEntity.noContent().build();
    }

}
