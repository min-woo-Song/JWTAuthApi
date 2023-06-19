package com.JWTAuthApi.demo.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UserSignupDto {
    private String email;
    private String password;
    private String username;
}
