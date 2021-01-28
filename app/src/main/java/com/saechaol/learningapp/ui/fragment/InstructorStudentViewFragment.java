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

public class InstructorStudentViewFragment extends Fragment {

    ListView listViewStudent;

    List<StudentDetails> userDetails = new ArrayList<StudentDetails>();
    String[] userName;
    String[] user_FLnames;
    String[] user_Email;
    UserAdapter userDisplayAdapter;

    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();
    String[] idSubject;
    String[] titleSubject;
    String[] descriptionSubject;

    Spinner spinnerSubject;

    PreferenceManager prefsManager;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_displayenrolledstudents, container, false);
        listViewStudent = (ListView) view.findViewById(R.id.studentEnrollDisplayListView);
        spinnerSubject = (Spinner) view.findViewById(R.id.studentEnrollSpinnerSubjectId);
        listViewStudent.setEmptyView(view.findViewById(R.id.empty_text_view));
        prefsManager = new PreferenceManager(getActivity());

        GetAllSubjectAPI getAllSubjectAPI = new GetAllSubjectAPI(this.getActivity());
        getAllSubjectAPI.execute();

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetEnrollBySubjectAPI getEnrollBySubjectAPI = new GetEnrollBySubjectAPI(InstructorStudentViewFragment.this.getActivity());
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
            ((HomeActivity) getActivity()).showProgressDialog("Getting Subject Data...");

        }

        @Override
        protected void onPostExecute(List<SubjectDetails> listSubjectDetail) {

            ((HomeActivity) getActivity()).hideProgressDialog();

            if (listSubjectDetail != null && listSubjectDetail.size() > 0) {
                subjectDetails = new ArrayList<>();
                subjectDetails = filter(listSubjectDetail, filterPredicate);
                if (subjectDetails.size() > 0) {
                    idSubject = new String[subjectDetails.size()];
                    titleSubject = new String[subjectDetails.size()];
                    descriptionSubject = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        idSubject[i] = subjectDetails.get(i).subjectId;
                        titleSubject[i] = subjectDetails.get(i).title;
                        titleSubject[i] += "    (" + subjectDetails.get(i).subjectId + ")";

                        descriptionSubject[i] = subjectDetails.get(i).description;
                    }
                    //add the subject ids to the spinner
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, idSubject);
                    spinnerSubject.setAdapter(arrayAdapter);
                }else{
                    ((HomeActivity) getActivity()).showSnackBar("There are no subjects assigned.", getView().findViewById(R.id.fragment_enroll_display_coordinatorLayout));

                }

            } else {
                ((HomeActivity) getActivity()).showSnackBar("There are no subjects assigned.", getView().findViewById(R.id.fragment_enroll_display_coordinatorLayout));

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
            if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getInstructorId().equals(prefsManager.getStringData("userName"))) {
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

    class GetEnrollBySubjectAPI extends AsyncTask<Void, Void, List<StudentDetails>> {
        Context context;
        String subj = "";

        public GetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((HomeActivity) getActivity()).showProgressDialog("Getting Student Data...");
            subj = spinnerSubject.getSelectedItem().toString();

        }

        //this method will be called after the doInBackground get all the student users from the webapi
        @Override
        protected void onPostExecute(List<StudentDetails> userDetails2) {
            ((HomeActivity) getActivity()).hideProgressDialog();

            //check if the call to api passed
            if (userDetails2 != null) {
                userDetails = userDetails2;
                userName = new String[userDetails.size()];
                user_FLnames = new String[userDetails.size()];
                user_Email = new String[userDetails.size()];
                for (int i = 0; i < userDetails.size(); i++) {
                    userName[i] = userDetails.get(i).studentId;
                    user_FLnames[i] = userDetails.get(i).lastName + ", " + userDetails.get(i).firstName;
                    user_FLnames[i] += "    (" + userDetails.get(i).studentId + ")";

                    user_Email[i] = userDetails.get(i).email;
                }
                List<UserWithCheckbox> listUserDisplayCheckb = new ArrayList<UserWithCheckbox>();
                for (int i = 0; i < userDetails.size(); i++) {
                    final UserWithCheckbox usersDisplayProvider = new UserWithCheckbox(user_FLnames[i], user_Email[i], false, userName[i]);
                    listUserDisplayCheckb.add(usersDisplayProvider);
                }
                //fill the items in the listview with a customized adapter
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
