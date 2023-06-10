package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.domain.ProviderType;
import com.JWTAuthApi.demo.domain.RefreshToken;
import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.dto.token.RefreshTokenDto;
import com.JWTAuthApi.demo.dto.user.UserLoginDto;
import com.JWTAuthApi.demo.dto.user.UserLoginResponseDto;
import com.JWTAuthApi.demo.dto.user.UserSignupDto;
import com.JWTAuthApi.demo.dto.user.UserSignupResponseDto;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import com.JWTAuthApi.demo.service.RefreshTokenService;
import com.JWTAuthApi.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @PostMapping("/users")
    public ResponseEntity register(@RequestBody UserSignupDto userSignupDto) {

        // User 데이터를 받아와서 user 객체를 생성
        User user = User.builder()
                .email(userSignupDto.getEmail())
                .username(userSignupDto.getUsername())
                .password(passwordEncoder.encode(userSignupDto.getPassword()))
                .providerType(ProviderType.LOCAL)
                .roleType(RoleType.USER)
                .build();

        User saveUser = userService.saveUser(user);

        UserSignupResponseDto userSignupResponseDto = UserSignupResponseDto
                .builder()
                .userId(saveUser.getUserId())
                .email(saveUser.getEmail())
                .username(saveUser.getUsername())
                .build();
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/users/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLoginDto) {

        User user = userService.findByEmail(userLoginDto.getEmail());
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenizer.createAccessToken(user.getUserId(), user.getEmail(), user.getUsername(), RoleType.USER.getCode());
        String refreshToken = jwtTokenizer.createRefreshToken(user.getUserId(), user.getEmail(), user.getUsername(), RoleType.USER.getCode());

        // RefreshToken 저장
        RefreshToken RToken = new RefreshToken();
        RToken.setValue(refreshToken);
        RToken.setUserId(user.getUserId());
        refreshTokenService.saveRefreshToken(RToken);

        UserLoginResponseDto userLoginResponseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(user.getUserId())
                .name(user.getUsername())
                .build();

        return new ResponseEntity<>(userLoginResponseDto, HttpStatus.OK);
    }

    @PostMapping("/users/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity updateUser(@RequestHeader String authorization, @RequestBody Map<String, String> username) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        userService.updateUser(userId, username.get("username"));
        return new ResponseEntity<>(username, HttpStatus.OK);
    }
}
