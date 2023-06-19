package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.dto.token.RefreshTokenDto;
import com.JWTAuthApi.demo.dto.login.UserLoginResponseDto;
import com.JWTAuthApi.demo.dto.login.UserSignupResponseDto;
import com.JWTAuthApi.demo.dto.user.UserPasswordDto;
import com.JWTAuthApi.demo.dto.user.UserUpdateDto;
import com.JWTAuthApi.demo.dto.user.UserUpdateResponseDto;
import com.JWTAuthApi.demo.security.jwt.util.IfLogin;
import com.JWTAuthApi.demo.security.jwt.util.LoginUser;
import com.JWTAuthApi.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // 회원 정보
    @GetMapping
    public ResponseEntity currentUser(@IfLogin LoginUser loginUser) {
        UserSignupResponseDto userSignupResponseDto = userService.currentUser(loginUser.getUserId());
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.OK);
    }

    // 회원 username 수정
    @PutMapping
    public ResponseEntity updateUser(@IfLogin LoginUser loginUser, @RequestBody @Valid UserUpdateDto userUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserUpdateResponseDto userUpdateResponseDto = userService.updateUser(loginUser.getUserId(), userUpdateDto);
        return new ResponseEntity<>(userUpdateResponseDto, HttpStatus.OK);
    }

    // 회원 password 수정
    @PutMapping("/password")
    public void updatePassword(@IfLogin LoginUser loginUser, @RequestBody UserPasswordDto userPasswordDto) {
        userService.updatePassword(loginUser.getUserId(), userPasswordDto.getPassword());
    }

    // 토큰 재발급
    @PostMapping("/refreshToken")
    public ResponseEntity requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        UserLoginResponseDto userLoginResponseDto = userService.reissuingToken(refreshTokenDto);
        return new ResponseEntity<>(userLoginResponseDto, HttpStatus.OK);
    }
}
