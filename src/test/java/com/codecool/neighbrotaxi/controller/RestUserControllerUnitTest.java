package com.codecool.neighbrotaxi.controller;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.SerializableSessionStorage;
import com.codecool.neighbrotaxi.model.SessionStorage;
import com.codecool.neighbrotaxi.model.User;
import com.codecool.neighbrotaxi.service.SecurityService;
import com.codecool.neighbrotaxi.service.UserService;
import com.codecool.neighbrotaxi.utils.TestUtil;
import com.codecool.neighbrotaxi.validator.UserValidator;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@MockBean(SecurityService.class)
@MockBean(HttpServletRequest.class)
@MockBean(SessionStorage.class)
@MockBean(UserService.class)
@MockBean(UserValidator.class)
@MockBean(SessionStorage.class)
@MockBean(classes = {SerializableSessionStorage.class, BindingResult.class})
public class RestUserControllerUnitTest extends AbstractTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User user;

    @Autowired
    private RestUserController restUserController;

    @Autowired
    private BindingResult bindingResult;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SessionStorage sessionStorage;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setName("name");
        user.setPassword("pw");
        user.setEmail("email");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // Registration route tests
    @Test
    public void registration_HasErrorsInBindingResult_ReturnListOfErrors() throws Exception {
        ArrayList<ObjectError> listOfErrors = new ArrayList<>();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(listOfErrors);

        Object object = restUserController.registration(user, bindingResult);

        assertEquals(listOfErrors, object);
    }

    @Test
    public void registration_HasNoErrorsInBindingResult_CallSaveUser() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);

        restUserController.registration(user, bindingResult);

        verify(userService).save(user);
    }

    @Test
    public void registration_HasNoErrorsInBindingResult_ReturnSavedUser() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findOne(user.getId())).thenReturn(user);

        User returnedUser = (User) restUserController.registration(user, bindingResult);

        assertEquals(user, returnedUser);
    }

    @Test
    public void registration_CallValidate() throws Exception {

        restUserController.registration(user, bindingResult);

        verify(userValidator).validate(user, bindingResult);
    }

    // Login Route Tests
    @Test
    public void loggedInUser_ReturnValidUser() throws Exception {
        when(sessionStorage.getLoggedInUser()).thenReturn(user);

        Object object = restUserController.loggedInUser();

        assertEquals(user, (User) object);
    }

    @Test
    public void userLogin_AlreadyLoggedIn_ShouldAddErrorMessageToTheSessionStorage() throws Exception {
        user.setUsername(user.getEmail());
        when(securityService.findLoggedInUsername()).thenReturn(user.getUsername());
        HttpServletRequest request = mock(HttpServletRequest.class);

        restUserController.userLogin(user, request);

        verify(sessionStorage, times(1)).addErrorMessage(anyString());
    }

    @Test
    public void userLogin_AlreadyLoggedIn_ShouldReturnSerializableSessionStorageObject() throws Exception {
        user.setUsername(user.getEmail());
        when(securityService.findLoggedInUsername()).thenReturn(user.getUsername());
        HttpServletRequest request = mock(HttpServletRequest.class);

        Object returnedObject = restUserController.userLogin(user, request);

        assertEquals(SerializableSessionStorage.class, returnedObject.getClass());
    }

    @Test
    public void userLogin_AlreadyLoggedIn_ShouldAddValidErrorMessages() throws Exception {
        user.setUsername(user.getEmail());
        when(securityService.findLoggedInUsername()).thenReturn(user.getUsername());
        HttpServletRequest request = mock(HttpServletRequest.class);

        restUserController.userLogin(user, request);

        verify(sessionStorage, times(1)).addErrorMessage("Already logged in.");
    }

    @Test
    public void userLogin_InvalidUsernameOrPassword_ShouldAddErrorMessageToTheSessionStorage() throws Exception {
        when(securityService.findLoggedInUsername()).thenReturn("anonymous");
        HttpServletRequest request = mock(HttpServletRequest.class);
        doThrow(new BadCredentialsException("invalidCredentials")).when(userService).login(request, user);

        restUserController.userLogin(user, request);

        verify(sessionStorage, times(1)).addErrorMessage(anyString());
    }

    @Test
    public void userLogin_InvalidUsernameOrPassword_ShouldReturnSerializableSessionStorageObject() throws Exception {
        when(securityService.findLoggedInUsername()).thenReturn("anonymous");
        HttpServletRequest request = mock(HttpServletRequest.class);
        doThrow(new BadCredentialsException("invalidCredentials")).when(userService).login(request, user);

        Object returnedObject = restUserController.userLogin(user, request);

        assertEquals(SerializableSessionStorage.class, returnedObject.getClass());
    }

    @Test
    public void userLogin_InvalidUsernameOrPassword_ShouldAddValidErrorMessage() throws Exception {
        when(securityService.findLoggedInUsername()).thenReturn("anonymous");
        HttpServletRequest request = mock(HttpServletRequest.class);
        doThrow(new BadCredentialsException("invalidCredentials")).when(userService).login(request, user);

        restUserController.userLogin(user, request);

        verify(sessionStorage, times(1)).addErrorMessage("Invalid username or password!");
    }

    @Test
    public void userLogin_SuccessfulAuth_ShouldCallSessionStorageLoggedInUserSetter() throws Exception {
        when(securityService.findLoggedInUsername()).thenReturn("anonymous");
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(userService).login(request, user);
        when(userService.findByUsername(anyString())).thenReturn(user);

        restUserController.userLogin(user, request);

        verify(sessionStorage, times(1)).setLoggedInUser(user);
    }

    @Test
    public void userLogin_SuccessfulAuth_ShouldAddInfoMessageToTheSessionStorage() throws Exception {
        when(securityService.findLoggedInUsername()).thenReturn("anonymous");
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(userService).login(request, user);
        when(userService.findByUsername(anyString())).thenReturn(user);

        restUserController.userLogin(user, request);

        verify(sessionStorage, times(1)).addInfoMessage(anyString());
    }

    @Test
    public void userLogin_SuccessfulAuth_ShouldAddErrorMessageToTheSessionStorage() throws Exception {
        when(securityService.findLoggedInUsername()).thenReturn("anonymous");
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(userService).login(request, user);
        when(userService.findByUsername(anyString())).thenReturn(user);

        Object returnedObject = restUserController.userLogin(user, request);

        assertEquals(SerializableSessionStorage.class, returnedObject.getClass());
    }

    @Test
    public void userLogin_SuccessfulAuth_ShouldAddValidErrorMessage() throws Exception {
        when(securityService.findLoggedInUsername()).thenReturn("anonymous");
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(userService).login(request, user);
        when(userService.findByUsername(anyString())).thenReturn(user);

        restUserController.userLogin(user, request);

        verify(sessionStorage, times(1)).addInfoMessage("Successfully logged in!");
    }

    @Test
    public void userLogout_SuccessfulLogout_ShouldAddValidInfoMessage() throws Exception {
        when(sessionStorage.getLoggedInUser()).thenReturn(user);
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(userService).logout(request);

        restUserController.userLogout(request);

        verify(sessionStorage, times(1)).addInfoMessage("You have been logged out successfully.");
    }

    @Test
    public void userLogout_ShouldCallLogoutMethod() throws Exception {
        when(sessionStorage.getLoggedInUser()).thenReturn(user);
        HttpServletRequest request = mock(HttpServletRequest.class);

        restUserController.userLogout(request);

        verify(userService, times(1)).logout(request);
    }

    @Test
    public void userLogout_ThereIsNoLoggedInUser_ShouldAddValidErrorMessage() throws Exception {
        user.setUsername("anonymous");
        when(sessionStorage.getLoggedInUser()).thenReturn(user);
        HttpServletRequest request = mock(HttpServletRequest.class);

        restUserController.userLogout(request);

        verify(sessionStorage, times(1)).addErrorMessage("There's no logged in user!");
    }

    @Test
    public void userLogout_ShouldReturnSerializableSessionStorageObject() throws Exception {
        when(sessionStorage.getLoggedInUser()).thenReturn(user);
        HttpServletRequest request = mock(HttpServletRequest.class);

        Object returnedObject = restUserController.userLogout(request);

        assertEquals(SerializableSessionStorage.class, returnedObject.getClass());
    }

    @Test
    public void userLogout_ShouldReturnSerializableSessionStorageObjectWithValidFields() throws Exception {
        when(sessionStorage.getLoggedInUser()).thenReturn(user);
        List<String> infoMessages = new ArrayList<>(Arrays.asList("You have been logged out successfully."));
        when(sessionStorage.getInfoMessages()).thenReturn(infoMessages);
        HttpServletRequest request = mock(HttpServletRequest.class);

        SerializableSessionStorage returnedObject = restUserController.userLogout(request);

        assertEquals("loggedInUser Field", user, returnedObject.getLoggedInUser());
        assertEquals("infoMessages field", infoMessages, returnedObject.getInfoMessages());
    }

    @Test
    public void userLogout_ThereIsNoLoggedInUser_ShouldReturnSerializableSessionStorageObjectWithValidFields() throws Exception {
        when(sessionStorage.getLoggedInUser()).thenReturn(user);
        List<String> errorMessages = new ArrayList<>(Arrays.asList("There's no logged in user!"));
        when(sessionStorage.getErrorMessages()).thenReturn(errorMessages);
        HttpServletRequest request = mock(HttpServletRequest.class);

        SerializableSessionStorage returnedObject = restUserController.userLogout(request);

        assertEquals("loggedInUser Field", user, returnedObject.getLoggedInUser());
        assertEquals("infoMessages field", errorMessages, returnedObject.getErrorMessages());
    }

    @Test
    public void userUpdate_errorInBindingResult_neverUpdatesUser() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(Arrays.asList(new ObjectError("error", "error"))));


        mockMvc.perform(put("/update-user")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(userService, never()).update(any());
    }

    @Test
    public void userUpdate_errorInBindingResult_ReturnsErrors() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);
        ObjectError error = new ObjectError("error", "error");
        ArrayList list = new ArrayList<>(Arrays.asList(error));
        when(bindingResult.getAllErrors()).thenReturn(list);

        Object object = restUserController.updateUser(user, bindingResult);

        assertEquals(list, object);
    }

    @Test
    public void userUpdate_doesntUpdateId() throws Exception {
        User userToUpdate = user;
        userToUpdate.setId(2);
        user.setId(1);
        when(sessionStorage.getLoggedInUser()).thenReturn(user);

        restUserController.updateUser(userToUpdate, bindingResult);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, atLeastOnce()).update(argumentCaptor.capture());
        assertEquals(user.getId(), argumentCaptor.getValue().getId());
    }

    @Test
    public void userUpdate_doesntUpdatePassword() throws Exception {
        User userToUpdate = user;
        userToUpdate.setPassword("new");
        user.setPassword("old");
        when(sessionStorage.getLoggedInUser()).thenReturn(user);

        restUserController.updateUser(userToUpdate, bindingResult);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, atLeastOnce()).update(argumentCaptor.capture());
        assertEquals(user.getPassword(), argumentCaptor.getValue().getPassword());
    }

    @Test
    public void userUpdate_AddInfoMessageAfterUpdate() throws Exception {
        when(sessionStorage.getLoggedInUser()).thenReturn(user);

        restUserController.updateUser(user, bindingResult);

        verify(sessionStorage, atLeastOnce()).addInfoMessage("User updated");
    }

    @Test
    public void userUpdate_ReturnInfoMassages() throws Exception {
        ArrayList list = new ArrayList(Arrays.asList("error"));
        user.setEmail("email@email.com");
        when(sessionStorage.getInfoMessages()).thenReturn(list);
        when(sessionStorage.getLoggedInUser()).thenReturn(user);

        mockMvc.perform(put("/update-user")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0]", containsString("error")));
    }
}