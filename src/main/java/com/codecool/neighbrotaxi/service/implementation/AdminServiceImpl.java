package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.enums.RoleEnum;
import com.codecool.neighbrotaxi.model.Role;
import com.codecool.neighbrotaxi.model.User;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import com.codecool.neighbrotaxi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Return all User object parsed from the database.
     * @return A list of users.
     */
    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    /**
     * Delete a specified user based on their ID.
     * @param userID The Id of the user who we want to delete.
     */
    @Override
    public void deleteUser(Integer userID) {
        userRepository.delete(userID);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public void addRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public boolean deleteRole(Integer roleID) {
        Role role = roleRepository.findOne(roleID);
        if (!role.getName().equals(RoleEnum.ADMIN.name()) && !role.getName().equals(RoleEnum.USER.name())) {
            roleRepository.delete(roleID);
            return true;
        }
        return false;
    }

    @Override
    public User getUser(Integer userID) {
        return userRepository.findOne(userID);
    }

    @Override
    public void addRoleToUser(Set<Role> roles, Integer userID) {
        User user = userRepository.findOne(userID);
        user.setRoles(roles);
        userRepository.save(user);
    }

}
