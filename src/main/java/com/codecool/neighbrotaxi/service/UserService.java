package com.codecool.neighbrotaxi.service;

import com.codecool.neighbrotaxi.model.entities.User;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;


public interface UserService {
    void save(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    void login(HttpServletRequest request, User user) throws AuthenticationException;
    User findOne(Integer id);
    void logout(HttpServletRequest request);

    void update(User user);
}