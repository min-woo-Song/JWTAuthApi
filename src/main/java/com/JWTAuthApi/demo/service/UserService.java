package com.JWTAuthApi.demo.service;

import com.JWTAuthApi.demo.domain.User;
import com.JWTAuthApi.demo.mapper.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveUser(User user) {
        userRepository.saveUser(user);
        return user;
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public void updateUser(Long userId, String username) {
        userRepository.updateUser(userId, username);
    }

    @Transactional
    public void updateUserPassword(Long userId, String password) {
        userRepository.updateUserPassword(userId, password);
    }
}
