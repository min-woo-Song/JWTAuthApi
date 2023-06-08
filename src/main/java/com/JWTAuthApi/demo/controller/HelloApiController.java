package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HelloApiController {
    private final JwtTokenizer jwtTokenizer;



    @GetMapping("/hello")
    public String hello(@RequestHeader("Authorization") String token) {
        Long userIdFromToken = jwtTokenizer.getUserIdFromToken(token);
        log.info("token={}", userIdFromToken);
        return "hello " + userIdFromToken;
    }

    @GetMapping("/test")
    public ResponseEntity<Object> test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<Object>(authentication, HttpStatus.OK);
    }
}
