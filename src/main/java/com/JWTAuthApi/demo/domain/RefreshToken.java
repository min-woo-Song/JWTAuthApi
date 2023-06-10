package com.JWTAuthApi.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {
    private Long id;
    private Long userId;
    private String value;
}
