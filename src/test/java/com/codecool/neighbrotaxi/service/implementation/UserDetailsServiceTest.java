package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.Role;
import com.codecool.neighbrotaxi.model.User;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@Transactional
public class UserDetailsServiceTest extends AbstractTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    private User user;

    @Before
    public void setUp() throws Exception {
        userRepository.deleteAll();
        user = new User();
        user.setEmail("email@email.com");
        user.setUsername("email@email.com");
        user.setPassword("password");
        user.setName("name");

        Role role = new Role();
        role.setName("ROLE");
        roleRepository.save(role);
        user.setRoles(new HashSet<>(Arrays.asList(role)));

        userRepository.save(user);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_ThereIsNoUserWithTheGivenUsername_ShouldThrowUsernameNotFoundException() throws Exception {

        userDetailsService.loadUserByUsername("noUser");
    }

    @Test
    public void loadUserByUsername_ThereIsValidUserWithTheGivenUsername_ShouldReturnValidUserObject() throws Exception {

        org.springframework.security.core.userdetails.User returnedUser =
                (org.springframework.security.core.userdetails.User) userDetailsService.loadUserByUsername(user.getUsername());

        assertEquals(returnedUser.getUsername(), user.getUsername());
    }
}