package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
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

public class EditTaskFragment extends Fragment {

    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();

    String[] strSubjectId;
    Spinner spinnerSubject;

    List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
    ListView listViewTasks;
    TaskUpdateAdapter taskUpdateAdapter;
    PreferenceManager prefsManager;

    View view;

    private String subIdSel = "";


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        subIdSel="";
        if (args != null && args.containsKey("subId")) {
            subIdSel = args.getString("subId");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_taskupdate, container, false);
        spinnerSubject = (Spinner) view.findViewById(R.id.taskUpdateSpinnerObject);
        listViewTasks = (ListView) view.findViewById(R.id.taskUpdateListView);
        listViewTasks.setEmptyView(view.findViewById(R.id.fragment_updated_task_relEmptyView));
        prefsManager = new PreferenceManager(getActivity());

        getIntentService();

        GetAllSubjectAPI getAllSubjectAPI = new GetAllSubjectAPI(this.getActivity());
        getAllSubjectAPI.execute();


        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetTasksBySubjectAPI getTasksBySubjectAPI = new GetTasksBySubjectAPI(EditTaskFragment.this.getActivity());
                getTasksBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragment = getFragmentManager();
                EditTaskDialog updateTaskDialog = EditTaskDialog.newInstance(EditTaskFragment.this, taskDetails.get(position).taskId + "", taskDetails.get(position).title, taskDetails.get(position).description);

                updateTaskDialog.show(fragment, "updateTaskDialog");
            }
        });

        return view;
    }

    public void refresh() {
        GetTasksBySubjectAPI getTaskDetails = new GetTasksBySubjectAPI(EditTaskFragment.this.getActivity());
        getTaskDetails.execute();
    }

    void getIntentService() {
        Intent previous = EditTaskFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
    }


    class GetTasksBySubjectAPI extends AsyncTask<Void, Void, List<TaskDetails>> {
        Context context;
        String subjectId = "";

        public GetTasksBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spinnerSubject.getSelectedItem().toString();
        }


        @Override
        protected void onPostExecute(List<TaskDetails> details) {
            if (details != null) {
                taskDetails = details;
                taskUpdateAdapter = new TaskUpdateAdapter(context, false, taskDetails);
                listViewTasks.setAdapter(taskUpdateAdapter);
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
        protected void onPostExecute(List<SubjectDetails> details) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (details != null) {
                subjectDetails = new ArrayList<>();

                subjectDetails = filter(details, filterPredicate);

                strSubjectId = new String[subjectDetails.size()];
                int position = 0;
                for (int i = 0; i < subjectDetails.size(); i++) {
                    strSubjectId[i] = subjectDetails.get(i).subjectId;
                    if (strSubjectId[i].equalsIgnoreCase(subIdSel)) {
                        position = i;
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strSubjectId);
                spinnerSubject.setAdapter(arrayAdapter);
                spinnerSubject.setSelection(position);
            } else {

            }
        }

        @Override
        protected List<SubjectDetails> doInBackground(Void... params) {
            try {
                Call<List<SubjectDetails>> callSubjectData = Api.getClient().getAllSubject();
                Response<List<SubjectDetails>> responseSubjectData = callSubjectData.execute();
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

    Predicate<SubjectDetails> filterPredicate = new Predicate<SubjectDetails>() {
        public boolean apply(SubjectDetails obj) {
            if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)) {
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


}
