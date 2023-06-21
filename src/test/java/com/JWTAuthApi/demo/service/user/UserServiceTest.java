package com.JWTAuthApi.demo.service.user;

import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.dto.login.UserSignupResponseDto;
import com.JWTAuthApi.demo.dto.user.UserUpdateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void findByEmail() {
        User user = User
                .builder()
                .email("email.com")
                .username("name")
                .password("password")
                .build();

        User findUser = userService.findByEmail(user.getEmail());

        assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void findById() {
        User findUser = userService.findById(70L);

        assertThat(findUser.getEmail()).isEqualTo("email.com");
    }

    @Test
    void currentUser() {
        UserSignupResponseDto userSignupResponseDto = userService.currentUser(70L);
        assertThat(userSignupResponseDto.getEmail()).isEqualTo("email.com");
    }
}