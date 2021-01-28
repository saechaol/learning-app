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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TaskViewFragment extends Fragment {

    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();
    String[] description;

    String[] idSubject;
    PreferenceManager prefsManager;

    Spinner spinnerSubject;

    List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
    ListView listViewTasks;
    TaskUpdateAdapter taskDisplayAdapter;


    View view;

    RegisterUsers register;

    // extract the extras that was sent from the previous intent
    void getExtra() {
        Intent previous = TaskViewFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            register.userId = (String) bundle.get("userId");
            register.username = (String) bundle.get("userName");
            register.userType = (String) bundle.get("userType");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasksdisplay, container, false);
        spinnerSubject = (Spinner) view.findViewById(R.id.taskSubjectSpinner);
        prefsManager = new PreferenceManager(getActivity());
        listViewTasks = (ListView) view.findViewById(R.id.taskDisplayListView);
        listViewTasks.setEmptyView(view.findViewById(R.id.empty_text_view));
        register = new RegisterUsers();

        // extract the extras that was sent from the previous intent
        getExtra();

        GetAllSubjectAPI getAllSubjectAPI = new GetAllSubjectAPI(this.getActivity());
        getAllSubjectAPI.execute();


        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (register.userType.equals("instructor") || register.userType.equals("admin")) {
                    GetTaskDetailsAPI getTaskDetailsAPI = new GetTaskDetailsAPI(TaskViewFragment.this.getActivity());
                    getTaskDetailsAPI.execute();
                } else if (register.userType.equals("student")) {
                    GetTaskDetailsStudenteAPI getTaskDetailsStudenteAPI = new GetTaskDetailsStudenteAPI(TaskViewFragment.this.getActivity());
                    getTaskDetailsStudenteAPI.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    //this class will get the task details and add them to listview item by item
    class GetTaskDetailsStudenteAPI extends AsyncTask<Void, Void, List<TaskDetails>> {
        Context context;
        String subjectId = "";
        String stdId;

        public GetTaskDetailsStudenteAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spinnerSubject.getSelectedItem().toString();
            stdId = register.username;
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<TaskDetails> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                taskDetails = taskDetails2;

                //fill the items in the listview with a customized adapter
                taskDisplayAdapter = new TaskUpdateAdapter(context,false, taskDetails);
                listViewTasks.setAdapter(taskDisplayAdapter);


            } else {

            }
        }

        @Override
        protected List<TaskDetails> doInBackground(Void... params) {
            try {
                Call<List<TaskDetails>> callTaskData = Api.getClient().getListTaskForStudent(subjectId, stdId);
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

    //this class will get the task details and add them to listview item by item
    class GetTaskDetailsAPI extends AsyncTask<Void, Void, List<TaskDetails>> {
        Context context;
        String subjectId = "";

        public GetTaskDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spinnerSubject.getSelectedItem().toString();
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<TaskDetails> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                taskDetails = taskDetails2;

                //fill the items in the listview with a customized adapter
                listViewTasks = (ListView) view.findViewById(R.id.taskDisplayListView);
                taskDisplayAdapter = new TaskUpdateAdapter(context,false ,taskDetails);
                listViewTasks.setAdapter(taskDisplayAdapter);

            } else {

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

    //this class will get the subject ids and add then to the spinner
    class GetAllSubjectAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetAllSubjectAPI(Context ctx) {
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

                    idSubject = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        idSubject[i] = subjectDetails.get(i).subjectId;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, idSubject);
                    spinnerSubject.setAdapter(arrayAdapter);
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
    
    Predicate<SubjectDetails> filterPredicate = (obj) -> {
        if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)||prefsManager.getStringData("userType").equals(UserTypeData.STUDENT)) {
            return true;
        } else if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getInstructorId().equals(prefsManager.getStringData("userName"))) {
            return true;
        }
        return false;
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
    
}
