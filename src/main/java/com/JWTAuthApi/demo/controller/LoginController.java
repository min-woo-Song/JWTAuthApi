package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.dto.login.UserLoginDto;
import com.JWTAuthApi.demo.dto.login.UserLoginResponseDto;
import com.JWTAuthApi.demo.dto.login.UserSignupDto;
import com.JWTAuthApi.demo.dto.login.UserSignupResponseDto;
import com.JWTAuthApi.demo.dto.token.RefreshTokenDto;
import com.JWTAuthApi.demo.service.login.RefreshTokenService;
import com.JWTAuthApi.demo.service.login.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class LoginController {

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    // 회원 가입
    @PostMapping
    public ResponseEntity register(@RequestBody UserSignupDto userSignupDto) {
        UserSignupResponseDto userSignupResponseDto = loginService.saveUser(userSignupDto);
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginDto userLoginDto) {
        return loginService.login(userLoginDto);
    }
    
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
