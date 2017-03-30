package com.codecool.neighbrotaxi.controller;

import com.codecool.neighbrotaxi.model.SerializableSessionStorage;
import com.codecool.neighbrotaxi.model.SessionStorage;
import com.codecool.neighbrotaxi.model.User;
import com.codecool.neighbrotaxi.service.SecurityService;
import com.codecool.neighbrotaxi.service.UserService;
import com.codecool.neighbrotaxi.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;


@RestController
@CrossOrigin
public class RestUserController {
    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SessionStorage sessionStorage;

    @Autowired
    private HttpSession session;


    /**
     * Route for user registration POST request. Using the userValidator for checking valid input.
     * If every input value is valid, then return an object of the saved user. If some input is invalid, returns a list of errors.
     * @param user It gets a JSON in the request body, and parse it into a User object.
     * @param bindingResult The method will store the errors in this object.
     * @return An object. If every input value is valid: returns an object of the saved user, else: returns a list of errors.
     */
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public Object registration(@RequestBody @Valid User user, BindingResult bindingResult){
        user.setUsername(user.getEmail());
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors();
        }

        userService.save(user);
        user.setPasswordConfirm(null);
        return user;
    }

    /**
     * Route for user login. Authenticate the given credentials.
     * If its valid, then setup the loggedInUser, else give back an error message corresponding to the problem.
     * @param user A User object. The Spring parse the JSON located in the request body, and make a new User object from it, with the given JSON datas.
     * @param request HttpServletRequest object. We use this to setup the session.
     * @return SerializableSessionStorage object. With this, we can serialize a SessionStorage objects fields, so the returned JSON will be parsed from this.
     * @see SerializableSessionStorage SerializableSessionStorage
     */
    @RequestMapping(value = "/user-login", method = RequestMethod.POST)
    public SerializableSessionStorage userLogin(@RequestBody User user, HttpServletRequest request) {
        sessionStorage.clearMessages();
        if (Objects.equals(securityService.findLoggedInUsername(), user.getUsername())) {
            sessionStorage.addErrorMessage("Already logged in.");
            return new SerializableSessionStorage(sessionStorage);
        }

        try {
            userService.login(request, user);
        } catch (AuthenticationException e) {
            sessionStorage.addErrorMessage("Invalid username or password!");
            return new SerializableSessionStorage(sessionStorage);
        }

        sessionStorage.setLoggedInUser(userService.findByUsername(securityService.findLoggedInUsername()));
        sessionStorage.addInfoMessage("Successfully logged in!");
        return new SerializableSessionStorage(sessionStorage);
    }

    /**
     * Route for user logout. If there's a logged in user, then clear the user from the session,
     * otherwise return an info message, that say "There's no logged in user!".
     * @param request HttpServletRequest object. We use this to setup the session.
     * @return SerializableSessionStorage object. With this, we can serialize a SessionStorage objects fields, so the returned JSON will be parsed from this.
     * @see SerializableSessionStorage SerializableSessionStorage
     */
    @RequestMapping(value = "/user-logout", method = RequestMethod.POST)
    public SerializableSessionStorage userLogout(HttpServletRequest request) {
        if (!Objects.equals(sessionStorage.getLoggedInUser().getUsername(), "anonymous")){
            userService.logout(request);
            sessionStorage.addInfoMessage("You have been logged out successfully.");
        } else {
            sessionStorage.clearMessages();
            sessionStorage.addErrorMessage("There's no logged in user!");
        }
        return new SerializableSessionStorage(sessionStorage);
    }

    /**
     * Route for user data update PUT request. Update the user with the given values in the JSON object.
     * @param user The Spring Framework parses the JSON - in the RequestBody - into a User object, and give it to the UserService's update method.
     * @return The updated user from the database.
     */
    @RequestMapping(value = "/update-user", method = RequestMethod.PUT)
    public Object updateUser(@RequestBody @Valid User user, BindingResult bindingResult){
        // @Valid validate the email field
        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors();
        }
        user.setId(sessionStorage.getLoggedInUser().getId());
        user.setPassword(sessionStorage.getLoggedInUser().getPassword());
        user.setPasswordConfirm(sessionStorage.getLoggedInUser().getPasswordConfirm());

        userService.update(user);
        sessionStorage.addInfoMessage("User updated");
        return sessionStorage.getInfoMessages();
    }

    @RequestMapping(value = "/logged-in-user", method = RequestMethod.GET)
    public Object loggedInUser(){
        sessionStorage.clearMessages();

        if (Objects.equals(sessionStorage.getLoggedInUser().getName(), "anonymous")) {
            sessionStorage.addErrorMessage("Nobody is logged in!");
            return new SerializableSessionStorage(sessionStorage);
        } else {
            sessionStorage.setLoggedInUser(userService.findByUsername(sessionStorage.getLoggedInUser().getUsername()));
        }

        return sessionStorage.getLoggedInUser();
    }
}
