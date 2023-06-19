package com.JWTAuthApi.demo.service.login;

import com.JWTAuthApi.demo.domain.RefreshToken;
import com.JWTAuthApi.demo.mapper.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.saveRefreshToken(refreshToken);
        return refreshToken;
    }

    public RefreshToken findRefreshToken(Long userId) {
        return refreshTokenRepository.findRefreshToken(userId);
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteRefreshToken(refreshToken);
    }
}
