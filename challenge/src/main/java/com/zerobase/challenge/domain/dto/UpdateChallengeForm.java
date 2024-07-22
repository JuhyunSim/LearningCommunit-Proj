package com.zerobase.challenge.domain.dto;

import com.zerobase.challenge.customAnnotation.StartDate;
import com.zerobase.challenge.domain.entity.ChallengeEntity;
import com.zerobase.challenge.domain.enums.Category;
import com.zerobase.challenge.domain.enums.ChallengeStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UpdateChallengeForm implements ChangeChallengeForm{

    private Long userId;

    @NotNull
    private String title;

    @NotNull(message = "수정할 챌린지를 선택해주세요.")
    private Long challengeId;

    @NotNull(message = "분야를 설정해주세요.")
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull(message = "목표를 입력해주세요.")
    @Size(max = 100, message = "목표는 100자 이내로 입력해주세요.")
    private String goal;

    @NotNull(message = "시작 날짜를 입력해주세요.")
    @StartDate(message = "현재 날짜부터 입력이 가능합니다.")
    private LocalDate startDate;

    @NotNull(message = "목표 날짜를 입력해주세요.")
    @Future(message = "시작 날짜로부터 최소 1개월 이후부터 입력가능합니다.")
    private LocalDate dueDate;

    @Size(max = 1000, message = "기타 설명은 1000자 이내로 입력해주세요.")
    private String description;

    public ChallengeEntity toUpdatedChallengeEntity(
            Long challengeId, Long userId, String username, ChallengeStatus status
    ) {
        return ChallengeEntity.builder()
                .id(challengeId)
                .userId(userId)
                .username(username)
                .title(title)
                .category(category)
                .goal(goal)
                .startDate(startDate)
                .dueDate(dueDate)
                .description(description)
                .status(status)
                .build();
    }
}
