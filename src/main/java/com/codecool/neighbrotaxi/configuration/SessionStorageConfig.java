package com.codecool.neighbrotaxi.configuration;

import com.codecool.neighbrotaxi.model.SessionStorage;
import com.codecool.neighbrotaxi.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.ArrayList;

@Configuration
public class SessionStorageConfig {

    /**
     * Define the default values for the sessionStorage bean.
     * We use this bean to store the session variables, and with it we can return a well formatted JSON object towards the client.
     */
    @Bean
    @Scope(value = "session", proxyMode= ScopedProxyMode.TARGET_CLASS)
    SessionStorage sessionStorage(){

        User user = new User();
        user.setName("anonymous");
        user.setEmail("anonymous@anonymous.com");

        SessionStorage sessionStorage = new SessionStorage();
        sessionStorage.setErrorMessages(new ArrayList<>());
        sessionStorage.setInfoMessages(new ArrayList<>());
        sessionStorage.setLoggedInUser(user);
        return sessionStorage;
    }
}
