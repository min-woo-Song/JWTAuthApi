package com.JWTAuthApi.demo.security.jwt.util;

import lombok.Data;

@Data
public class UserInfo {
    private Long userId;
    private String email;
    private String name;
}
