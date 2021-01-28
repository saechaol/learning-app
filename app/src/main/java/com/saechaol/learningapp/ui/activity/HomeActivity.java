package com.saechaol.learningapp.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.ListData;
import com.saechaol.learningapp.model.ListModel;
import com.saechaol.learningapp.model.RegisterUsers;
import com.saechaol.learningapp.service.AlertTaskIntentService;
import com.saechaol.learningapp.ui.adapter.MenuAdapter;
import com.saechaol.learningapp.ui.fragment.AdminViewFragment;
import com.saechaol.learningapp.ui.fragment.AssignedSubjectViewFragment;
import com.saechaol.learningapp.ui.fragment.EditPasswordFragment;
import com.saechaol.learningapp.ui.fragment.EditTaskFragment;
import com.saechaol.learningapp.ui.fragment.EnrollStudentFragment;
import com.saechaol.learningapp.ui.fragment.EnrollStudentViewFragment;
import com.saechaol.learningapp.ui.fragment.GradeGraphFragment;
import com.saechaol.learningapp.ui.fragment.GradeStudentViewFragment;
import com.saechaol.learningapp.ui.fragment.GradeViewFragment;
import com.saechaol.learningapp.ui.fragment.HomeFragment;
import com.saechaol.learningapp.ui.fragment.InProgressFragment;
import com.saechaol.learningapp.ui.fragment.InstructorStudentViewFragment;
import com.saechaol.learningapp.ui.fragment.InstructorViewFragment;
import com.saechaol.learningapp.ui.fragment.MessageViewFragment;
import com.saechaol.learningapp.ui.fragment.MyInformationFragment;
import com.saechaol.learningapp.ui.fragment.NewMessageFragment;
import com.saechaol.learningapp.ui.fragment.ScheduleAddFragment;
import com.saechaol.learningapp.ui.fragment.StudentDisenrollFragment;
import com.saechaol.learningapp.ui.fragment.StudentSubjectViewFragment;
import com.saechaol.learningapp.ui.fragment.StudentViewFragment;
import com.saechaol.learningapp.ui.fragment.SubjectViewFragment;
import com.saechaol.learningapp.ui.fragment.TaskDeleteFragment;
import com.saechaol.learningapp.ui.fragment.TaskInProgressViewFragment;
import com.saechaol.learningapp.ui.fragment.TaskViewFragment;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends BaseActivity {

    ActionBarDrawerToggle actionBarDrawerToggle;
    ActionBar actionBar;

    HashMap<String, List<ListModel>> listHashMap;
    ListData listData;
    DrawerLayout drawerLayout;
    List<String> listPrimary;

    MenuAdapter adapter;

    private int lastGroupSelPos = -1, lastChildSelPos = -1;
    ExpandableListView listExpandable;
    LinearLayout navDrawerList;

    RegisterUsers user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = new RegisterUsers();
        intentService();
        configureData();
        configureNavDrawer();
        startService(new Intent(this, AlertTaskIntentService.class));
        if (savedInstanceState == null) {
            onHomeClick();
        }
    }


    public void onHomeClick() {
        setToolbarTitle("Home");
        UserTypeData userTypeData = new UserTypeData(user.userType);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (userTypeData.getUserType()) {

            case UserTypeData.ADMIN:
                HomeFragment displayTask = new HomeFragment();
                fragmentTransaction.replace(R.id.fragmentholder, displayTask);
                fragmentTransaction.commit();
                break;

            case UserTypeData.INSTRUCTOR:
                AssignedSubjectViewFragment taskFragment = new AssignedSubjectViewFragment();
                fragmentTransaction.replace(R.id.fragmentholder, taskFragment);
                fragmentTransaction.commit();
                break;

            case UserTypeData.STUDENT:
                StudentSubjectViewFragment InProgressTasksFragment = new StudentSubjectViewFragment();
                fragmentTransaction.replace(R.id.fragmentholder, InProgressTasksFragment);
                fragmentTransaction.commit();
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void openCourse() {

        setToolbarTitle("Subject");
        lastGroupSelPos = 5;
        SubjectViewFragment infoFragment = new SubjectViewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
        fragmentTransaction.commit();
    }


    public void openInstruction() {
        lastGroupSelPos = 3;
        setToolbarTitle("Instructor");
        InstructorViewFragment infoFragment = new InstructorViewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
        fragmentTransaction.commit();
    }

    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    void intentService() {
        Intent previous = getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            user.userId = (String) bundle.get("userId");
            user.username = (String) bundle.get("userName");
            user.userType = (String) bundle.get("userType");
        }
    }

    public void openStudent() {
        lastGroupSelPos = 4;
        setToolbarTitle("Student");
        if (user.userType.equalsIgnoreCase(UserTypeData.INSTRUCTOR)) {
            InstructorStudentViewFragment infoFragment = new InstructorStudentViewFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
            fragmentTransaction.commit();
        } else {
            StudentViewFragment infoFragment = new StudentViewFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
            fragmentTransaction.commit();
        }
    }


    public void parentClicking(){
        listExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (expandableListView.getExpandableListAdapter().getChildrenCount(groupPosition) == 0) {
                    if (lastGroupSelPos != groupPosition) {
                        if (listPrimary.get(groupPosition).equals("Home")) {
                            onHomeClick();

                        } else if (listPrimary.get(groupPosition).equals("My Info")) {
                            setToolbarTitle("My Info");
                            MyInformationFragment infoFragment = new MyInformationFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
                            fragmentTransaction.commit();

                        } else if (listPrimary.get(groupPosition).equals("Change Password")) {
                            setToolbarTitle("Change Password");
                            EditPasswordFragment EditPasswordFragment = new EditPasswordFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentholder, EditPasswordFragment);
                            fragmentTransaction.commit();

                        } else if (listPrimary.get(groupPosition).equals("Log out")) {
                            setToolbarTitle("Log out");
                            PreferenceManager prefsManager = new PreferenceManager(HomeActivity.this);
                            prefsManager.clearData();
                            final Intent intent = new Intent(HomeActivity.this, AlertTaskIntentService.class);
                            intent.putExtra("stop", true);
                            startService(intent);
                            if (getSinchServiceInterface().isStarted() && getSinchServiceInterface().isBinderAlive()) {
                                getSinchServiceInterface().stopClient();
                            }
                            LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(new Intent(BaseActivity.ACTION_FINISH));
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

                        } else if (listPrimary.get(groupPosition).equals("Calendar")) {
                            //This intent is created to create the google calendar
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity"));
                            PackageManager manager = getPackageManager();
                            List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                            if (infos.size() > 0) {
                                startActivity(intent);
                            } else {
                                showSnackBar("Please install Google Calendar Application.", findViewById(R.id.activityCoordinatorLayout));
                            }
                        } else if (listPrimary.get(groupPosition).equals("Admin")) {
                            setToolbarTitle("Admin");
                            AdminViewFragment infoFragment = new AdminViewFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
                            fragmentTransaction.commit();
                        } else if (listPrimary.get(groupPosition).equals("Grade")) {
                            setToolbarTitle("Grade");
                            if (user.userType.equals("admin") ) {
                                GradeViewFragment GradeViewFragment = new GradeViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragmentholder, GradeViewFragment);
                                fragmentTransaction.commit();
                            } else if (user.userType.equals("student")) {
                                GradeStudentViewFragment GradeStudentViewFragment = new GradeStudentViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragmentholder, GradeStudentViewFragment);
                                fragmentTransaction.commit();
                            }
                        } else if (listPrimary.get(groupPosition).equals("Student")) {
                            setToolbarTitle("Student");

                            if (user.userType.equalsIgnoreCase(UserTypeData.INSTRUCTOR)) {
                                InstructorStudentViewFragment infoFragment = new InstructorStudentViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
                                fragmentTransaction.commit();
                            } else {
                                StudentViewFragment infoFragment = new StudentViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
                                fragmentTransaction.commit();
                            }
                        } else if (listPrimary.get(groupPosition).equals("Instructor")) {
                            setToolbarTitle("Instructor");
                            InstructorViewFragment infoFragment = new InstructorViewFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
                            fragmentTransaction.commit();
                        } else if (listPrimary.get(groupPosition).equals("Subject")) {
                            setToolbarTitle("Subject");
                            SubjectViewFragment infoFragment = new SubjectViewFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentholder, infoFragment);
                            fragmentTransaction.commit();
                        }  else if (listPrimary.get(groupPosition).equals("Messages")) {
                            setToolbarTitle("New Message");
                            NewMessageFragment mlaNewMessageFragment = new NewMessageFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentholder, mlaNewMessageFragment);
                            fragmentTransaction.commit();
                        }else {
                            Toast.makeText(HomeActivity.this, "Screen is still under development.", Toast.LENGTH_LONG).show();
                        }
                    }
                    drawerLayout.closeDrawer(navDrawerList);
                    lastGroupSelPos = groupPosition;
                }
                return false;
            }


        });

    }


    public void childClicking(){

        listExpandable.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (lastGroupSelPos != groupPosition || lastChildSelPos != childPosition) {

                    if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Display") && listPrimary.get(groupPosition).equals("Enrollment")) {
                        setToolbarTitle("Enroll Student Display");
                        EnrollStudentViewFragment EnrollStudentViewFragment = new EnrollStudentViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, EnrollStudentViewFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Drop") && listPrimary.get(groupPosition).equals("Enrollment")) {
                        setToolbarTitle("Disenroll Student");
                        StudentDisenrollFragment StudentDisEnrollFragment = new StudentDisenrollFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, StudentDisEnrollFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Enroll") && listPrimary.get(groupPosition).equals("Enrollment")) {
                        setToolbarTitle("Enroll Student");
                        EnrollStudentFragment enrollFragment = new EnrollStudentFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, enrollFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Add Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Add Schedule");
                        ScheduleAddFragment ScheduleAddFragment = new ScheduleAddFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, ScheduleAddFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Display Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Display Schedule");

                        TaskViewFragment displayTask = new TaskViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, displayTask);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Edit Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Edit Schedule");

                        EditTaskFragment EditTaskFragment = new EditTaskFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, EditTaskFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("In-process") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("In-process");

                        InProgressFragment inProcessTasksFragment = new InProgressFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, inProcessTasksFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("In-process ") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("In-process");

                        TaskInProgressViewFragment displayTaskInProgress = new TaskInProgressViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, displayTaskInProgress);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Remove Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Remove Schedule");

                        TaskDeleteFragment taskDeleteFragment = new TaskDeleteFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, taskDeleteFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("New") && listPrimary.get(groupPosition).equals("Messages")) {
                        setToolbarTitle("New Message");

                        NewMessageFragment NewMessageFragment = new NewMessageFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, NewMessageFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Display") && listPrimary.get(groupPosition).equals("Messages")) {
                        setToolbarTitle("Display Message");

                        MessageViewFragment displayGmail = new MessageViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, displayGmail);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Display Grade") && listPrimary.get(groupPosition).equals("Grade")) {
                        setToolbarTitle("Display Grade");

                        GradeViewFragment GradeViewFragment = new GradeViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, GradeViewFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTextView().equals("Grade Graph") && listPrimary.get(groupPosition).equals("Grade")) {
                        setToolbarTitle("Grade Graph");

                        GradeGraphFragment gradeGraphFragment = new GradeGraphFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentholder, gradeGraphFragment);
                        fragmentTransaction.commit();

                    } else {
                        Toast.makeText(HomeActivity.this, "Screen is still under development.", Toast.LENGTH_LONG).show();
                    }
                }
                lastGroupSelPos = groupPosition;
                lastChildSelPos = childPosition;
                drawerLayout.closeDrawer(navDrawerList);
                return true;
            }
        });
    }

    public void configureNavDrawer() {
        parentClicking();
        childClicking();
        listExpandable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        listExpandable.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });
    }


    public void configureData(){
        // Configure list view with data.
        listExpandable = (ExpandableListView) findViewById(R.id.expandableList);
        listData = new ListData(user.userType);
        listHashMap = listData.getlist();
        listPrimary = new ArrayList<>(listHashMap.keySet());

        // Configure the data in Adapter.
        adapter = new MenuAdapter(this, listHashMap, listPrimary);
        listExpandable.setAdapter(adapter);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.learningAppSliderOpen, R.string.learningAppSliderClose);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navDrawerList = (LinearLayout) findViewById(R.id.drawer1);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navDrawerList)) {
                drawerLayout.closeDrawer(navDrawerList);
            } else {
                drawerLayout.openDrawer(navDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();

    }
    
}
