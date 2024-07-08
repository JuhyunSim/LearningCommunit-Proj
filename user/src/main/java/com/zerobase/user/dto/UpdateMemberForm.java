package com.zerobase.user.dto;

import com.zerobase.user.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateMemberForm {
    //2~12자
    @NotBlank
    @Size(min = 2, max = 12)
    private String nickName;
    @NotBlank
    @Size(min = 2, max = 12)
    private String name;
    private String job;
    private String interests;
    private Gender gender;
}
