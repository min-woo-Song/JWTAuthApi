package com.JWTAuthApi.demo.service.login;

import com.JWTAuthApi.demo.domain.ProviderType;
import com.JWTAuthApi.demo.domain.RefreshToken;
import com.JWTAuthApi.demo.domain.RoleType;
import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.dto.login.UserLoginDto;
import com.JWTAuthApi.demo.dto.login.UserLoginResponseDto;
import com.JWTAuthApi.demo.dto.login.UserSignupDto;
import com.JWTAuthApi.demo.dto.login.UserSignupResponseDto;
import com.JWTAuthApi.demo.exception.CustomException;
import com.JWTAuthApi.demo.exception.ErrorCode;
import com.JWTAuthApi.demo.mapper.UserRepository;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import com.JWTAuthApi.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

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
        User user = userService.findByEmail(userLoginDto.getEmail());
        if (user == null)
            throw new CustomException(ErrorCode.ID_PASSWORD_NOT_MATCH);

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


}
