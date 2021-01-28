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
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.GradeTask;
import com.saechaol.learningapp.model.RegisterUsers;
import com.saechaol.learningapp.model.StudentGrade;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.ui.adapter.GradeStudentAdapter;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GradeStudentViewFragment extends Fragment {

    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();
    String[] idTasks;
    String[] topic;
    String[] description;
    String[] studentId;
    String[] instrGrade;

    String[] idSubject;
    String[] titleSubject;
    String[] descriptionSubject;
    PreferenceManager prefsManager;


    Spinner spinnerSubject;

    List<GradeTask> gradeDetails = new ArrayList<GradeTask>();

    ListView listViewTasks;
    GradeStudentAdapter gradeStudentAdapter;

    private TextView txtAvgGrade;
    View view;
    List<String> gradeList;
    final float[] gradeAmt = new float[]{4.0f, 3.7f, 3.3f, 3.0f, 2.7f, 2.3f, 2.0f, 1.7f, 1.3f, 1.0f, 0.7f, 0.0f};

    RegisterUsers register;

    // extract the extras that was sent from the previous intent
    void getExtra() {
        Intent previous = GradeStudentViewFragment.this.getActivity().getIntent();
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
        view = inflater.inflate(R.layout.fragment_studentgradedisplay, container, false);
        spinnerSubject = (Spinner) view.findViewById(R.id.taskSubjectSpinner);
        listViewTasks = (ListView) view.findViewById(R.id.taskDisplayListView);
        listViewTasks.setEmptyView(view.findViewById(R.id.empty_text_view));
        register = new RegisterUsers();
        final String[] grades = new String[]{"A (4.0)", "A- (3.7)", "B+ (3.3)", "B (3.0)", "B- (2.7)", "C+ (2.3)", "C (2.0)", "C- (1.7)", "D+ (1.3)", "D (1.0)", "D- (0.7)", "F - (0.0)", "Not Graded"};

        gradeList = Arrays.asList(grades);

        prefsManager = new PreferenceManager(getActivity());
        txtAvgGrade = (TextView) view.findViewById(R.id.fragment_display_grade_txtTotalGrade);
        // extract the extras that was sent from the previous intent
        getExtra();

        GetSubjectByStudentAPI getSubjectByStudentAPI = new GetSubjectByStudentAPI(this.getActivity());
        getSubjectByStudentAPI.execute();

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtAvgGrade.setText("Not Available");

                GetGradeDetailsAPI getGradeDetailsAPI= new GetGradeDetailsAPI(GradeStudentViewFragment.this.getActivity());
                getGradeDetailsAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    //this class will get the grade details and add them to listview item by item
    class GetGradeDetailsAPI extends AsyncTask<Void, Void, List<GradeTask>> {
        Context context;
        String subjectId2 = "";
        String stdId = "";

        public GetGradeDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId2 = spinnerSubject.getSelectedItem().toString();
            stdId = register.username;
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<GradeTask> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                gradeDetails = new ArrayList<GradeTask>();
                for (int i = 0; i < taskDetails2.size(); i++) {
                    if (isTaskFinished(taskDetails2.get(i).getScheduleEndTime())) {
                        gradeDetails.add(taskDetails2.get(i));
                    }
                }

                idTasks = new String[gradeDetails.size()];
                topic = new String[gradeDetails.size()];
                description = new String[gradeDetails.size()];
                studentId = new String[gradeDetails.size()];
                instrGrade = new String[gradeDetails.size()];
                for (int i = 0; i < gradeDetails.size(); i++) {
                    idTasks[i] = gradeDetails.get(i).taskId + "";
                    topic[i] = gradeDetails.get(i).title;
                    description[i] = gradeDetails.get(i).description;
                    studentId[i] = gradeDetails.get(i).studentId;
                    instrGrade[i] = gradeDetails.get(i).instructorGrade;
                }

                gradeStudentAdapter = new GradeStudentAdapter(context, R.layout.rowlayout_grade);
                listViewTasks.setAdapter(gradeStudentAdapter);
                for (int i = 0; i < gradeDetails.size(); i++) {
                    StudentGrade gradeStdDisplayProvider = new StudentGrade(idTasks[i].toString(), topic[i].toString(), instrGrade[i].toString());
                    gradeStudentAdapter.add(gradeStdDisplayProvider);
                }

                if (gradeDetails != null && gradeDetails.size() > 0) {
                    float gradeAvg = 0f;
                    boolean hasOneGrade = false;
                    int totalData = 0;
                    for (int i = 0; i < gradeDetails.size(); i++) {
                        int index = gradeList.indexOf(gradeDetails.get(i).instructorGrade);
                        if (index != -1 && index != gradeList.size() - 1) {
                            totalData++;
                            gradeAvg = gradeAvg + gradeAmt[index];

                            hasOneGrade = true;

                        }

                    }
                    if (hasOneGrade) {
                        gradeAvg = gradeAvg / totalData;

                        for (int i = 0; i < gradeAmt.length; i++) {
                            if (gradeAvg >= gradeAmt[i]) {
                                txtAvgGrade.setText(gradeList.get(i));
                                break;
                            }
                        }
                    }

                }
            }
        }

        @Override
        protected List<GradeTask> doInBackground(Void... params) {
            try {
                Call<List<GradeTask>> callSubjectData = Api.getClient().getGradesForStudent(stdId, subjectId2);
                Response<List<GradeTask>> responseSubjectData = callSubjectData.execute();
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


    //this class will get the subject ids and add then to the spinner
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
            } else {

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
