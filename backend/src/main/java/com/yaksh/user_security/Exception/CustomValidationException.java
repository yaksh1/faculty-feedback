package com.yaksh.user_security.Exception;

public class CustomValidationException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomValidationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
