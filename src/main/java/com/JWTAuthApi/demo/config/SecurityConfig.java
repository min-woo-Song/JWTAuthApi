package com.JWTAuthApi.demo.config;

import com.JWTAuthApi.demo.service.user.CustomOAuth2UserService;
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

    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .formLogin().disable() // 직접 id, password를 입력받아서 JWT토큰을 리턴하는 API를 직접 만든다.
                .csrf().disable()
                .cors()
            .and()
                .apply(authenticationManagerConfig)
            .and()
                .httpBasic().disable()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // Preflight 허용
                    .antMatchers( "/", "/users", "/users/login", "/user/refreshToken").permitAll()
                    .antMatchers("/test2").hasRole("USER")
                    .antMatchers(GET,"/user").hasAnyRole("USER", "ADMIN")
                    .antMatchers(PUT,"/user").hasAnyRole("USER", "ADMIN")
                    .anyRequest().hasAnyRole("USER", "ADMIN")
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
                .oauth2Login()
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)
            .and()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository)
            .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
            .and()
                .build();
    }
}
