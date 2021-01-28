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
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.GradeTask;
import com.saechaol.learningapp.model.RegisterUsers;
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.util.Predicate;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GradeGraphFragment extends Fragment {

    List<SubjectDetails> subjectDetails = new ArrayList<>();
    PreferenceManager prefsManager;
    protected BarChart mChart;
    String[] subjectID;
    String[] studentName;

    Spinner spnrStudent;
    Spinner spnrSubject;


    List<StudentDetails> studentDetails = new ArrayList<>();

    List<GradeTask> gradeDetails = new ArrayList<>();

    int spinnerStudentPos = 0;

    View view;

    RegisterUsers register;
    final String[] grades = new String[]{"F - (0.0)", "D- (0.7)", "D (1.0)", "D+ (1.3)", "C- (1.7)", "C (2.0)", "C+ (2.3)", "B- (2.7)", "B (3.0)", "B+ (3.3)", "A- (3.7)", "A (4.0)"};
    final String[] chartGrades = new String[]{"","F - (0.0)", "D- (0.7)", "D (1.0)", "D+ (1.3)", "C- (1.7)", "C (2.0)", "C+ (2.3)", "B- (2.7)", "B (3.0)", "B+ (3.3)", "A- (3.7)", "A (4.0)"};

    // extract the extras that was sent from the previous intent
    void getExtra() {
        Intent previous = GradeGraphFragment.this.getActivity().getIntent();
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
        view = inflater.inflate(R.layout.fragment_gradegraph, container, false);

        spnrSubject = (Spinner) view.findViewById(R.id.taskSubjectSpinner);
        spnrStudent = (Spinner) view.findViewById(R.id.taskStudentSpinner);
        register = new RegisterUsers();
        prefsManager = new PreferenceManager(getActivity());


        // extract the extras that was sent from the previous intent
        getExtra();

        // To add all the subjects within a subject spinner.
        GetSubjectAPI getSubjectDetails = new GetSubjectAPI(this.getActivity());
        getSubjectDetails.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                studentName = new String[]{};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GradeGraphFragment.this.getActivity(), android.R.layout.simple_spinner_dropdown_item, studentName);
                spnrStudent.setAdapter(arrayAdapter);
                GetStudentsAPI getStudentsAPI = new GetStudentsAPI(GradeGraphFragment.this.getActivity());
                getStudentsAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnrStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerStudentPos = position;


                GetGradeDetails getGradeDetails = new GetGradeDetails(GradeGraphFragment.this.getActivity());
                getGradeDetails.execute();
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

    }

    //this class will get the grade details and add them to listview item by item
    class GetGradeDetails extends AsyncTask<Void, Void, List<GradeTask>> {
        Context context;
        String subjectId2 = "";
        String stdId = "";

        public GetGradeDetails(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId2 = spnrSubject.getSelectedItem().toString();
            stdId = studentDetails.get(spnrStudent.getSelectedItemPosition()).studentId;
            ((HomeActivity) getActivity()).showProgressDialog("Fetching Grades ...");
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<GradeTask> taskDetails2) {
            //check if the call to api passed
            ((HomeActivity) getActivity()).hideProgressDialog();
            if (taskDetails2 != null) {
                gradeDetails = taskDetails2;


                loadChart();
            } else {

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


    private void loadChart() {
        mChart = (BarChart) getView().findViewById(R.id.taskBarChart);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);


        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawValueAboveBar(false);

        final String[] xAxisData=new String[gradeDetails.size()];

        for(int i=0;i<gradeDetails.size();i++){
            xAxisData[i]="Task - "+(i+1);
        }


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(gradeDetails.size());

        xAxis.setLabelRotationAngle(90);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisData));


        YAxis leftAxis = mChart.getAxisLeft();

////        leftAxis.setAxisMaximum(grades.length+1);
//        leftAxis.setAxisMinimum(1);
        leftAxis.setGranularity(1f);

        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(chartGrades.length);
        leftAxis.setLabelCount(chartGrades.length);
        leftAxis.setValueFormatter(new IndexAxisValueFormatter(chartGrades));
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setEnabled(false);
        rightYAxis.setDrawGridLines(false);


        ; // this replaces setStartAtZero(true)


        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
//
//        MarkerView mv = new MarkerView(this, xAxisFormatter);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

        setData();
    }

    class GetStudentsAPI extends AsyncTask<Void, Void, List<StudentDetails>> {
        Context context;
        String subj = "";

        public GetStudentsAPI(Context ctx) {
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


            if (apiResponse != null && apiResponse.size() > 0) {
                studentDetails = apiResponse;

                studentName = new String[studentDetails.size()];
                for (int i = 0; i < studentDetails.size(); i++) {
                    studentName[i] = studentDetails.get(i).getFirstName() + " " + studentDetails.get(i).getLastName();
                }
                //add the subject ids to the spinner
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, studentName);
                spnrStudent.setAdapter(arrayAdapter);

            } else {

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


    class DayAxisValueFormatter implements IAxisValueFormatter {


        public DayAxisValueFormatter() {

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return "Task-" + ((int) (value) );
        }


    }


    class MyAxisValueFormatter implements IAxisValueFormatter {


        public MyAxisValueFormatter() {

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value< 0 || value>grades.length-1) {
                return  "";
            } else {
                System.out.println("grade data"+value);
                return grades[(int)(value) ];
            }
        }


    }

    private void setData() {


        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        List<String> gradeList = Arrays.asList(grades);

        for (int i = 0; i < gradeDetails.size(); i++) {

            int index = gradeList.indexOf(gradeDetails.get(i).instructorGrade);
            if (index != -1) {
                yVals1.add(new BarEntry((float)i, (float)index+1));
            } else {
                yVals1.add(new BarEntry((float)i, 0f));
            }


        }

        BarDataSet set;
        mChart.clear();
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(yVals1, "");

            set.setDrawIcons(false);

            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }


}
