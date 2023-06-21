package com.JWTAuthApi.demo.security.auth.util;

import com.JWTAuthApi.demo.domain.RefreshToken;
import com.JWTAuthApi.demo.mapper.RefreshTokenRepository;
import com.JWTAuthApi.demo.security.auth.userInfo.UserPrincipal;
import com.JWTAuthApi.demo.security.auth.util.CookieAuthorizationRequestRepository;
import com.JWTAuthApi.demo.security.auth.util.CookieUtils;
import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.JWTAuthApi.demo.security.auth.util.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.authorizedRedirectUris}")
    private String propertiesUrl;
    private final JwtTokenizer jwtTokenizer;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed.");
            return;
        }

        // URI 쿠키 삭제
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(
                request,
                response,
                targetUrl
        );
    }

    // 사용자 인증 정보를 통해 jwt token을 생성한다. 최초 oauth 인증 요청시 받았던 redirect uri를 검증하고 해당 uri로 토큰을 넘겨준다.
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 쿠키에 저장된 Redirect URI 획득
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("redirect URIs are not matched.");
        }

        // 없을 시 "/"
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUserId();
        String email = userPrincipal.getEmail();
        String name = userPrincipal.getUsername();
        String role = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //JWT 생성
        String accessToken = jwtTokenizer.createAccessToken(userId, email, name, role);
        String refreshToken = jwtTokenizer.createRefreshToken(userId, email, name, role);

        RefreshToken RToken = new RefreshToken(userId, refreshToken);

        RefreshToken findToken = refreshTokenRepository.findRefreshToken(userId);
        // RefreshToken이 이미 있으면 해당 userId의 토큰을 업데이트 한다
        if (findToken != null){
            refreshTokenRepository.updateRefreshToken(userId, refreshToken);
            // RefreshToken 첫 생성시 save
        } else {
            refreshTokenRepository.saveRefreshToken(RToken);
        }

        return UriComponentsBuilder
                .fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(propertiesUrl);

        return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort();
    }
}
