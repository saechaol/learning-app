package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.RegisterUsers;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.model.TaskDetails;
import com.saechaol.learningapp.ui.adapter.TaskInProgressAdapter;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

public class InProgressFragment extends Fragment {

    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();

    String[] strSubjectId;
    String[] strSubjectTitle;
    String[] strSubjectDescription;
    String[] description;

    List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
    ListView listViewTasks;
    Spinner spnrSubject;

    TaskInProgressAdapter taskDisplayAdapter;
    PreferenceManager prefsManager;

    Handler handler = new Handler();
    Timer timer;
    View view;

    RegisterUsers user;

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (taskDisplayAdapter != null && taskDisplayAdapter.getCount() > 0) {
            startTimer();

        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();

        }

        timer = new Timer("UpdateProgress");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (taskDisplayAdapter != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            taskDisplayAdapter.updateTaskProgress();
                        }
                    });
                }

            }
        }, 000, 3000);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasksinprocess, container, false);
        spnrSubject = (Spinner) view.findViewById(R.id.inProgressTaskSpinnerObject);
        listViewTasks = (ListView) view.findViewById(R.id.inProgressTaskListView);
        listViewTasks.setEmptyView(view.findViewById(R.id.empty_text_view));
        user = new RegisterUsers();

        getIntentService();
        prefsManager = new PreferenceManager(getActivity());

        GetSubjectByStudentAPI getSubjectByStudentAPI = new GetSubjectByStudentAPI(this.getActivity());
        getSubjectByStudentAPI.execute();


        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                GetTasksBySubjectAPI getTasksBySubjectAPI = new GetTasksBySubjectAPI(InProgressFragment.this.getActivity());
                getTasksBySubjectAPI.execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    void getIntentService() {
        Intent previous = InProgressFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            user.userId = (String) bundle.get("userId");
            user.username = (String) bundle.get("userName");
            user.userType = (String) bundle.get("userType");
        }
    }


    class GetTasksBySubjectAPI extends AsyncTask<Void, Void, List<TaskDetails>> {
        Context context;
        String subjectId = "";

        public GetTasksBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
        }

        @Override
        protected void onPostExecute(List<TaskDetails> taskDetails2) {
            final ArrayList<TaskDetails> filterInProcess = new ArrayList<TaskDetails>();

            if (taskDetails2 != null && taskDetails2.size() > 0) {
                taskDetails = taskDetails2;


                for (TaskDetails task :
                        taskDetails) {

                    if (isTaskInProcess(task.getScheduleStartTime(), task.getScheduleEndTime())) {
                        filterInProcess.add(task);
                    }

                }
                if (filterInProcess != null && filterInProcess.size() > 0) {
                    taskDisplayAdapter = new TaskInProgressAdapter(context, filterInProcess);
                    listViewTasks.setAdapter(taskDisplayAdapter);
                    startTimer();

                } else {
                    if (timer != null) {
                        timer.cancel();

                    }
                    taskDisplayAdapter = new TaskInProgressAdapter(context, filterInProcess);
                    listViewTasks.setAdapter(taskDisplayAdapter);
                }
            } else {
                if (timer != null) {
                    timer.cancel();

                }
                taskDisplayAdapter = new TaskInProgressAdapter(context, filterInProcess);
                listViewTasks.setAdapter(taskDisplayAdapter);

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

    class GetSubjectByStudentAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetSubjectByStudentAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<SubjectDetails> userDetails2) {
            if (userDetails2 != null) {
                subjectDetails = userDetails2;
                strSubjectId = new String[subjectDetails.size()];
                strSubjectTitle = new String[subjectDetails.size()];
                strSubjectDescription = new String[subjectDetails.size()];
                for (int i = 0; i < subjectDetails.size(); i++) {
                    strSubjectId[i] = subjectDetails.get(i).subjectId;
                    strSubjectTitle[i] = subjectDetails.get(i).title;
                    strSubjectTitle[i] += "    (" + subjectDetails.get(i).subjectId + ")";

                    strSubjectDescription[i] = subjectDetails.get(i).description;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strSubjectId);
                spnrSubject.setAdapter(arrayAdapter);
            }
        }

        @Override
        protected List<SubjectDetails> doInBackground(Void... params) {
            try {
                Call<ArrayList<SubjectDetails>> callSubjectData = Api.getClient().getSubjectForStudent(prefsManager.getStringData("userName"));
                Response<ArrayList<SubjectDetails>> responseSubjectData = callSubjectData.execute();
                if (responseSubjectData.isSuccessful() && responseSubjectData.body() != null) {
                    return responseSubjectData.body();
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

    private boolean isTaskInProcess(String startDateTime, String endDateTime) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date startDate = dateFormat.parse(startDateTime);

            Date endDate = dateFormat.parse(endDateTime);

            Date currentDate = new Date();

            if (currentDate.getTime() > startDate.getTime() && currentDate.getTime() < endDate.getTime()) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

}
