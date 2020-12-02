package com.saechaol.learningapp.model;


/**
 * Provides support for retrieving details about users with a checkbox
 */
public class UserWithCheckbox {

    String userId;
    String userName;
    String emailId;
    boolean check;

    public UserWithCheckbox(String userName, String emailId, boolean check, String userId) {
        this.userId = userId;
        this.userName = userName;
        this.emailId = emailId;
        this.check = check;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean getCheck() {
        return check;
    }

    public void setUserName(String user) {
        this.userName = user;
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public void setEmailId(String email) {
        this.emailId = email;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

}
