package com.JWTAuthApi.demo.config;

import com.JWTAuthApi.demo.security.jwt.filter.JwtAuthenticationFilter;
import com.JWTAuthApi.demo.security.jwt.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    // UsernamePasswordAuthenticationFilter 대신 JwtAuthenticationFilter 가 먼저 동작하게 설정하고 jwtAuthenticationProvider를 넣어준다.
    // JwtAuthenticationFilter 의 authenticate()는 넣어준 jwtAuthenticationProvider로 동작한다.
    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        builder.addFilterBefore(
                        new JwtAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(jwtAuthenticationProvider);
    }
}