package com.zerobase.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_CHALLENGE_DUEDATE(HttpStatus.BAD_REQUEST, "챌린지 진행 기간을 확인하세요"),
    CHALLENGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 챌린지를 찾을 수 없습니다."),
    UPDATABLE_CHALLENGE_NOT_FOUNT(HttpStatus.BAD_REQUEST, "수정 가능한 챌린지를 찾을 수 없습니다."),
    SHORTEN_DUEDATE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "이미 진행 중인 챌린지는 기한을 앞당길 수 없습니다."),
    CHALLENGE_STATUS_NOT_MODIFIABLE(HttpStatus.BAD_REQUEST,
            "이미 진행 중이거나 완료 또는 만료 상태인 챌린지는 변경할 수 없습니다."),
    DUPLICATE_CHALLENGE_PERIOD(
            HttpStatus.BAD_REQUEST,
            "요청한 기한 내에 이미 등록된 챌린지가 있습니다."),
    INVALID_CHALLENGE_USER(HttpStatus.BAD_REQUEST,
            "챌린지의 유저 아이디가 토큰 소유자의 아이디와 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
