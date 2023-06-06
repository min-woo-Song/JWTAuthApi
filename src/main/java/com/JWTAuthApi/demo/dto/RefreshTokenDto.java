package com.JWTAuthApi.demo.dto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RefreshTokenDto {
    @NotEmpty
    private String refreshToken;
}