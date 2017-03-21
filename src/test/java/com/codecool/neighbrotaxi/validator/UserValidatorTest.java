package com.codecool.neighbrotaxi.validator;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@MockBean(UserService.class)
@MockBean(Errors.class)
@Transactional
public class UserValidatorTest extends AbstractTest {

    @Autowired
    private UserService userService;

    @Autowired
    private Errors errors;

    @Autowired
    private UserValidator userValidator;

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setEmail("email");
        user.setPassword("password");
        user.setPasswordConfirm("password");
    }

    @Test
    public void supports_WhenTheClassIsSupported_ShouldReturnTrue() throws Exception {

        boolean isSupported = userValidator.supports(User.class);

        assertTrue(isSupported);
    }

    @Test
    public void supports_WhenTheClassIsNotSupported_ShouldReturnFalse() throws Exception {

        boolean isSupported = userValidator.supports(NotSupportedClassForValidator.class);

        assertFalse(isSupported);
    }

    private class NotSupportedClassForValidator{}

    @Test
    public void validate_AllFieldValid_NoErrorsInBinding() throws Exception {

        userValidator.validate(user, errors);

        verify(errors, never()).rejectValue(anyString(), anyString());
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void validate_EmailFieldNotValid_EmailAlreadyInUse() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(new User());

        userValidator.validate(user, errors);

        verify(errors).rejectValue("email", "Duplicate.user.email", "Email already in database");
    }

    @Test
    public void validate_PassLength_Short() {
        user.setPassword("har");
        user.setPasswordConfirm("har");
        userValidator.validate(user, errors);

        verify(errors).rejectValue("password", "Size.userForm.password", "The size of the password is incorrect");
    }

    @Test
    public void validate_PassLength_Long() {
        user.setPassword("harrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        user.setPasswordConfirm("harrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        userValidator.validate(user, errors);

        verify(errors).rejectValue("password", "Size.userForm.password", "The size of the password is incorrect");
    }

    @Test
    public void validate_PassLength_ShortAndEmailExists() {
        when(userService.findByEmail(anyString())).thenReturn(new User());
        user.setPassword("harr");
        user.setPasswordConfirm("harr");
        userValidator.validate(user, errors);

        verify(errors).rejectValue("password", "Size.userForm.password", "The size of the password is incorrect");
        verify(errors).rejectValue("email", "Duplicate.user.email", "Email already in database");
    }


    @Test
    public void validate_PassLength_LongAndEmailExistsAndNotAMatch() {
        when(userService.findByEmail(anyString())).thenReturn(new User());
        user.setPassword("harrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        user.setPasswordConfirm("harrrrrrrrr");
        userValidator.validate(user, errors);

        verify(errors).rejectValue("password", "Size.userForm.password", "The size of the password is incorrect");
        verify(errors).rejectValue("email", "Duplicate.user.email", "Email already in database");
        verify(errors).rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm", "The passwords do not match");
    }

    public void validate_PasswordsDoNotMatch() throws Exception {
        user.setPasswordConfirm("password1");

        userValidator.validate(user, errors);

        verify(errors).rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm", "The passwords do not match");
    }
}