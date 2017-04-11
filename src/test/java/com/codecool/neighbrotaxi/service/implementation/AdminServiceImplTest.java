package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.model.entities.Role;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;


@Transactional
@MockBean(classes = {UserRepository.class, RoleRepository.class})
public class AdminServiceImplTest extends AbstractTest {
    @Autowired
    private AdminServiceImpl adminService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    private User user;
    private Role role;
    
    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setName("name");

        role = new Role();
        role.setName("newRole");
        when(roleRepository.findOne(anyInt())).thenReturn(role);
    }

    @Test
    public void getAllUser_callUserRepositoryFindAll() throws Exception {
        adminService.getAllUser();

        verify(userRepository, atLeastOnce()).findAll();
    }

    @Test
    public void getAllUser_returnValidList() throws Exception {
        List<User> users = new ArrayList<User>(Arrays.asList(user));
        when(userRepository.findAll()).thenReturn(users);

        List<User> returnedUsers = adminService.getAllUser();

        assertEquals(users, returnedUsers);
    }

    @Test
    public void deleteUser_callUserRepositoryDeleteMethod() throws Exception {

        adminService.deleteUser(anyInt());

        verify(userRepository, atLeastOnce()).delete(anyInt());
    }

    @Test
    public void deleteUser_callUserRepositoryDeleteMethod_ValidIdGivenToThisMethod() throws Exception {
        user.setId(1);

        adminService.deleteUser(1);

        verify(userRepository, atLeastOnce()).delete(1);
    }

    @Test
    public void getAllRole_ReturnsValidList() throws Exception {
        ArrayList<Role> roleList = new ArrayList<>();
        when(roleRepository.findAll()).thenReturn(roleList);

        Object returnedObject = adminService.getAllRole();

        assertEquals(roleList, returnedObject);
    }

    @Test
    public void addRole_SavingTheGivenRole() throws Exception {

        adminService.addRole(role);

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void deleteRole_DoesNotDeleteAdminRole_ReturnsFalse() throws Exception {
        role.setName("ADMIN");

        boolean returnedValue = adminService.deleteRole(1);

        verify(roleRepository, never()).delete(anyInt());
        assertFalse(returnedValue);
    }

    @Test
    public void deleteRole_DoesNotDeleteUserRole_ReturnsFalse() throws Exception {
        role.setName("USER");

        boolean returnedValue = adminService.deleteRole(1);

        verify(roleRepository, never()).delete(anyInt());
        assertFalse(returnedValue);
    }

    @Test
    public void deleteRole_DeletableRole_CallDeleteMethodAndReturnTrue() throws Exception {

        boolean returnedValue = adminService.deleteRole(1);

        verify(roleRepository, times(1)).delete(anyInt());
        assertTrue(returnedValue);
    }
}