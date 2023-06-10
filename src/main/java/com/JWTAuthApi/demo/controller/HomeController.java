package com.JWTAuthApi.demo.controller;

import com.JWTAuthApi.demo.security.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final JwtTokenizer jwtTokenizer;


}
