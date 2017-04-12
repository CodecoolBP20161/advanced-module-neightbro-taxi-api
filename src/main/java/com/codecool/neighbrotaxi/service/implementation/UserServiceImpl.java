package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.enums.RoleEnum;
import com.codecool.neighbrotaxi.model.SessionStorage;
import com.codecool.neighbrotaxi.model.entities.Role;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import com.codecool.neighbrotaxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.UniqueConstraint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashSet;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionStorage sessionStorage;

    /**
     * Saving user object into the database with the UserRepository's save method.
     * @param user The object of the User class. This is the user we want to save into the database.
     */
    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleEnum.USER.name()));
        user.setRoles(roles);
        userRepository.save(user);
    }

    /**
     * Find user by username.
     * @param username Find the user by this, given username.
     * @return null if there's no user with the given username.
     * Otherwise a User object with the given username, parsed from the user table.
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by email.
     * @param email Find the user by this - given - email.
     * @return null if there's no user with the given email.
     * Otherwise a User object with the given email, parsed from the user table.
     */
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by id.
     * @param id Find the user by this - given - id.
     * @return null if there's no user with the given id.
     * Otherwise a User object with the given id, parsed from the user table.
     */
    @Override
    public User findOne(Integer id) {
        return userRepository.findOne(id);
    }

    /**
     * With this method we can logging out the logged in user.
     * Clean up the session session storage and the security context.(Remove the user from these)
     * @param request HttpServletRequest object. We use this to setup the session.
     */
    @Override
    public void logout(HttpServletRequest request) {
        sessionStorage.setDefault();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }

    /**
     * This method is for updating an existing user.
     * Saves the new user details.
     * @param user
     */
    @Override
    public void update(User user) throws SQLIntegrityConstraintViolationException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SQLIntegrityConstraintViolationException();
        }
        userRepository.save(user);
    }

    /**
     * With this method we can logging in a user. Authenticate and store the user into the session.
     * @param request HttpServletRequest object. We use this to setup the session.
     * @param user User object with the necessary fields for validation.
     * @throws AuthenticationException Throws this exception when the authentication fails.
     */
    @Override
    public void login(HttpServletRequest request, User user) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // Create a new session and add the security context.
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }
}
