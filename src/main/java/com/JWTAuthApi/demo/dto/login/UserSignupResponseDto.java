package com.JWTAuthApi.demo.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupResponseDto {
    private Long userId;
    private String email;
    private String username;
}
