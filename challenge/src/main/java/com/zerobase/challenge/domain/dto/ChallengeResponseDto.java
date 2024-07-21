package com.zerobase.challenge.domain.dto;

import com.zerobase.challenge.domain.enums.Category;
import com.zerobase.challenge.domain.enums.ChallengeStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class ChallengeResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private Category category;
    private String goal;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String description;
    private ChallengeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    @Builder
    @Getter
    @Setter
    public static class ChallengeSimpleDto {
        private Long id;
        private String username;
        private String title;
        private ChallengeStatus status;
        private LocalDate startDate;
        private LocalDate dueDate;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
    }
}
