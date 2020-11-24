package com.saechaol.learningapp.model;

/**
 * Provide functionality for retrieving and setting user information
 */
public class RegisterUsers {

    public String userId;
    public String username;
    public String userType;

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public void setUserType(String user) {
        this.userType = user;
    }

}
