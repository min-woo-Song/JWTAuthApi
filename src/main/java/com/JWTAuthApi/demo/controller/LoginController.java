package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.dto.token.RefreshTokenDto;
import com.JWTAuthApi.demo.dto.user.*;
import com.JWTAuthApi.demo.security.jwt.util.IfLogin;
import com.JWTAuthApi.demo.security.jwt.util.LoginUser;
import com.JWTAuthApi.demo.service.user.RefreshTokenService;
import com.JWTAuthApi.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    // 회원 가입
    @PostMapping("/users")
    public ResponseEntity register(@RequestBody UserSignupDto userSignupDto) {
        UserSignupResponseDto userSignupResponseDto = userService.saveUser(userSignupDto);
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/users/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLoginDto) {
        UserLoginResponseDto userLoginResponseDto = userService.login(userLoginDto);
        return new ResponseEntity<>(userLoginResponseDto, HttpStatus.OK);
    }
    
    // 로그아웃
    @PostMapping("/users/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 유저 정보
    @GetMapping("/user")
    public ResponseEntity currentUser(@IfLogin LoginUser loginUser) {
        UserSignupResponseDto userSignupResponseDto = userService.currentUser(loginUser.getUserId());
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.OK);
    }

    /**
     * 회원 정보 업데이트
     * Test 용 password까지 반환
     * 원래는 유저 정보를 Front로 보내줘서 표기해주어야 함
     * password는 api를 분리하는게 좋아 보임
     */
    @PutMapping("/user")
    public ResponseEntity updateUser(@IfLogin LoginUser loginUser, @RequestBody @Valid UserUpdateDto userUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserUpdateResponseDto userUpdateResponseDto = userService.updateUser(loginUser.getUserId(), userUpdateDto);
        return new ResponseEntity<>(userUpdateResponseDto, HttpStatus.OK);
    }

    // 토큰 재발급
    @PostMapping("/user/refreshToken")
    public ResponseEntity requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        UserLoginResponseDto userLoginResponseDto = userService.reissuingToken(refreshTokenDto);
        return new ResponseEntity<>(userLoginResponseDto, HttpStatus.OK);
    }
}
