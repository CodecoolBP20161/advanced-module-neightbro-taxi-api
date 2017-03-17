package com.codecool.neighbrotaxi.model;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class has no methods, but constructor.
 * This constructor takes a SessionStorage object and setup the fields based on it.
 * Technically it is the same as the the SessionStorage class,
 * but it can be parsed to a JSON object by the Spring, because its scope is not session.
 */
@Component
public class SerializableSessionStorage {
    private List<String> errorMessages;
    private List<String> infoMessages;
    private User loggedInUser;

    public SerializableSessionStorage() {
        super();
    }

    public SerializableSessionStorage(SessionStorage sessionStorage) {
        this.errorMessages = sessionStorage.getErrorMessages();
        this.loggedInUser = sessionStorage.getLoggedInUser();
        this.infoMessages = sessionStorage.getInfoMessages();
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getInfoMessages() {
        return infoMessages;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
