package com.zerobase.user.dto;

import com.zerobase.user.entity.MemberEntity;
import com.zerobase.user.enums.Gender;
import com.zerobase.user.enums.MemberLevel;
import com.zerobase.common.enums.Role;
import com.zerobase.user.util.AESUtil;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static com.zerobase.common.enums.Role.ROLE_USER;

@Getter
@Setter
@Builder
public class RegisterForm {
    //15자 제한 - 영문+숫자 허용
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$")
    private String username;
    //영문+숫자+특수문자 포함, 8~20자
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,20}$")
    private String password;
    //이메일 형식
    @NotBlank
    @Email
    private String email;
    //숫자만 포함, 10~15자
    @NotBlank
    @Pattern(regexp = "^\\d{10,15}$")
    private String phoneNumber;
    //2~12자
    @NotBlank
    @Size(min = 2, max = 12)
    private String nickName;
    //비어있지 않아야 함
    @NotBlank
    @Size(min = 2, max = 12)
    private String name;
    //과거 날짜
    @Past
    private LocalDate birth;
    private String job;
    private String interests;
    private Gender gender;
    private List<Role> roles;

    public MemberEntity toEntity(PasswordEncoder passwordEncoder, AESUtil aesUtil)
            throws Exception {
        return MemberEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .phoneNumber(aesUtil.encrypt(phoneNumber))
                .nickName(nickName)
                .name(name)
                .birth(birth)
                .job(job)
                .interests(interests)
                .gender(gender)
                .points(0L)
                .provider(null)  // 자체 로그인을 위한 Provider null
                .providerId(null)          // 자체 로그인은 providerId null
                .level(MemberLevel.BEGINNER) // 초기 회원 레벨 설정
                .roles(List.of(ROLE_USER))
                .build();
    }
}
