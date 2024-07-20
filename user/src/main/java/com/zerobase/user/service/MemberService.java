package com.zerobase.user.service;

import com.zerobase.common.exception.CustomException;
import com.zerobase.common.exception.ErrorCode;
import com.zerobase.user.dto.MemberDto;
import com.zerobase.user.dto.RegisterForm;
import com.zerobase.user.dto.UpdateMemberForm;
import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.repository.MemberRepository;
import com.zerobase.user.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;

    @Transactional
    public MemberDto registerUser(RegisterForm registerForm)
            throws Exception {
        // 회원가입 정보 검증
        validateRegisterForm(registerForm);

        // 사용자 엔티티 생성 및 저장(비밀번호와 전화번호는 암호화)
        MemberEntity memberEntity = registerForm.toEntity(passwordEncoder, aesUtil);
        return memberRepository.save(memberEntity).toDto(aesUtil);
    }

    //나의 정보 조회
    @Transactional
    public MemberDto getMyInfo(String username) throws Exception {
        MemberEntity memberEntity = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        log.debug("memberEntity: {}", memberEntity);
        return memberEntity.toDto(aesUtil);
    }

    @Transactional
    public MemberDto updateMyInfo(
            String loginId, UpdateMemberForm updateMemberForm
    ) throws Exception {
        MemberEntity memberEntity = memberRepository.findByUsername(loginId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_USER)
                );

        MemberEntity updatedMemberEntity = MemberEntity.builder()
                .id(memberEntity.getId())
                .username(memberEntity.getUsername())
                .password(memberEntity.getPassword())
                .email(memberEntity.getEmail())
                .phoneNumber(memberEntity.getPhoneNumber())
                .nickName(updateMemberForm.getNickName())
                .name(updateMemberForm.getName())
                .birth(memberEntity.getBirth())
                .job(updateMemberForm.getJob())
                .interests(updateMemberForm.getInterests())
                .gender(updateMemberForm.getGender())
                .level(memberEntity.getLevel())
                .roles(memberEntity.getRoles())
                .provider(memberEntity.getProvider())
                .providerId(memberEntity.getProviderId())
                .points(memberEntity.getPoints())
                .build();

        return memberRepository.save(updatedMemberEntity).toDto(aesUtil);
    }

    @Transactional
    public void deleteAccount(String loginId) throws Exception {
        MemberEntity memberEntity = memberRepository.findByUsername(loginId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        memberRepository.delete(memberEntity);
    }

    private void validateRegisterForm(RegisterForm registerForm) throws Exception {
        // 로그인 ID 중복 체크
        if(memberRepository.existsByUsername(registerForm.getUsername())) {
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
