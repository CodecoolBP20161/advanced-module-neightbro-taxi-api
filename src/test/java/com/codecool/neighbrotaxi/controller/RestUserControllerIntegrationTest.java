package com.codecool.neighbrotaxi.controller;

import com.codecool.neighbrotaxi.NeighBroTaxiApplicationTests;
import com.codecool.neighbrotaxi.model.SessionStorage;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.repository.UserRepository;
import com.codecool.neighbrotaxi.service.UserService;
import com.codecool.neighbrotaxi.service.implementation.SecurityServiceImpl;
import com.codecool.neighbrotaxi.utils.TestUtil;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpyBean(UserService.class)
@SpyBean(SessionStorage.class)
@MockBean(SecurityServiceImpl.class)
@Transactional
public class RestUserControllerIntegrationTest extends NeighBroTaxiApplicationTests{
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private UserService userServiceMock;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionStorage sessionStorageMock = new SessionStorage();

    @Autowired
    private SecurityServiceImpl securityServiceImplMock;

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setEmail("email@email.com");
        user.setName("name");
        user.setPassword("password");
        user.setPasswordConfirm("password");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    // "/registration" route tests
    @Test
    public void registration_InvalidEmail_ShouldReturnValidErrorMessage() throws Exception {
        user.setEmail("invalid email");

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("[*].defaultMessage", contains(new JSONArray(new ArrayList(Arrays.asList("not a well-formed email address"))).get(0))))
        .andExpect(jsonPath("[*].objectName", contains(new JSONArray(new ArrayList(Arrays.asList("user"))).get(0))))
        .andExpect(jsonPath("[*].field", contains(new JSONArray(new ArrayList(Arrays.asList("email"))).get(0))));

        verify(userServiceMock, times(0)).save(any());
    }

    @Test
    public void registration_EmailAlreadyExistsInDb_ShouldReturnValidErrorMessage() throws Exception {
        userRepository.save(user);

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("[*].defaultMessage", contains(new JSONArray(new ArrayList(Arrays.asList("Email already in database"))).get(0))))
                .andExpect(jsonPath("[*].objectName", contains(new JSONArray(new ArrayList(Arrays.asList("user"))).get(0))))
                .andExpect(jsonPath("[*].field", contains(new JSONArray(new ArrayList(Arrays.asList("email"))).get(0))));

        verify(userServiceMock, times(0)).save(any());
    }

    @Test
    public void registration_PasswordDontMatch_ShouldReturnValidErrorMessage() throws Exception {
        user.setPasswordConfirm("notValidConfirm");

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("[*].defaultMessage", contains(new JSONArray(new ArrayList(Arrays.asList("The passwords do not match"))).get(0))))
                .andExpect(jsonPath("[*].objectName", contains(new JSONArray(new ArrayList(Arrays.asList("user"))).get(0))))
                .andExpect(jsonPath("[*].field", contains(new JSONArray(new ArrayList(Arrays.asList("passwordConfirm"))).get(0))));

        verify(userServiceMock, times(0)).save(any());
    }

    @Test
    public void registration_PasswordIsTooShort_ShouldReturnValidErrorMessage() throws Exception {
        user.setPassword("short");
        user.setPasswordConfirm(user.getPassword());

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("[*].defaultMessage", contains(new JSONArray(new ArrayList(Arrays.asList("The size of the password is incorrect"))).get(0))))
                .andExpect(jsonPath("[*].objectName", contains(new JSONArray(new ArrayList(Arrays.asList("user"))).get(0))))
                .andExpect(jsonPath("[*].field", contains(new JSONArray(new ArrayList(Arrays.asList("password"))).get(0))));

        verify(userServiceMock, times(0)).save(any());
    }

    @Test
    public void registration_PasswordIsTooLong_ShouldReturnValidErrorMessage() throws Exception {
        user.setPassword("ThisPasswordIsLargerThan32Character");
        user.setPasswordConfirm(user.getPassword());

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("[*].defaultMessage", contains(new JSONArray(new ArrayList(Arrays.asList("The size of the password is incorrect"))).get(0))))
                .andExpect(jsonPath("[*].objectName", contains(new JSONArray(new ArrayList(Arrays.asList("user"))).get(0))))
                .andExpect(jsonPath("[*].field", contains(new JSONArray(new ArrayList(Arrays.asList("password"))).get(0))));

        verify(userServiceMock, times(0)).save(any());
    }

    @Test
    public void registration_MoreThanOneFailedValidation_ShouldReturnValidErrorMessageWithAllFailures() throws Exception {
        user.setPassword("ThisPasswordIsLargerThan32Character");
        user.setPasswordConfirm(user.getPassword());
        user.setEmail("invalid email");
        JSONArray defaultMessageForInvalidEmail = new JSONArray(new ArrayList(Arrays.asList("not a well-formed email address")));
        JSONArray defaultMessageForLongPassword = new JSONArray(new ArrayList(Arrays.asList("The size of the password is incorrect")));
        JSONArray objectName = new JSONArray(new ArrayList(Arrays.asList("user")));
        JSONArray fieldForInvalidEmail = new JSONArray(new ArrayList(Arrays.asList("email")));
        JSONArray fieldForLongPassword = new JSONArray(new ArrayList(Arrays.asList("password")));

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath(
                        "[*].defaultMessage",
                        containsInAnyOrder(defaultMessageForInvalidEmail.get(0), defaultMessageForLongPassword.get(0))
                ))
                .andExpect(jsonPath(
                        "[*].objectName",
                        containsInAnyOrder(objectName.get(0), objectName.get(0))
                ))
                .andExpect(jsonPath(
                        "[*].field",
                        contains(fieldForInvalidEmail.get(0), fieldForLongPassword.get(0))
                ));

        verify(userServiceMock, times(0)).save(any());
    }

    @Test
    public void registration_EverythingIsValid_ShouldReturnUserJsonObjectOfTheSavedUser() throws Exception {

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name", containsString("name")))
                .andExpect(jsonPath("$.email", containsString("email@email.com")))
                .andExpect(jsonPath("$.username", containsString("email@email.com")))
                .andExpect(jsonPath("$.passwordConfirm", nullValue()))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("USER")));


        verify(userServiceMock, times(1)).save(any());
    }

    // "/user-login" route tests
    @Test
    public void userLogin_InvalidUsername_ShouldReturnValidJsonWithErrorMessage() throws Exception {
        user.setUsername("invalid");

        mockMvc.perform(post("/user-login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorMessages[0]", containsString("Invalid username or password!")))
                .andExpect(jsonPath("$.loggedInUser.name", containsString("anonymous")))
                .andExpect(jsonPath("$.loggedInUser.email", containsString("anonymous@anonymous.com")))
                .andExpect(jsonPath("$.loggedInUser.roles", emptyIterableOf(JSONArray.class)));
    }

    @Test
    public void userLogin_InvalidPassword_ShouldReturnValidJsonWithErrorMessage() throws Exception {
        user.setPassword("invalid");
        when(securityServiceImplMock.findLoggedInUsername()).thenReturn("notloggedin");

        mockMvc.perform(post("/user-login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorMessages[0]", containsString("Invalid username or password!")))
                .andExpect(jsonPath("$.loggedInUser.name", containsString("anonymous")))
                .andExpect(jsonPath("$.loggedInUser.email", containsString("anonymous@anonymous.com")))
                .andExpect(jsonPath("$.loggedInUser.roles", emptyIterableOf(JSONArray.class)));
    }

    @Test
    public void userLogin_AlreadyLoggedIn_ShouldReturnValidJsonWithErrorMessage() throws Exception {
        when(securityServiceImplMock.findLoggedInUsername()).thenReturn(user.getUsername());

        mockMvc.perform(post("/user-login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorMessages[0]", containsString("Already logged in.")));
    }

    @Test
    public void userLogin_EverythingIsOk_ShouldReturnValidJsonWithInfoMessage() throws Exception {
        doNothing().when(userServiceMock).login(any(), any());
        when(securityServiceImplMock.findLoggedInUsername()).thenReturn("anonymousUser");

        mockMvc.perform(post("/user-login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.infoMessages[0]", containsString("Successfully logged in!")))
                .andExpect(jsonPath("$.errorMessages", emptyIterable()));
    }

    @Test
    public void userLogout_ShouldReturnValidJsonWithInfoMessage() throws Exception {

        mockMvc.perform(post("/user-logout")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.infoMessages[0]", containsString("You have been logged out successfully.")))
                .andExpect(jsonPath("$.errorMessages", emptyIterable()));
    }

    @Test
    public void loggedInUserReturnsAnonymusByDefault() throws Exception {
        mockMvc.perform(get("/logged-in-user"))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorMessages[0]", containsString("Nobody is logged in!")))
                .andExpect(jsonPath("$.infoMessages[*]", hasSize(0)))
                .andExpect(jsonPath("$.loggedInUser.id", nullValue()))
                .andExpect(jsonPath("$.loggedInUser.name", containsString("anonymous")))
                .andExpect(jsonPath("$.loggedInUser.email", containsString("anonymous@anonymous.com")))
                .andExpect(jsonPath("$.loggedInUser.username", nullValue()))
                .andExpect(jsonPath("$.loggedInUser.password", nullValue()))
                .andExpect(jsonPath("$.loggedInUser.passwordConfirm", nullValue()))
                .andExpect(jsonPath("$.loggedInUser.roles", emptyIterableOf(JSONArray.class)));
    }
}