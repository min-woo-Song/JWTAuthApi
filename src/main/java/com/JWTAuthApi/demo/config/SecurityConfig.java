package com.JWTAuthApi.demo.config;

import com.JWTAuthApi.demo.security.auth.util.OAuth2AuthenticationFailureHandler;
import com.JWTAuthApi.demo.service.login.CustomOAuth2UserService;
import com.JWTAuthApi.demo.security.auth.util.OAuth2AuthenticationSuccessHandler;
import com.JWTAuthApi.demo.security.auth.util.CookieAuthorizationRequestRepository;
import com.JWTAuthApi.demo.security.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = false)
public class SecurityConfig {

    private final AuthenticationManagerConfig authenticationManagerConfig;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomOAuth2UserService customOAuth2UserService;
    
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .formLogin().disable()  // email, password를 입력받아서 JWT토큰을 리턴하는 API를 직접 만드므로 form 비활성화.
                .csrf().disable()       // rest api를 이용한 서버는 stateless 하기 때문에 인증정보를 보관하지 않으므로 사용하지 않는다.
                .cors()                 // cors 활성화. WebConfig cors 설정
            .and()
                .apply(authenticationManagerConfig) // 구현한 Jwt Filter, Manager, Provider 관련 설정 등록
            .and()
                .httpBasic().disable()  // http basic 기반 로그인 인증 창 비활성화
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // 예비 요청 Preflight 허용
                    .antMatchers( "/", "/users", "/users/login", "/user/refreshToken").permitAll()
                    .antMatchers(GET,"/user").hasAnyRole("USER", "ADMIN")
                    .antMatchers(PUT,"/user").hasAnyRole("USER", "ADMIN")
                    .anyRequest().hasAnyRole("USER", "ADMIN")
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)   // 예외 처리 EntryPoint 등록
            .and()
                .oauth2Login()  // OAuth2 설정
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)   // OAuth2 유저정보를 처리할 Service 등록
            .and()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository)   // redirect를 관리 할 repository
            .and()
                .successHandler(oAuth2AuthenticationSuccessHandler) // oauth2 인증 성공 시 동작하는 handler
                .failureHandler(oAuth2AuthenticationFailureHandler) // oauth2 인증 실패 시 동작하는 handler
            .and()
                .build();
    }
}
