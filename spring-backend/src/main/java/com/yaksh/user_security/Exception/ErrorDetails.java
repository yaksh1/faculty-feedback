package com.yaksh.user_security.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private int statusCode;
    private LocalDateTime timeStamp;
    private String errorMessage;
    private String status;
    private String errorCode;
}
