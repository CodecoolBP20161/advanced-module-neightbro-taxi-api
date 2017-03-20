package com.codecool.neighbrotaxi.service;


import com.codecool.neighbrotaxi.model.Role;
import com.codecool.neighbrotaxi.model.User;

import java.util.List;
import java.util.Set;

public interface AdminService {
    List<User> getAllUser();
    List<Role> getAllRole();
    void addRole(Role role);
    void deleteUser(Integer userID);
    boolean deleteRole(Integer roleID);
    User getUser(Integer userID);
    void addRoleToUser(Set<Role> roles, Integer userID);
}
