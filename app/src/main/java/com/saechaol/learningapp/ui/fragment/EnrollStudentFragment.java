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

public class EnrollStudentFragment extends Fragment {

    ListView listViewStudent;
    Spinner spnrSubject;
    Button btnEnroll;

    // To Store the details of the Students
    List<StudentDetails> studentDetails = new ArrayList<>();
    ArrayList<String> userNames;
    String[] studentUserName;
    String[] studentName;
    String[] studentEmail;
    UserCheckboxAdapter userDisplayAdapter;

    // To Store the details of the Subjects
    List<SubjectDetails> subjectDetails = new ArrayList<>();
    String[] subjectId;
    String[] subjectTitle;
    String[] subjectDescription;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_enrollstudent, container, false);
        listViewStudent = (ListView) view.findViewById(R.id.studentEnrollDisplayListView);
        listViewStudent.setEmptyView(view.findViewById(R.id.empty_text_view));
        btnEnroll = (Button) view.findViewById(R.id.studentEnrollButtonEnroll);
        spnrSubject = (Spinner) view.findViewById(R.id.studentEnrollSpinnerSubjectId);

        //Flow of the API calls,
        //1.) Fetch all the Subjects.
        //2.) Fetch all the Disenroll Students for the selected subject. By default, the system takes 1st one.
        //3.) Select the students and enrolled them, if needed.

        GetAllSubjectAPI getAllSubjectAPI = new GetAllSubjectAPI(this.getActivity());
        getAllSubjectAPI.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //this will call the AsyncTask which will bring the student details and filled them on list
                GetDeEnrollBySubjectAPI getDeEnrollBySubjectAPI = new GetDeEnrollBySubjectAPI(EnrollStudentFragment.this.getActivity());
                getDeEnrollBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDisplayAdapter != null && userDisplayAdapter.getCount() > 0) {
                    // the userDisplayadapter include the boolean values and the userName of the student.
                    int listSize = userDisplayAdapter.getCount();//this is the list size
                    UserWithCheckbox userDisplayCheckboxProvider;
                    userNames = new ArrayList<>();
                    for (int i = 0; i < listSize; i++) {
                        userDisplayCheckboxProvider = (UserWithCheckbox) userDisplayAdapter.getItem(i);
                        if (userDisplayCheckboxProvider.getCheck()) {
                            userNames.add(userDisplayCheckboxProvider.getUserId());
                        }
                    }

                    // if the students are selected from the list then call the API.
                    if(userNames.size()>0){
                        EnrollStudentAPI enrollStudent = new EnrollStudentAPI(EnrollStudentFragment.this.getActivity());
                        enrollStudent.execute();
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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, subjectId);
                spnrSubject.setAdapter(arrayAdapter);
            } else {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new String[]{});
                spnrSubject.setAdapter(arrayAdapter);
                ((HomeActivity) getActivity()).showSnackBar("There is no subject to enroll.", getView().findViewById(R.id.fragment_enroll_student_coordinatorLayout));
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


    class GetDeEnrollBySubjectAPI extends AsyncTask<Void, Void, List<StudentDetails>> {
        Context context;
        String subj = "";

        public GetDeEnrollBySubjectAPI(Context ctx) {
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
                List<UserWithCheckbox> listUserDisplayCheckbox = new ArrayList<>();
                for (int i = 0; i < studentDetails.size(); i++) {
                    final UserWithCheckbox usersDisplayProvider = new UserWithCheckbox(studentName[i], studentEmail[i], false, studentUserName[i]);
                    listUserDisplayCheckbox.add(usersDisplayProvider);
                }

                userDisplayAdapter = new UserCheckboxAdapter(context, listUserDisplayCheckbox);
                listViewStudent = (ListView) view.findViewById(R.id.studentEnrollDisplayListView);
                listViewStudent.setAdapter(userDisplayAdapter);

            } else {
                userDisplayAdapter = new UserCheckboxAdapter(context, new ArrayList<UserWithCheckbox>());
                listViewStudent = (ListView) view.findViewById(R.id.studentEnrollDisplayListView);
                listViewStudent.setAdapter(userDisplayAdapter);
            }
        }

        @Override
        protected List<StudentDetails> doInBackground(Void... params) {
            try {
                Call<List<StudentDetails>> callStudentData = Api.getClient().getDeEnrollBySubject(subj);
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

    class EnrollStudentAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String idSubjectData;
        ArrayList<String> userNameData;

        public EnrollStudentAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            idSubjectData = spnrSubject.getSelectedItem().toString();
            userNameData = userNames;
        }

        @Override
        protected void onPostExecute(String statusCode) {

            if (statusCode.equals("enrolled")) //the item is created
            {
                GetDeEnrollBySubjectAPI getDeEnrollBySubjectAPI = new GetDeEnrollBySubjectAPI(EnrollStudentFragment.this.getActivity());
                getDeEnrollBySubjectAPI.execute();
                ((HomeActivity) getActivity()).showSnackBar("The students have been enrolled to the subject.", view.findViewById(R.id.fragment_enroll_student_coordinatorLayout));
            } else {
                ((HomeActivity) getActivity()).showSnackBar("Error while enrolling student.", view.findViewById(R.id.fragment_enroll_student_coordinatorLayout));
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
                    Call<StudentEnrollmentPostData> callEnrollSubjectData = Api.getClient().enrollBySubject(postData);
                    Response<StudentEnrollmentPostData> responseSubjectData = callEnrollSubjectData.execute();

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            return "enrolled";
        }
    }

}
