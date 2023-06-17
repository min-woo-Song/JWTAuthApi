package com.JWTAuthApi.demo.service.user;

import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.exception.CustomException;
import com.JWTAuthApi.demo.mapper.UserRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
class UserServiceTest {

    private final UserRepository userRepository;

    @Test
    void findByEmail() {
        User user = User
                .builder()
                .email("abc@abc.com")
                .username("abc")
                .password("aaa")
                .build();

        userRepository.findByEmail(user.getEmail());
    }

    @Test
    void findById() {
    }

    @Test
    void saveUser() {
    }

    @Test
    void login() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void currentUser() {
    }
}