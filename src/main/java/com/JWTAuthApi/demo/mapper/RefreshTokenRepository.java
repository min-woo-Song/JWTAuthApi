package com.JWTAuthApi.demo.mapper;

import com.JWTAuthApi.demo.domain.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenRepository {

    void saveRefreshToken(RefreshToken refreshToken);

    RefreshToken findRefreshToken(Long refreshToken);

    void updateRefreshToken(@Param("userId") Long userId, @Param("value") String value);

    void deleteRefreshToken(String refreshToken);
}
