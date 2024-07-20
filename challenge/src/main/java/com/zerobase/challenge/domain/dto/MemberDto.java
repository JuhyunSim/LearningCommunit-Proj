package com.zerobase.challenge.domain.dto;

import com.zerobase.challenge.domain.enums.Gender;
import com.zerobase.challenge.domain.enums.MemberLevel;
import com.zerobase.challenge.domain.enums.Provider;
import com.zerobase.challenge.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class MemberDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String nickName;
    private String name;
    private LocalDate birth;
    private String job;
    private String interests;
    private Gender gender;
    private Long points;
    private Provider provider;
    private String providerId;
    private MemberLevel level;
    private List<Role> roles;
}
