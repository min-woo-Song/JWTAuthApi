package com.JWTAuthApi.demo.security.jwt.util;

import lombok.Data;

@Data
public class MemberInfo {
    private Long memberId;
    private String email;
    private String name;
}
