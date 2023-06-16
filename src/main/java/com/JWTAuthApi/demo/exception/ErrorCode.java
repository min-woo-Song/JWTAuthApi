package com.JWTAuthApi.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    ID_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "id 혹은 password가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 사용자가 없습니다.");

    private HttpStatus httpStatus;
    private String message;

}
