package com.codecool.neighbrotaxi.model;

import com.codecool.neighbrotaxi.model.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This Class' session object stores the session attributes in valid form for JSON response.
 */
@Component
public class SessionStorage {
    private List<String> errorMessages;
    private List<String> infoMessages;
    private User loggedInUser;

    public void setDefault(){
        User user = new User();
        user.setName("anonymous");
        user.setEmail("anonymous@anonymous.com");

        clearMessages();
        setLoggedInUser(user);
    }

    public void clearMessages(){
        errorMessages.clear();
        infoMessages.clear();
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessage(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }

    public void clearAllErrorMessages() {
        this.errorMessages.clear();
    }

    public List<String> getInfoMessages() {
        return infoMessages;
    }

    public void setInfoMessages(List<String> infoMessages) {
        this.infoMessages = infoMessages;
    }

    public void addInfoMessage(String infoMessage) {
        this.infoMessages.add(infoMessage);
    }

    public void clearAllInfoMessages() {
        this.infoMessages.clear();
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
