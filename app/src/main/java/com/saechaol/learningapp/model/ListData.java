package com.saechaol.learningapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Creates ArrayList objects of type ListModel, a textual representation
 * of a student, instructor or admin user role, and stores information about
 * each user within several lists representing different aspects of user information,
 * which is then stored in a hashmap.
 */
public class ListData {
    String userType;

    public ListData(String userType) {
        this.userType = userType;
    }

    public HashMap<String,List<ListModel>> getlist()  {

        HashMap <String,List<ListModel>> navDrawerHashMap = new LinkedHashMap<String, List<ListModel>>();

        if(userType.equals("admin"))
        {
            List<ListModel> home = new ArrayList<>();

            List<ListModel> myInformation = new ArrayList<>();

            List<ListModel> admin = new ArrayList<>();

            List<ListModel> instructor = new ArrayList<>();

            List<ListModel> student = new ArrayList<ListModel>();

            List<ListModel> subjects = new ArrayList<>();

            List<ListModel> enroll = new ArrayList<ListModel>();
            enroll.add(new ListModel("Enroll"));
            enroll.add(new ListModel("Display"));
            enroll.add(new ListModel("Drop"));

            List<ListModel> schedule = new ArrayList<ListModel>();
            schedule.add(new ListModel("In-progress "));
            schedule.add(new ListModel("Add Schedule"));
            schedule.add(new ListModel("Display Schedule"));
            schedule.add(new ListModel("Edit Schedule"));
            schedule.add(new ListModel("Remove Schedule"));

            List<ListModel> grade = new ArrayList<>();

            List<ListModel> calendar = new ArrayList<>();

            List<ListModel> messages = new ArrayList<>();

            List<ListModel> updatePassword = new ArrayList<>();

            List<ListModel> logout = new ArrayList<>();


            navDrawerHashMap.put("Home", home);
            navDrawerHashMap.put("My Info",myInformation);
            navDrawerHashMap.put("Admin", admin);
            navDrawerHashMap.put("Instructor", instructor);
            navDrawerHashMap.put("Student", student);
            navDrawerHashMap.put("Subject", subjects);
            navDrawerHashMap.put("Enrollment", enroll);
            navDrawerHashMap.put("Schedule", schedule);
            navDrawerHashMap.put("Grade", grade);
            navDrawerHashMap.put("Calendar",calendar);
            navDrawerHashMap.put("Messages", messages);
            navDrawerHashMap.put("Change Password", updatePassword);
            navDrawerHashMap.put("Log out", logout);

        }
        else if (userType.equals("instructor"))
        {
            List<ListModel> home = new ArrayList<>();

            List<ListModel> myInformation = new ArrayList<>();

            List<ListModel> student = new ArrayList<>();

            List<ListModel> schedule = new ArrayList<>();
            schedule.add(new ListModel("In-progress "));
            schedule.add(new ListModel("Add Schedule"));
            schedule.add(new ListModel("Display Schedule"));
            schedule.add(new ListModel("Edit Schedule"));
            schedule.add(new ListModel("Remove Schedule"));

            List<ListModel> grade = new ArrayList<ListModel>();
            grade.add(new ListModel("Display Grade"));
            grade.add(new ListModel("Grade Graph"));

            List<ListModel> calendar = new ArrayList<ListModel>();

            List<ListModel> messages = new ArrayList<ListModel>();

            List<ListModel> updatePassword = new ArrayList<ListModel>();

            List<ListModel> logout = new ArrayList<ListModel>();

            navDrawerHashMap.put("Home", home);
            navDrawerHashMap.put("My Info", myInformation);
            navDrawerHashMap.put("Student", student);
            navDrawerHashMap.put("Schedule", schedule);
            navDrawerHashMap.put("Grade", grade);
            navDrawerHashMap.put("Calendar",calendar);
            navDrawerHashMap.put("Messages", messages);
            navDrawerHashMap.put("Change Password", updatePassword);
            navDrawerHashMap.put("Log out", logout);

        }
        else if(userType.equals("student"))
        {
            List<ListModel> home = new ArrayList<ListModel>();

            List<ListModel> myInformation = new ArrayList<ListModel>();

            List<ListModel> subjects = new ArrayList<ListModel>();

            List<ListModel> schedule = new ArrayList<ListModel>();
            schedule.add(new ListModel("In-progress"));
            schedule.add(new ListModel("Display Schedule"));

            List<ListModel> grade = new ArrayList<ListModel>();

            List<ListModel> calendar = new ArrayList<ListModel>();

            List<ListModel> messages = new ArrayList<ListModel>();

            List<ListModel> updatePassword = new ArrayList<ListModel>();

            List<ListModel> logout = new ArrayList<ListModel>();

            navDrawerHashMap.put("Home", home);
            navDrawerHashMap.put("My Info", myInformation);
            navDrawerHashMap.put("Subject", subjects);
            navDrawerHashMap.put("Schedule", schedule);
            navDrawerHashMap.put("Grade", grade);
            navDrawerHashMap.put("Calendar",calendar);
            navDrawerHashMap.put("Messages", messages);
            navDrawerHashMap.put("Change Password", updatePassword);
            navDrawerHashMap.put("Log out", logout);

        }

        return navDrawerHashMap;
    }
}
