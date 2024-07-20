package com.zerobase.challenge.exception;

public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
