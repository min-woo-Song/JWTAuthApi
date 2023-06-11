package com.JWTAuthApi.demo.config;

import com.JWTAuthApi.demo.security.jwt.util.IfLoginArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 프론트에서 사용하는 3000번대 포트 http://localhost:3000 ---> 8080 api를 호출할 수 있도록 설정.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new IfLoginArgumentResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")    // 외부에서 들어오는 경로 http://localhost:3000 허용
                .allowedMethods("GET", "POST", "PATCH", "PUT", "OPTIONS")
                .allowCredentials(true);
    }
}
