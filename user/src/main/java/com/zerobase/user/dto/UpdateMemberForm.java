package com.zerobase.user.dto;

import com.zerobase.user.enums.Gender;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateMemberForm {
    private String nickName;
    private String name;
    private String job;
    private String interests;
    private Gender gender;
}
