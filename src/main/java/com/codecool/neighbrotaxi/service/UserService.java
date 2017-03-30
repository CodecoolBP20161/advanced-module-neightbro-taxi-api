package com.codecool.neighbrotaxi.service;

import com.codecool.neighbrotaxi.model.User;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;


public interface UserService {
    void save(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    void login(HttpServletRequest request, User user) throws AuthenticationException;
    User findOne(Integer id);
    void logout(HttpServletRequest request);

    void update(User user) throws SQLIntegrityConstraintViolationException;
}