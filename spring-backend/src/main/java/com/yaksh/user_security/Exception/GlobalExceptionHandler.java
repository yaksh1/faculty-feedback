package com.yaksh.user_security.Exception;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ErrorDetails> handleCustomValidationException(CustomValidationException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setTimeStamp(LocalDateTime.now());
        errorDetails.setErrorCode(e.getErrorCode().name());

        // Function: Set different HTTP status codes based on errorCode
        if (e.getErrorCode()==ErrorCode.USER_ALREADY_EXISTS
                ||e.getErrorCode()==ErrorCode.INVALID_EMAIL
                ||e.getErrorCode()==ErrorCode.INVALID_PASSWORD
                || e.getErrorCode()==ErrorCode.USER_NOT_FOUND
                || e.getErrorCode()==ErrorCode.OTP_EXPIRED
                || e.getErrorCode()==ErrorCode.INVALID_OTP
                || e.getErrorCode()==ErrorCode.USER_ALREADY_VERIFIED
                || e.getErrorCode()==ErrorCode.ACCOUNT_NOT_VERIFIED
                || e.getErrorCode()==ErrorCode.INAPPROPRIATE_REVIEW
                || e.getErrorCode()==ErrorCode.REVIEW_TOO_SHORT
                || e.getErrorCode() == ErrorCode.ACCOUNT_BANNED

        ) {
            errorDetails.setStatusCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setStatus(HttpStatus.BAD_REQUEST.name());
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }
        else if(e.getErrorCode()==ErrorCode.DATA_NOT_FOUND){
            errorDetails.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorDetails.setStatus(HttpStatus.NOT_FOUND.name());
        }

        errorDetails.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> authenticationException(AuthenticationException e){
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setTimeStamp(LocalDateTime.now());
        errorDetails.setErrorCode(ErrorCode.BAD_CREDENTIALS.name());
        errorDetails.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.name());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
