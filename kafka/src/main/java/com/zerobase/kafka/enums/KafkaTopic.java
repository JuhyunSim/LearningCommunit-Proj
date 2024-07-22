package com.zerobase.kafka.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KafkaTopic {
    CHALLENGE_CREATED("challenge-created"),
    CHALLENGE_UPDATED("challenge-updated"),
    CHALLENGE_DELETED("challenge-deleted");
    private final String name;
}
