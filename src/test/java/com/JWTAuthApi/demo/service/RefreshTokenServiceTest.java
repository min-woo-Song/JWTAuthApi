package com.JWTAuthApi.demo.service;

import com.JWTAuthApi.demo.domain.RefreshToken;
import com.JWTAuthApi.demo.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RefreshTokenServiceTest {

    @Autowired
    RefreshTokenService refreshTokenService;

    @Test
    void saveRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(1L);
        refreshToken.setValue("token");

        RefreshToken saveToken = refreshTokenService.saveRefreshToken(refreshToken);
        Long userId = saveToken.getUserId();

        RefreshToken token = refreshTokenService.findRefreshToken(userId);

        assertThat(token.getUserId()).isEqualTo(userId);
    }

    @Test
    @Commit
    void deleteRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(1L);
        refreshToken.setValue("token");

        RefreshToken saveToken = refreshTokenService.saveRefreshToken(refreshToken);

        refreshTokenService.deleteRefreshToken(refreshToken.getValue());

        RefreshToken findToken = refreshTokenService.findRefreshToken(saveToken.getUserId());

        assertThat(findToken).isNull();
    }
}