package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.swipe.util.Attributes;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.GradeTask;
import com.saechaol.learningapp.model.RegisterUsers;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.model.TaskDetails;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.activity.UpdateGradeActivity;
import com.saechaol.learningapp.ui.adapter.GradeAdapter;
import com.saechaol.learningapp.ui.adapter.OnItemClickListener;
import com.saechaol.learningapp.ui.view.EmptyRecyclerView;
import com.saechaol.learningapp.util.Predicate;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;
import com.saechaol.learningapp.util.VerticalSpaceItemDecoration;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GradeViewFragment extends Fragment {

    List<SubjectDetails> subjectDetails = new ArrayList<>();
    PreferenceManager prefsManager;

    String[] subjectID;
    String[] subjectTopic;

    Spinner spnrSubjectTaskId;
    Spinner spnrSubject;

    List<TaskDetails> taskDetails = new ArrayList<>();
    List<GradeTask> gradeDetails = new ArrayList<>();
    ListView listViewTasks;
    GradeAdapter gradeDisplayAdapter;

    int spinnerTaskPosition = 0;

    View view;

    RegisterUsers user;
    EmptyRecyclerView recyclerViewUsers;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gradedisplay, container, false);
        spnrSubject = (Spinner) view.findViewById(R.id.gradeFragmentSpinnerSubject);
        spnrSubjectTaskId = (Spinner) view.findViewById(R.id.gradeFragmentSpinnerTasks);
        user = new RegisterUsers();
        prefsManager = new PreferenceManager(getActivity());
        recyclerViewUsers = (com.saechaol.learningapp.ui.view.EmptyRecyclerView) view.findViewById(R.id.fragment_display_grade_recyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dividerList)));
        gradeDisplayAdapter = new GradeAdapter(getActivity(), gradeDetails, new OnItemClickListener<GradeTask>() {
            @Override
            public void onItemClick(GradeTask item, int resourceId) {
                if (resourceId == R.id.row_display_grade_imgEditUser) {
                    final Intent intent = new Intent(GradeViewFragment.this.getActivity(), UpdateGradeActivity.class);
                    intent.putExtra("GradeData", item);
                    startActivity(intent);
                }
            }
        });

        gradeDisplayAdapter.setMode(Attributes.Mode.Single);
        recyclerViewUsers.setAdapter(gradeDisplayAdapter);
        recyclerViewUsers.setEmptyView(view.findViewById(R.id.fragment_display_grade_relEmptyView));

        getIntentService();

        // To add all the subjects within a subject spinner.
        GetSubjectAPI getSubjectDetails = new GetSubjectAPI(this.getActivity());
        getSubjectDetails.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gradeDisplayAdapter = new GradeAdapter(getActivity(), new ArrayList<GradeTask>(), new OnItemClickListener<GradeTask>() {
                    @Override
                    public void onItemClick(GradeTask item, int resourceId) {
                        if (resourceId == R.id.row_display_grade_imgEditUser) {
                            final Intent intent = new Intent(GradeViewFragment.this.getActivity(), UpdateGradeActivity.class);
                            intent.putExtra("GradeData", item);
                            startActivity(intent);
                        }
                    }
                });

                gradeDisplayAdapter.setMode(Attributes.Mode.Single);
                recyclerViewUsers.setAdapter(gradeDisplayAdapter);
                recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_grade_relEmptyView));

                GetTaskDetailsBySubjectAPI getTaskDetailsBySubjectAPI = new GetTaskDetailsBySubjectAPI(GradeViewFragment.this.getActivity());
                getTaskDetailsBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnrSubjectTaskId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerTaskPosition = position;
                gradeDisplayAdapter = new GradeAdapter(getActivity(), new ArrayList<GradeTask>(), new OnItemClickListener<GradeTask>() {
                    @Override
                    public void onItemClick(GradeTask item, int resourceId) {
                        if (resourceId == R.id.row_display_grade_imgEditUser) {
                            final Intent intent = new Intent(GradeViewFragment.this.getActivity(), UpdateGradeActivity.class);
                            intent.putExtra("GradeData", item);
                            startActivity(intent);
                        }
                    }
                });

                gradeDisplayAdapter.setMode(Attributes.Mode.Single);
                recyclerViewUsers.setAdapter(gradeDisplayAdapter);
                recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_grade_relEmptyView));

                GetStudentByTaskAPI getStudentByTaskAPI = new GetStudentByTaskAPI(GradeViewFragment.this.getActivity());
                getStudentByTaskAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (taskDetails != null && taskDetails.size() > 0) {
            GetStudentByTaskAPI getStudentByTaskAPI = new GetStudentByTaskAPI(GradeViewFragment.this.getActivity());
            getStudentByTaskAPI.execute();
        }

    }

    void getIntentService() {
        Intent previous = GradeViewFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            user.userId = (String) bundle.get("userId");
            user.username = (String) bundle.get("userName");
            user.userType = (String) bundle.get("userType");
        }
    }

    class GetStudentByTaskAPI extends AsyncTask<Void, Void, List<GradeTask>> {
        Context context;
        String subjectId = "";
        String taskId = "";

        public GetStudentByTaskAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
            taskId = taskDetails.get(spnrSubjectTaskId.getSelectedItemPosition()).taskId + "";
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<GradeTask> taskDetails) {
            //check if the call to api passed
            gradeDetails = new ArrayList<GradeTask>();
            if (taskDetails != null) {
                gradeDetails = taskDetails;
            } else {
                ((HomeActivity) getActivity()).showSnackBar(getString(R.string.serverError), getView().findViewById(R.id.fragment_display_grade_coordinatorLayout));
            }
            gradeDisplayAdapter = new GradeAdapter(context, gradeDetails, new OnItemClickListener<GradeTask>() {
                @Override
                public void onItemClick(GradeTask item, int resourceId) {
                    if (resourceId == R.id.row_display_grade_imgEditUser) {
                        final Intent intent = new Intent(GradeViewFragment.this.getActivity(), UpdateGradeActivity.class);
                        intent.putExtra("GradeData", item);
                        startActivity(intent);
                    }
                }
            });

            gradeDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(gradeDisplayAdapter);
            recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_grade_relEmptyView));
        }

        @Override
        protected List<GradeTask> doInBackground(Void... params) {

            try {
                Call<List<GradeTask
                        >> callTaskData = Api.getClient().getGrades(taskId, subjectId);
                Response<List<GradeTask>> responseTaskData = callTaskData.execute();
                if (responseTaskData.isSuccessful() && responseTaskData.body() != null) {
                    return responseTaskData.body();
                } else {
                    return null;
                }

            } catch (MalformedURLException e) {
                return null;


            } catch (IOException e) {
                return null;
            }

        }
    }

    class GetTaskDetailsBySubjectAPI extends AsyncTask<Void, Void, List<TaskDetails>> {
        Context context;
        String subjectId = "";

        public GetTaskDetailsBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<TaskDetails> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                taskDetails = new ArrayList<TaskDetails>();
                for (int i = 0; i < taskDetails2.size(); i++) {
                    if (isTaskFinished(taskDetails2.get(i).getScheduleEndTime())) {
                        taskDetails.add(taskDetails2.get(i));
                    }
                }
                subjectTopic = new String[taskDetails.size()];
                for (int i = 0; i < taskDetails.size(); i++) {
                    if (!TextUtils.isEmpty(taskDetails.get(i).getTitle())) {
                        subjectTopic[i] = taskDetails.get(i).getTitle();
                    } else {
                        subjectTopic[i] = getDateString(taskDetails.get(i).getScheduleStartTime(), taskDetails.get(i).getScheduleEndTime());
                    }
                }

                spnrSubjectTaskId = (Spinner) view.findViewById(R.id.gradeFragmentSpinnerTasks);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, subjectTopic);
                spnrSubjectTaskId.setAdapter(arrayAdapter);
            }
        }

        @Override
        protected List<TaskDetails> doInBackground(Void... params) {
            try {
                Call<List<TaskDetails>> callTaskData = Api.getClient().getTasksBySubject(subjectId);
                Response<List<TaskDetails>> responseTaskData = callTaskData.execute();
                if (responseTaskData.isSuccessful() && responseTaskData.body() != null) {
                    return responseTaskData.body();
                } else {
                    return null;
                }

            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }


        }
    }

    class GetSubjectAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetSubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Getting Subjects...");
        }

        @Override
        protected void onPostExecute(List<SubjectDetails> userDetails2) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (userDetails2 != null) {
                subjectDetails = new ArrayList<>();

                subjectDetails = filter(userDetails2, filterPredicate);
                if (subjectDetails.size() > 0) {

                    subjectID = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        subjectID[i] = subjectDetails.get(i).subjectId;
                    }
                    //add the subject ids to the spinner
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, subjectID);
                    spnrSubject.setAdapter(arrayAdapter);
                } else {

                }
            }
        }

        @Override
        protected List<SubjectDetails> doInBackground(Void... params) {
            try {
                if (prefsManager.getStringData("userType").equalsIgnoreCase(UserTypeData.STUDENT)) {
                    Call<ArrayList<SubjectDetails>> callSubjectData = Api.getClient().getSubjectForStudent(prefsManager.getStringData("userName"));
                    Response<ArrayList<SubjectDetails>> responseSubjectData = callSubjectData.execute();
                    if (responseSubjectData.isSuccessful() && responseSubjectData.body() != null) {
                        return responseSubjectData.body();
                    } else {
                        return null;
                    }
                } else {
                    Call<List<SubjectDetails>> callSubjectData = Api.getClient().getAllSubject();
                    Response<List<SubjectDetails>> responseSubjectData = callSubjectData.execute();
                    if (responseSubjectData.isSuccessful() && responseSubjectData.body() != null) {
                        return responseSubjectData.body();
                    } else {
                        return null;
                    }
                }


            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }
        }
    }

    Predicate<SubjectDetails> filterPredicate = new Predicate<SubjectDetails>() {
        public boolean apply(SubjectDetails obj) {
            if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN) || prefsManager.getStringData("userType").equals(UserTypeData.STUDENT)) {
                return true;
            } else if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getInstructorId().equals(prefsManager.getStringData("userName"))) {
                return true;
            }
            return false;
        }

    };

    public static <T> ArrayList<T> filter(Collection<T> source, Predicate<T> predicate) {
        ArrayList<T> result = new ArrayList<T>();
        for (T element : source) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    private String getDateString(String startDateString, String endDateString) {
        Calendar calendar = null;
        String durationString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date date = dateFormat.parse(startDateString);

            calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dateString = calendar.get(Calendar.DATE) > 9 ? calendar.get(Calendar.DATE) + "" : "0" + calendar.get(Calendar.DATE);
            String monthString = (calendar.get(Calendar.MONTH) + 1) > 9 ? ((calendar.get(Calendar.MONTH) + 1) + "") : ("0" + (calendar.get(Calendar.MONTH) + 1)
            );
            String hourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String minuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));
            Date endDate = dateFormat.parse(endDateString);
            calendar.setTime(endDate);
            String endHourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String endMinuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));

            durationString = monthString + "/" + dateString + "/" + calendar.get(Calendar.YEAR) + "," + hourString + ":" + minuteString + "-" + endHourString + ":" + endMinuteString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return durationString;

    }

    private boolean isTaskFinished(String endDateTime) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date endDate = dateFormat.parse(endDateTime);
            Date currentDate = new Date();
            if (currentDate.getTime() > endDate.getTime()) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

}
