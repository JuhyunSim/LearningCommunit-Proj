package com.zerobase.challenge.domain.dto;

import com.zerobase.challenge.domain.enums.Category;
import com.zerobase.challenge.domain.enums.ChallengeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ChallengeResponseDto {
    private Long id;
    private String username;
    private String title;
    private Category category;
    private String goal;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String description;
    private ChallengeStatus status;

}
