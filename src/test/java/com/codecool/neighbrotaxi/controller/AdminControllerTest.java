package com.codecool.neighbrotaxi.controller;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.entities.Role;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.service.AdminService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@Transactional
@MockBean(Model.class)
@MockBean(AdminService.class)
public class AdminControllerTest extends AbstractTest {
    @Autowired
    private AdminController adminController;

    @Autowired
    private AdminService adminService;

    @Autowired
    private Model model;

    private User user;

    private Role role;

    private List<String> id;


    @Before
    public void setUp() throws Exception {
        role = new Role();
        role.setName("PREMIUM");

        user = new User();
        user.setName("name");
        user.setPassword("pw");
        user.setEmail("email@email.com");

        id = new ArrayList<>();
    }

    @Test
    public void getAllUsers_AddCorrectUsersIntoModel() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(adminService.getAllUser()).thenReturn(users);


        adminController.getAllUsers(model);

        verify(model, atLeastOnce()).addAttribute("user_list", users);
    }

    @Test
    public void deleteUser_CallDeleteUserFromUserService() throws Exception {
        user.setId(1);

        adminController.deleteUser("1");

        verify(adminService, times(1)).deleteUser(1);
    }

    @Test
    public void addRole_CallAddRoleFromUserService() throws Exception {

        adminController.addRole("Premium");

        verify(adminService, times(1)).addRole(any(Role.class));
    }

    @Test
    public void addRole_SaveToDatabase() throws Exception {
        role.setId(1);

        adminService.addRole(role);

        verify(adminService, times(1)).addRole(role);
    }

    @Test
    public void addRole_IfExistsThenItWillNotBeSaved() throws Exception {
        when(adminService.getAllRole()).thenReturn(new ArrayList<Role>(Arrays.asList(role)));

        adminController.addRole("Premium");

        verify(adminService, times(0)).addRole(any());
    }

    @Test
    public void getAllRoles_AddCorrectUsersIntoModel() throws Exception {
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        when(adminService.getAllRole()).thenReturn(roles);

        adminController.getAllRoles(model);

        verify(model, atLeastOnce()).addAttribute("role_list", roles);
    }

}