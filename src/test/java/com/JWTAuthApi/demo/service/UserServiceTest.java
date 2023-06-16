/*
package com.JWTAuthApi.demo.service;

import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;


    @Test
    void save() {
        User user = User.builder()
                .email("abc@abc.com")
                .password("1234")
                .username("name")
                .build();

        userService.saveUser(user);
        User findUser = userService.findByEmail("abc@abc.com");

        assertThat(findUser.getUsername()).isEqualTo("name");
    }

    @Test
    void update() {
        User user = User.builder()
                .email("abc@abc.com")
                .password("1234")
                .username("name")
                .build();
        User saveUser = userService.saveUser(user);

        String username = "change";
        userService.updateUser(saveUser.getUserId(), username);

        User findUser = userService.findByEmail("abc@abc.com");
        assertThat(findUser.getUsername()).isEqualTo(username);
    }
}*/
