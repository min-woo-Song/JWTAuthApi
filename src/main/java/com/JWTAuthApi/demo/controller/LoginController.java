package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.domain.ProviderType;
import com.JWTAuthApi.demo.domain.RefreshToken;
import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.dto.token.RefreshTokenDto;
import com.JWTAuthApi.demo.dto.user.*;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import com.JWTAuthApi.demo.service.RefreshTokenService;
import com.JWTAuthApi.demo.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    /**
     * ProtoType
     * Controller가 하지 않아도 되는 Logic 전부 Service로 이동시켜야 함
     */

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

        // DB 저장
        User saveUser = userService.saveUser(user);

        // DTO 생성 후 반환
        UserSignupResponseDto userSignupResponseDto = UserSignupResponseDto
                .builder()
                .userId(saveUser.getUserId())
                .email(saveUser.getEmail())
                .username(saveUser.getUsername())
                .build();
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/users/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLoginDto) {

        // DB password 검사, 다를 시 401 에러
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
                .userId(user.getUserId())
                .name(user.getUsername())
                .build();

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
    public ResponseEntity currentUser(@RequestHeader String authorization) {
        // SecurityContextHolder 에서 가져올 수도 있음. 어떤것이 좋을지 아직 확실치 않음
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        User findUser = userService.findById(userId);

        UserSignupResponseDto userSignupResponseDto = UserSignupResponseDto
                .builder()
                .userId(findUser.getUserId())
                .email(findUser.getEmail())
                .username(findUser.getUsername())
                .build();
        return new ResponseEntity<>(userSignupResponseDto, HttpStatus.OK);
    }

    /**
     * Test 용 password까지 반환
     * password는 api를 분리하는게 좋아 보임
     */
    @PutMapping("/user")
    public ResponseEntity updateUser(@RequestHeader String authorization, @RequestBody @Valid UserUpdateDto userUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);

        userService.updateUser(userId, userUpdateDto.getUsername());
        userService.updateUserPassword(userId, passwordEncoder.encode(userUpdateDto.getPassword()));
        User findUser = userService.findById(userId);
        UserUpdateResponseDto userUpdateResponseDto =
                UserUpdateResponseDto
                        .builder().username(findUser.getUsername()).password(findUser.getPassword()).build();

        return new ResponseEntity<>(userUpdateResponseDto, HttpStatus.OK);
    }

    // 토큰 재발급
    @PostMapping("/user/refreshToken")
    public ResponseEntity requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto) {

        // TODO refreshToken 검증 실패시 에러 추가해야함
        Claims claims = jwtTokenizer.parseRefreshToken(refreshTokenDto.getRefreshToken());

        // RefreshToken 에서 userId 추출
        Long userId = Long.valueOf((Integer) claims.get("userId"));

        // 추출한 userId 로 RefreshToken 서치 없을 시 에러
        User user = userService.findById(userId);

        List roles = (List) claims.get("roles");
        String email = claims.getSubject();
        String name = (String) claims.get("username");

        // DB 에서 가져온 정보로 AccessToken 생성
        String accessToken = jwtTokenizer.createAccessToken(userId, email, name, RoleType.USER.getCode());

        UserLoginResponseDto loginResponse = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenDto.getRefreshToken())
                .userId(userId)
                .name(user.getUsername())
                .build();

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
