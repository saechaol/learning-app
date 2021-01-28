package com.saechaol.learningapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
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
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.model.UserWithCheckbox;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.adapter.UserAdapter;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class EnrollStudentViewFragment extends Fragment {

    ListView listViewStudent;
    Spinner spnrSubject;

    // To Store the details of the Students
    List<StudentDetails> studentDetails = new ArrayList<>();
    String[] studentUserName;
    String[] studentName;
    String[] studentEmail;
    UserAdapter userDisplayAdapter;

    // To Store the details of the Subjects
    List<SubjectDetails> subjectDetails = new ArrayList<>();
    String[] subjectId;
    String[] subjectTitle;
    String[] subjectDescription;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_displayenrolledstudents, container, false);
        listViewStudent = (ListView) view.findViewById(R.id.studentEnrollDisplayListView);
        spnrSubject = (Spinner) view.findViewById(R.id.studentEnrollSpinnerSubjectId);
        listViewStudent.setEmptyView(view.findViewById(R.id.empty_text_view));

        GetAllSubjectAPI getAllSubjectAPI = new GetAllSubjectAPI(this.getActivity());
        getAllSubjectAPI.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetEnrollBySubjectAPI getEnrollBySubjectAPI = new GetEnrollBySubjectAPI(EnrollStudentViewFragment.this.getActivity());
                getEnrollBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    class GetAllSubjectAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetAllSubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Fetching Subjects...");
        }

        @Override
        protected void onPostExecute(List<SubjectDetails> listSubjectDetail) {

            ((HomeActivity) getActivity()).hideProgressDialog();
            subjectDetails = new ArrayList<>();
            if (listSubjectDetail != null && listSubjectDetail.size() > 0) {
                subjectDetails = listSubjectDetail;
                subjectId = new String[subjectDetails.size()];
                subjectTitle = new String[subjectDetails.size()];
                subjectDescription = new String[subjectDetails.size()];
                for (int i = 0; i < subjectDetails.size(); i++) {
                    subjectId[i] = subjectDetails.get(i).subjectId;
                    subjectTitle[i] = subjectDetails.get(i).title;
                    subjectTitle[i] += "    (" + subjectDetails.get(i).subjectId + ")";
                    subjectDescription[i] = subjectDetails.get(i).description;
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, subjectId);
                spnrSubject.setAdapter(arrayAdapter);
            } else {
                ((HomeActivity) getActivity()).showSnackBar("There are no subjects.", getView().findViewById(R.id.fragment_enroll_display_coordinatorLayout));
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

    class GetEnrollBySubjectAPI  extends AsyncTask<Void, Void, List<StudentDetails>> {
        Context context;
        String subj = "";

        public GetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Fetching Students ...");
            subj = spnrSubject.getSelectedItem().toString();
        }

        @Override
        protected void onPostExecute(List<StudentDetails> apiResponse) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            if (apiResponse != null) {
                studentDetails = apiResponse;
                studentUserName = new String[studentDetails.size()];
                studentName = new String[studentDetails.size()];
                studentEmail = new String[studentDetails.size()];
                for (int i = 0; i < studentDetails.size(); i++) {
                    studentUserName[i] = studentDetails.get(i).studentId;
                    studentName[i] = studentDetails.get(i).lastName + ", " + studentDetails.get(i).firstName;
                    studentName[i] += "    (" + studentDetails.get(i).studentId + ")";
                    studentEmail[i] = studentDetails.get(i).email;
                }
                List<UserWithCheckbox> listUserDisplayCheckb = new ArrayList<>();
                for (int i = 0; i < studentDetails.size(); i++) {
                    final UserWithCheckbox usersDisplayProvider = new UserWithCheckbox(studentName[i], studentEmail[i], false, studentUserName[i]);
                    listUserDisplayCheckb.add(usersDisplayProvider);
                }
                userDisplayAdapter = new UserAdapter(context, listUserDisplayCheckb);
                listViewStudent = (ListView) view.findViewById(R.id.studentEnrollDisplayListView);
                listViewStudent.setAdapter(userDisplayAdapter);
            } else {
                userDisplayAdapter = new UserAdapter(context, new ArrayList<UserWithCheckbox>());
                listViewStudent = (ListView) view.findViewById(R.id.studentEnrollDisplayListView);
                listViewStudent.setAdapter(userDisplayAdapter);
            }
        }

        @Override
        protected List<StudentDetails> doInBackground(Void... params) {
            try {
                Call<List<StudentDetails>> callStudentData = Api.getClient().getEnrollBySubject(subj);
                Response<List<StudentDetails>> responseStudentData = callStudentData.execute();
                if (responseStudentData.isSuccessful() && responseStudentData.body() != null) {
                    return responseStudentData.body();
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
    
}
