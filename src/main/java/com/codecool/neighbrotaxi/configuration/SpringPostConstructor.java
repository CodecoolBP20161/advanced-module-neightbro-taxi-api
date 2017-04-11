package com.codecool.neighbrotaxi.configuration;

import com.codecool.neighbrotaxi.enums.RoleEnum;
import com.codecool.neighbrotaxi.model.entities.Role;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.repository.RoleRepository;
import com.codecool.neighbrotaxi.repository.RouteRepository;
import com.codecool.neighbrotaxi.repository.UserRepository;
import com.codecool.neighbrotaxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class SpringPostConstructor {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;


    /**
     * Save an ADMIN and a USER role into the DB upon the first server start.
     */
    void fillUpDb(){
        Role role = new Role();

        if (roleRepository.findByName("ADMIN") == null) {
            role.setName("ADMIN");
            roleRepository.save(role);
        }

        if (roleRepository.findByName("USER") == null) {
            role = new Role();
            role.setName("USER");
            roleRepository.save(role);
        }
    }

    /**
     * Registering the very first user with ADMIN and USER roles upon the first server start.
     */
    @PostConstruct
    public void setupAdmin(){
        fillUpDb();

        // TODO: think about how to reg a main admin on server startup.

        // Its a fast version before DEMO day
        if (userRepository.findByEmail("admin@admin.com") == null) {

            User user = new User();
            user.setEmail("admin@admin.com");
            user.setName("admin");
            user.setPassword("admin");
            user.setUsername("admin");
            user.setRoles(new HashSet<>());
            userService.save(user);
            Set<Role> roleSet = user.getRoles();
            roleSet.add(roleRepository.findByName(RoleEnum.ADMIN.name()));
            user.setRoles(roleSet);
            userRepository.save(user);
        }
    }
}
