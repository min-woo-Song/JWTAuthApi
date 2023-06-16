package com.JWTAuthApi.demo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CustomException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;
    public CustomException(ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }
}
