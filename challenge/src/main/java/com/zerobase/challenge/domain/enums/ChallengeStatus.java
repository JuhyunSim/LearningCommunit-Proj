package com.zerobase.challenge.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChallengeStatus {
    PENDING("대기 중"),
    ONGOING("진행 중"),
    COMPLETED("완료"),
    EXPIRED("기간 만료"),
    CANCELLED("취소");

    private final String displayName;
}
