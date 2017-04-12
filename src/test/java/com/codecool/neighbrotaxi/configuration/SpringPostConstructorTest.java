package com.codecool.neighbrotaxi.configuration;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.entities.Role;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import com.codecool.neighbrotaxi.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@Transactional
@MockBean(RoleRepository.class)
@MockBean(UserRepository.class)
@SpyBean(UserService.class)
public class SpringPostConstructorTest extends AbstractTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SpringPostConstructor springPostConstructor;

    @Before
    public void setUp() throws Exception {
        when(roleRepository.findByName("USER")).thenReturn(new Role());
        when(roleRepository.findByName("ADMIN")).thenReturn(new Role());
    }

    @Test
    public void fillUpDb_ThereIsNoAdminRoleInDb_SaveAdminRoleIntoRoleTable() throws Exception {
        when(roleRepository.findByName("ADMIN")).thenReturn(null);

        springPostConstructor.fillUpDb();

        ArgumentCaptor<Role> argumentCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository, atLeastOnce()).save(argumentCaptor.capture());
        assertEquals("ADMIN", argumentCaptor.getValue().getName());
    }

    @Test
    public void fillUpDb_ThereIsNoAdminRoleInDb_SaveUserRoleIntoRoleTable() throws Exception {
        when(roleRepository.findByName("USER")).thenReturn(null);

        springPostConstructor.fillUpDb();

        ArgumentCaptor<Role> argumentCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository, atLeastOnce()).save(argumentCaptor.capture());
        assertEquals("USER", argumentCaptor.getValue().getName());
    }

    @Test
    public void setupAdmin_SaveAdminWithValidUsername() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(roleRepository.findByName(any())).thenReturn(new Role());

        springPostConstructor.setupAdmin();

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, atLeastOnce()).save(argumentCaptor.capture());

        assertEquals("Valid username", "admin", argumentCaptor.getValue().getUsername());
    }

    @Test
    public void setupAdmin_SaveAdminWithValidRole() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        Role role = new Role();
        role.setName("ADMIN");
        when(roleRepository.findByName(any())).thenReturn(role);

        springPostConstructor.setupAdmin();

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().getRoles().size());
        for (Role givenRole: argumentCaptor.getValue().getRoles()){
            assertEquals("ADMIN", givenRole.getName());
        }
    }
}