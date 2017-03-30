package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.enums.RoleEnum;
import com.codecool.neighbrotaxi.model.Role;
import com.codecool.neighbrotaxi.model.User;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import com.codecool.neighbrotaxi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * Gets all of the Role entry that are in the DB.
     * @return A list of Roles.
     */
    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    /**
     * Adds a new Role to the database.
     * @param role comes from the AdminController, which is a Role object,
     */
    @Override
    public void addRole(Role role) {
        roleRepository.save(role);
    }

    /**
     * Deletes a specified role based on their ID.
     * @param roleID The id of the role that needs to be deleted
     * @return returns true if the given ID is not ADMIN role, and false if it can't
     * be deleted.
     */
    @Override
    public boolean deleteRole(Integer roleID) {
        Role role = roleRepository.findOne(roleID);
        if (!role.getName().equals(RoleEnum.ADMIN.name()) && !role.getName().equals(RoleEnum.USER.name())) {
            roleRepository.delete(roleID);
            return true;
        }
        return false;
    }

    /**
     * Get a user based on the its ID.
     * @param userID comes from AdminController, and it is the ID of a given user.
     * @return User object.
     */
    @Override
    public User getUser(Integer userID) {
        return userRepository.findOne(userID);
    }

    /**
     * Sets roles for the specified user.
     * @param roles It a set, that contains all the roles that you want to set, as Objects.
     * @param userID ID of a user.
     */
    @Override
    public void addRoleToUser(Set<Role> roles, Integer userID) {
        User user = userRepository.findOne(userID);
        user.setRoles(roles);
        userRepository.save(user);
    }

    /**
     * Queries all the Users that have ADMIN set as a role.
     * @return A list of users that are ADMINS.
     */
    @Override
    public List<User> getAdminUser() {
        List<User> adminList = new ArrayList<>();
        for (User user : getAllUser()) {
            adminList.addAll(user.getRoles().stream().filter(role -> role.getName().equals(RoleEnum.ADMIN.name())).map(role -> user).collect(Collectors.toList()));
        }
         return adminList;
    }


}
