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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.model.StudentEnrollmentPostData;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.model.UserWithCheckbox;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.ui.adapter.UserCheckboxAdapter;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StudentDisenrollFragment extends Fragment {

    ListView listViewStudent;
    Spinner spnrSubject;
    Button btnDisEnroll;

    List<StudentDetails> studentDetails = new ArrayList<>();
    ArrayList<String> userNames;
    String[] studentUserName;
    String[] studentName;
    String[] studentEmail;
    UserCheckboxAdapter userDisplayAdapter;

    List<SubjectDetails> subjectDetails = new ArrayList<>();
    String[] subjectId;
    String[] subjectTitle;
    String[] subjectDescription;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_disenrollstudent, container, false);
        listViewStudent = (ListView) view.findViewById(R.id.studentDisenrollDisplayListView);
        spnrSubject = (Spinner) view.findViewById(R.id.studentDisenrollSpinnerSubjectId);
        btnDisEnroll = (Button) view.findViewById(R.id.studentDisenrollButtonDisenroll);
        listViewStudent.setEmptyView(view.findViewById(R.id.empty_text_view));

        //Flow of the API calls,
        //1.) Fetch all the Subjects.
        //2.) Fetch all the enrolled Students for the selected subject. By default, the system takes 1st one.
        //3.) Select the students and enrolled them, if needed.

        GetAllSubjectAPI getAllSubjectAPI = new GetAllSubjectAPI(this.getActivity());
        getAllSubjectAPI.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetEnrollBySubjectAPI getEnrollBySubjectAPI = new GetEnrollBySubjectAPI(StudentDisenrollFragment.this.getActivity());
                getEnrollBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDisEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDisplayAdapter != null && userDisplayAdapter.getCount() > 0) {
                    int listSize = userDisplayAdapter.getCount();
                    UserWithCheckbox userDisplayCheckbxProvider;
                    userNames = new ArrayList<>();
                    for (int i = 0; i < listSize; i++) {
                        userDisplayCheckbxProvider = (UserWithCheckbox) userDisplayAdapter.getItem(i);
                        if (userDisplayCheckbxProvider.getCheck()) {
                            userNames.add(userDisplayCheckbxProvider.getUserId());
                        }
                    }
                    // if the students are selected from the list then call the API.
                    if (userNames.size() > 0){
                        DisEnrollStudentAPI disenrollment = new DisEnrollStudentAPI(StudentDisenrollFragment.this.getActivity());
                        disenrollment.execute();
                    }
                }
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
                //add the subject ids to the spinner
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, subjectId);
                spnrSubject.setAdapter(arrayAdapter);
            } else {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new String[]{});
                spnrSubject.setAdapter(arrayAdapter);
                ((HomeActivity) getActivity()).showSnackBar("There is no subject to Disenroll.", getView().findViewById(R.id.fragment_deenroll_student_coordinatorLayout));
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


    //this class will get the student details and add them to listview item by item
    class GetEnrollBySubjectAPI extends AsyncTask<Void, Void, List<StudentDetails>> {
        Context context;
        String subj = "";

        public GetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Fetching Students...");
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
                List<UserWithCheckbox> listUserDisplayCheckb = new ArrayList<UserWithCheckbox>();
                for (int i = 0; i < studentDetails.size(); i++) {
                    final UserWithCheckbox usersDisplayProvider = new UserWithCheckbox(studentName[i], studentEmail[i], false, studentUserName[i]);
                    listUserDisplayCheckb.add(usersDisplayProvider);
                }
                //fill the items in the listview with a customized adapter
                userDisplayAdapter = new UserCheckboxAdapter(context, listUserDisplayCheckb);
                listViewStudent = (ListView) view.findViewById(R.id.studentDisenrollDisplayListView);
                listViewStudent.setAdapter(userDisplayAdapter);
            } else {
                userDisplayAdapter = new UserCheckboxAdapter(context, new ArrayList<UserWithCheckbox>());
                listViewStudent = (ListView) view.findViewById(R.id.studentDisenrollDisplayListView);
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

    class DisEnrollStudentAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String idSubjectData;
        ArrayList<String> userNameData;

        public DisEnrollStudentAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            idSubjectData = spnrSubject.getSelectedItem().toString();
            userNameData = userNames;
        }

        @Override
        protected void onPostExecute(String statusCode) {
            if (statusCode.equals("disenrolled"))
            {
                GetEnrollBySubjectAPI getEnrollBySubjectAPI = new GetEnrollBySubjectAPI(StudentDisenrollFragment.this.getActivity());
                getEnrollBySubjectAPI.execute();
                ((HomeActivity) getActivity()).showSnackBar("The students have been Disenrolled to the subject.", view.findViewById(R.id.fragment_deenroll_student_coordinatorLayout));
            } else {
                ((HomeActivity) getActivity()).showSnackBar("Error while Disenrolling student.", view.findViewById(R.id.fragment_deenroll_student_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < userNameData.size(); i++) {
                final StudentEnrollmentPostData postData = new StudentEnrollmentPostData();
                postData.setInstructorId("");
                postData.setSubjectId(idSubjectData);
                postData.setStudentId(userNameData.get(i));
                try {
                    Call<StudentEnrollmentPostData> callEnrollSubjectData = Api.getClient().deEnrollBySubject(postData);
                    Response<StudentEnrollmentPostData> responseSubjectData = callEnrollSubjectData.execute();
                } catch (MalformedURLException e) {
                    return null;
                } catch (IOException e) {
                    return null;
                }
            }
            return "disenrolled";
        }
    }

}
