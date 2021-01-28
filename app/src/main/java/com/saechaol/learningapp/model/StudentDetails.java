package com.saechaol.learningapp.model;

import java.io.Serializable;

/**
 * Provides support for retrieving and updating student data
 */
public class StudentDetails implements Serializable {

    public String studentId;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public int userId;
    public String aliasMailId;
    public String address;
    public String skypeId;
    public String password;

    public String getPassword() { return password; }

    public void setPassword(String pwd) { this.password = pwd; }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String student) {
        this.studentId = student;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailId) {
        this.email = emailId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String number) {
        this.phone = number;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public String getAliasMailId() {
        return aliasMailId;
    }

    public void setAliasMailId(String alias) {
        this.aliasMailId = alias;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String addr) {
        this.address = addr;
    }

    public String getSkypeId() {
        return skypeId;
    }

    public void setSkypeId(String skype) {
        this.skypeId = skype;
    }


}
