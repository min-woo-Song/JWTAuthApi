package com.JWTAuthApi.demo.security.jwt.provider;

import com.JWTAuthApi.demo.security.jwt.token.JwtAuthenticationToken;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import com.JWTAuthApi.demo.security.jwt.util.UserInfo;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenizer jwtTokenizer;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;

        // 전달받은 토큰을 검증한다. 기간이 만료되었는지, 토큰 문자열이 문제가 있는지 등
        Claims claims = jwtTokenizer.parseAccessToken(authenticationToken.getToken());

        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        String username = claims.get("username", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        UserInfo userInfo = UserInfo
                .builder()
                .userId(userId)
                .email(email)
                .username(username)
                .build();

        return new JwtAuthenticationToken(authorities, userInfo, null);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        log.info("***** getGrantedAuthorities= {} *****", claims.get("roleType").toString()); // ROLE_USER
        return Arrays.stream(new String[]{claims.get("roleType").toString()})
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
