package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.security.auth.userInfo.UserPrincipal;
import com.JWTAuthApi.demo.security.jwt.util.IfLogin;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import com.JWTAuthApi.demo.security.jwt.util.LoginUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final JwtTokenizer jwtTokenizer;

    @GetMapping("/test")
    public LoginUser argumentsTest(@IfLogin LoginUser loginUser) {
        return loginUser;
    }

    @GetMapping("/test2")
    public String userCheck() {
        return "User Check ok";
    }
}
