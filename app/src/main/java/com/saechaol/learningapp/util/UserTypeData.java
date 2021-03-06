package com.saechaol.learningapp.util;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UserTypeData {

    public static final String INSTRUCTOR = "instructor";
    public static final String STUDENT = "student";
    public static final String ADMIN = "admin";

    private @UserType String userType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public UserTypeData(@UserType String userType) {
        System.out.println("type: " + userType);
        this.userType = userType;
    }

    @StringDef({
            INSTRUCTOR, STUDENT, ADMIN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserType {}
}
