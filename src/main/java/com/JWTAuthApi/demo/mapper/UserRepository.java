package com.JWTAuthApi.demo.mapper;

import com.JWTAuthApi.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    void saveUser(User user);

    User findByEmail(String email);

    void updateUser(Long userId, String username);
}
