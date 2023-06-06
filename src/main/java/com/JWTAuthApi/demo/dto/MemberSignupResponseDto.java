package com.JWTAuthApi.demo.dto;

import lombok.Data;

@Data
public class MemberSignupResponseDto {
    private Long memberId;
    private String email;
    private String name;
}