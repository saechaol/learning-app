package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.adapter.TaskUpdateAdapter;
import com.saechaol.learningapp.util.Predicate;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TaskInProgressViewFragment extends Fragment {

    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();
    String[] description;

    String[] strSubjectID;
    PreferenceManager prefsManager;
    Spinner spnrSubject;

    List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
    ListView listViewInproccessTask;
    TaskUpdateAdapter taskDisplayAdapter;

    View view;

    RegisterUsers user;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasksdisplay,container,false);
        spnrSubject =(Spinner) view.findViewById(R.id.taskSubjectSpinner);
        listViewInproccessTask = (ListView) view.findViewById(R.id.taskDisplayListView);
        listViewInproccessTask.setEmptyView(view.findViewById(R.id.empty_text_view));
        user = new RegisterUsers();
        prefsManager=new PreferenceManager(getActivity());

        getIntentService();

        GetSubjectByStudentAPI getSubjectByStudentAPI=new GetSubjectByStudentAPI(this.getActivity());
        getSubjectByStudentAPI.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(user.userType.equals("instructor") || user.userType.equals("admin")) {
                    GetTasksBySubjectAPI getTasksBySubjectAPI = new GetTasksBySubjectAPI(TaskInProgressViewFragment.this.getActivity());
                    getTasksBySubjectAPI.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    void getIntentService()
    {
        Intent previous= TaskInProgressViewFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if(bundle!=null)
        {
            user.userId =(String) bundle.get("userId");
            user.username =(String) bundle.get("userName");
            user.userType =(String) bundle.get("userType");
        }
    }

    class GetTasksBySubjectAPI extends AsyncTask<Void,Void,List<TaskDetails>>
    {
        Context context;
        String subjectId="";
        public GetTasksBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
        }

        @Override
        protected void onPostExecute(List<TaskDetails> taskDetails2) {

            if(taskDetails2 != null ) {
                taskDetails = taskDetails2;

                final ArrayList<TaskDetails> filterInProcess=new ArrayList<TaskDetails>();
                for (TaskDetails task:
                        taskDetails) {

                    if(isTaskInProcess(task.getScheduleStartTime(),task.getScheduleEndTime())){
                        filterInProcess.add(task);
                    }

                }
                taskDisplayAdapter = new TaskUpdateAdapter(context,prefsManager.getStringData("userType").equalsIgnoreCase(UserTypeData.INSTRUCTOR), filterInProcess);
                listViewInproccessTask.setAdapter(taskDisplayAdapter);
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
            ((HomeActivity) getActivity()).showProgressDialog("Getting Subjects...");
        }

        @Override
        protected void onPostExecute(List<SubjectDetails> userDetails2) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (userDetails2 != null) {
                subjectDetails = new ArrayList<>();

                subjectDetails = filter(userDetails2, filterPredicate);
                if (subjectDetails.size() > 0) {

                    strSubjectID = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        strSubjectID[i] = subjectDetails.get(i).subjectId;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strSubjectID);
                    spnrSubject.setAdapter(arrayAdapter);
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
            if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)||prefsManager.getStringData("userType").equals(UserTypeData.STUDENT)) {
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


    private boolean isTaskInProcess(String startDateTime,String endDateTime){

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date startDate = dateFormat.parse(startDateTime);

            Date endDate = dateFormat.parse(endDateTime);

            Date currentDate=new Date();

            if(currentDate.getTime()>startDate.getTime()&&currentDate.getTime()<endDate.getTime()){
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}
