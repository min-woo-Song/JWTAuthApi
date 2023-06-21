package com.JWTAuthApi.demo.service.user;

import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.dto.token.RefreshTokenDto;
import com.JWTAuthApi.demo.dto.login.UserLoginResponseDto;
import com.JWTAuthApi.demo.dto.login.UserSignupResponseDto;
import com.JWTAuthApi.demo.dto.user.UserUpdateDto;
import com.JWTAuthApi.demo.dto.user.UserUpdateResponseDto;
import com.JWTAuthApi.demo.exception.CustomException;
import com.JWTAuthApi.demo.exception.ErrorCode;
import com.JWTAuthApi.demo.mapper.UserRepository;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User findById(Long userId) {
        return userRepository.findById(userId);
    }


    @Transactional
    public UserUpdateResponseDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        userRepository.updateUser(userId, userUpdateDto.getUsername());
//        String EncodePassword = passwordEncoder.encode(userUpdateDto.getPassword());
//        userRepository.updateUserPassword(userId, EncodePassword);

        User findUser = this.findById(userId);
        return UserUpdateResponseDto
                .builder()
                .username(findUser.getUsername())
                .build();
    }

    @Transactional
    public void updatePassword(Long userId, String password) {
        String EncodePassword = passwordEncoder.encode(password);
        userRepository.updateUserPassword(userId, EncodePassword);
    }

    @Transactional
    public UserSignupResponseDto currentUser(Long userId) {
        User findUser = this.findById(userId);
        return UserSignupResponseDto
                .builder()
                .userId(findUser.getUserId())
                .email(findUser.getEmail())
                .username(findUser.getUsername())
                .build();
    }

    @Transactional
    public UserLoginResponseDto reissuingToken(RefreshTokenDto refreshTokenDto) {

        Claims claims = jwtTokenizer.parseRefreshToken(refreshTokenDto.getRefreshToken());
        Long userId = Long.valueOf((Integer) claims.get("userId"));
        User user = this.findById(userId);

        String email = claims.getSubject();
        String name = (String) claims.get("username");
        String accessToken = jwtTokenizer.createAccessToken(userId, email, name, RoleType.USER.getCode());

        return UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenDto.getRefreshToken())
                .userId(userId)
                .name(user.getUsername())
                .build();
    }
}
