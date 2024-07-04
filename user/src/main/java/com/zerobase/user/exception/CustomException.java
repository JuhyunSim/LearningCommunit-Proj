package com.zerobase.user.exception;

public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
