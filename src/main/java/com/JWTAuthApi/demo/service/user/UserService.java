package com.JWTAuthApi.demo.service.user;

import com.JWTAuthApi.demo.domain.ProviderType;
import com.JWTAuthApi.demo.domain.RefreshToken;
import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.dto.token.RefreshTokenDto;
import com.JWTAuthApi.demo.dto.user.*;
import com.JWTAuthApi.demo.exception.CustomException;
import com.JWTAuthApi.demo.exception.ErrorCode;
import com.JWTAuthApi.demo.mapper.UserRepository;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public User findByEmail(String email) {
        if (userRepository.findByEmail(email) == null)
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        return userRepository.findByEmail(email);
    }

    @Transactional
    public User findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public UserSignupResponseDto saveUser(UserSignupDto userSignupDto) {
        if (userRepository.findByEmail(userSignupDto.getEmail()) != null)
            throw new CustomException(ErrorCode.EMAIL_DUPLICATE);

        User user = User.builder()
                .email(userSignupDto.getEmail())
                .username(userSignupDto.getUsername())
                .password(passwordEncoder.encode(userSignupDto.getPassword()))
                .providerType(ProviderType.LOCAL)
                .roleType(RoleType.USER)
                .build();

        userRepository.saveUser(user);

        return UserSignupResponseDto
                .builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public UserLoginResponseDto login(UserLoginDto userLoginDto) {
        User user = this.findByEmail(userLoginDto.getEmail());

        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.ID_PASSWORD_NOT_MATCH);

        String accessToken = jwtTokenizer.createAccessToken(user.getUserId(), user.getEmail(), user.getUsername(), RoleType.USER.getCode());
        String refreshToken = jwtTokenizer.createRefreshToken(user.getUserId(), user.getEmail(), user.getUsername(), RoleType.USER.getCode());

        RefreshToken rToken = new RefreshToken(user.getUserId(), refreshToken);
        refreshTokenService.saveRefreshToken(rToken);

        return UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .name(user.getUsername())
                .build();
    }

    @Transactional
    public UserUpdateResponseDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        userRepository.updateUser(userId, userUpdateDto.getUsername());
        String EncodePassword = passwordEncoder.encode(userUpdateDto.getPassword());
        userRepository.updateUserPassword(userId, EncodePassword);

        User findUser = this.findById(userId);
        return UserUpdateResponseDto
                .builder()
                .username(findUser.getUsername())
                .password(findUser.getPassword())
                .build();
    }

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
