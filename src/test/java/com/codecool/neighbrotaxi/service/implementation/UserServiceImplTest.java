package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.enums.RoleEnum;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.sql.SQLIntegrityConstraintViolationException;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpyBean(BCryptPasswordEncoder.class)
@MockBean(SecurityContext.class)
@MockBean(AuthenticationManager.class)
@MockBean(SecurityContextHolder.class)
@MockBean(UserRepository.class)
public class UserServiceImplTest extends AbstractTest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecurityContext securityContext;

    @Autowired
    private AuthenticationManager authenticationManager;

    private User user;
    private HttpServletRequest request;
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        userRepository.deleteAll();
        user = new User();
        user.setEmail("email@email.com");
        user.setUsername("email@email.com");
        user.setPassword("password");
        user.setName("name");

        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);

    }

    @Test
    public void findByEmail() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        User user = userService.findByEmail("email@email.com");

        assertEquals(this.user.getId(), user.getId());
    }

    @Test
    public void findByEmail_NoUserFound_ReturnNull() throws Exception {
        userRepository.save(user);

        User user = userService.findByEmail("notEmail");

        assertNull(user);

    }

    @Test
    public void save_PasswordHashingCalled() throws Exception {

        userService.save(user);

        verify(bCryptPasswordEncoder).encode("password");
    }

    @Test
    public void save_PasswordHashed() throws Exception {

        userService.save(user);

        assertNotEquals("password", user.getPassword());
    }

    @Test
    public void save_RoleUser() throws Exception {

        userService.save(user);

        assertEquals(RoleEnum.USER.name(), user.getRoles().stream().findFirst().get().getName());
    }

    @Test
    public void save_OnlyRoleUser() throws Exception {

        userService.save(user);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void save_UserSavedIntoDB() throws Exception {

        userService.save(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void findByUsername_ShouldReturnUserObjectWithValidMethodCall() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        User returnedUser = userService.findByUsername(user.getUsername());

        assertEquals(user.getUsername(), returnedUser.getUsername());
    }

    @Test
    public void findOne_ShouldReturnUserObjectWithValidMethodCall() throws Exception {
        when(userRepository.findOne(user.getId())).thenReturn(user);

        User returnedUser = userService.findOne(user.getId());

        assertEquals(user.getId(), returnedUser.getId());
    }

    @Test
    public void logout_CallSessionStorageDefaultMethod() throws Exception {

        userService.logout(request);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SecurityContext> securityContextArgumentCaptor = ArgumentCaptor.forClass(SecurityContext.class);

        verify(session, atLeastOnce())
                .setAttribute(stringArgumentCaptor.capture(), securityContextArgumentCaptor.capture());
        assertEquals("SPRING_SECURITY_CONTEXT", stringArgumentCaptor.getValue());
        assertNull(securityContextArgumentCaptor.getValue().getAuthentication());    }

    @Test
    public void login_CallAuthenticationManagerAuthentication() throws Exception {

        userService.login(request, user);

        verify(authenticationManager, atLeastOnce()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void login_SetSpringSecurityAttributeWithValidParameters() throws Exception {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SecurityContext> securityContextArgumentCaptor = ArgumentCaptor.forClass(SecurityContext.class);

        userService.login(request, user);

        verify(session, atLeastOnce())
                .setAttribute(stringArgumentCaptor.capture(), securityContextArgumentCaptor.capture());
        assertEquals("SPRING_SECURITY_CONTEXT", stringArgumentCaptor.getValue());
        assertEquals(SecurityContextImpl.class, securityContextArgumentCaptor.getValue().getClass());
    }

    @Test
    public void update_callsSaveMethod() throws Exception {

        userService.update(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void update_duplicateEmailAddress_callsSaveMethodThrowsExpectedException() throws Exception {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        userService.update(user);
    }

    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void update_callsSaveMethodNOt() throws Exception {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        userService.update(user);
        verify(userRepository, never()).save(user);
    }
}