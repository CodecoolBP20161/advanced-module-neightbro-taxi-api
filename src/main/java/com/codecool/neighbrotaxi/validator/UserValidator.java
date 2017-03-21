package com.codecool.neighbrotaxi.validator;

import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class UserValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    @Autowired
    private UserService userService;


    /**
     * Can this Validator validate instances of the supplied aClass?
     * @param aClass The supplied class what we want to validate
     * @return True if the Validator can validate the class. Otherwise its return value is False.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    /**
     * Validates user registration
     * Ensures that the:
     *      - email address is not too long
     *      - the passwords length is valid
     *      - the password fields match
     * @param o empty Object which is then casted into the proper one(user)
     * @param errors Stores and exposes information about data-binding and validation errors for a specific object.
     * @see org.springframework.validation.Validator Validator
     */
    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        if (userService.findByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "Duplicate.user.email", "Email already in database");
            logger.warn("Email already in database: " + user.getEmail());
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password", "The size of the password is incorrect");
            logger.warn("Password too long or too short: " + user.getPassword());
        }

        if (user.getPasswordConfirm() != null) {
            if (!user.getPasswordConfirm().equals(user.getPassword())) {
                errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm", "The passwords do not match");
                logger.warn("The passwords do not match" );
            }
        }
    }
}
