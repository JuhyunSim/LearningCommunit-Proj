package com.zerobase.user.service;

import com.zerobase.user.dto.RegisterForm;
import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.repository.MemberRepository;
import com.zerobase.user.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;

    public MemberEntity registerUser(RegisterForm registerForm)
            throws Exception {
        // 회원가입 정보 검증
        validateRegisterForm(registerForm);

        // 사용자 엔티티 생성 및 저장(비밀번호와 전화번호는 암호화)
        MemberEntity memberEntity = registerForm.toEntity(passwordEncoder, aesUtil);
        return memberRepository.save(memberEntity);
    }

    private void validateRegisterForm(RegisterForm registerForm) throws Exception {
        // 로그인 ID 중복 체크
        if(memberRepository.existsByLoginId(registerForm.getLoginId())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_LOGINID);
        }
        // 회원 이메일 중복 체크
        if (memberRepository.existsByEmail(registerForm.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }
        // 회원 전화번호 중복 체크
        if (memberRepository.existsByPhoneNumber(aesUtil.encrypt(registerForm.getPhoneNumber()))) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_PHONE);
        }
    }
}
