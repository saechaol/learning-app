package com.saechaol.learningapp.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.ScheduleDetailPostData;
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.service.AlertTaskIntentService;
import com.saechaol.learningapp.ui.activity.HomeActivity;
import com.saechaol.learningapp.util.Predicate;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class ScheduleAddFragment extends Fragment {

    View view;

    TextView txtstartDay, txtChooseDay;
    TextView txtendDay;
    TextView txtstartTime;
    TextView txtendTime;
    Spinner spnrSubjectID;

    String[] strSubjectId;
    String[] strSubjectTitle;
    String[] strSubjectDescription;

    String strStartDay;
    String strEndDay;
    String strStartTime;
    String strEndTime;

    int scheduleStartYear;
    int scheduleStartMonth;
    int scheduleStartDay;
    int scheduleStartHour;
    int scheduleStartMinute;

    int scheduleEndYear;
    int scheduleEndMonth;
    int scheduleEndDay;
    int scheduleEndHour;
    int scheduleEndMinute;

    boolean isAvailableForSchedule = true;

    String[] strUserNames;
    String[] strFirstLastNameUser;
    String[] strEmailUser;

    List<SubjectDetails> subjectDetails = new ArrayList<SubjectDetails>();
    boolean[] checkedDays = new boolean[]{
            false,
            false,
            false,
            false,
            false,
            false,
            false
    };

    PreferenceManager prefsManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ((HomeActivity) getActivity()).showSnackBar("Please grant permission otherwise calendar events can not be added.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));
                }
                return;
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addschedule, container, false);
        setHasOptionsMenu(true);
        prefsManager = new PreferenceManager(getActivity());
        spnrSubjectID = (Spinner) view.findViewById(R.id.addScheduleSpinnerSubjectId);
        txtstartDay = (TextView) view.findViewById(R.id.addScheduleTextStartDay);
        txtendDay = (TextView) view.findViewById(R.id.addScheduleTextEndDay);
        txtstartTime = (TextView) view.findViewById(R.id.addScheduleTextStartTime);
        txtendTime = (TextView) view.findViewById(R.id.addScheduleTextEndTime);
        txtChooseDay = (TextView) view.findViewById(R.id.addScheduleTextChooseDay);
        txtChooseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDaysDialog();
            }
        });

        txtstartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int mothe = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtstartDay.setText((1 + monthOfYear) + "/" + dayOfMonth + "/" + year);
                        strStartDay = (1 + monthOfYear) + "/" + dayOfMonth + "/" + year;
                        scheduleStartYear = year;
                        scheduleStartMonth = monthOfYear;
                        scheduleStartDay = dayOfMonth;

                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onDateSetListener, year, mothe, day);
                datePickerDialog.setTitle("Start Day");
                datePickerDialog.show();
            }
        });
        txtendDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int mothe = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtendDay.setText((1 + monthOfYear) + "/" + dayOfMonth + "/" + year);
                        strEndDay = (1 + monthOfYear) + "/" + dayOfMonth + "/" + year;
                        scheduleEndYear = year;
                        scheduleEndMonth = monthOfYear;
                        scheduleEndDay = dayOfMonth;
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onDateSetListener, year, mothe, day);
                datePickerDialog.setTitle("End Day");
                datePickerDialog.show();
            }
        });
        txtstartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtstartTime.setText(getTimeInAmPm(hourOfDay, minute));
                        strStartTime = getTimeInAmPm(hourOfDay, minute);
                        scheduleStartHour = hourOfDay;
                        scheduleStartMinute = minute;
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onTimeSetListener, hour, minute, android.text.format.DateFormat.is24HourFormat(ScheduleAddFragment.this.getActivity()));
                timePickerDialog.setTitle("Start Time");
                timePickerDialog.show();

            }
        });
        txtendTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtendTime.setText(getTimeInAmPm(hourOfDay, minute));
                        strEndTime = getTimeInAmPm(hourOfDay, minute);
                        scheduleEndHour = hourOfDay;
                        scheduleEndMinute = minute;
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onTimeSetListener, hour, minute, android.text.format.DateFormat.is24HourFormat(ScheduleAddFragment.this.getActivity()));
                timePickerDialog.setTitle("End Time");
                timePickerDialog.show();
            }
        });

        GetAllSubjectWithTaskAPI getAllSubjectWithTaskAPI = new GetAllSubjectWithTaskAPI(this.getActivity());
        getAllSubjectWithTaskAPI.execute();

        return view;
    }


    private void showDaysDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog);

        String[] days = new String[]{
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };


        final List<String> daysList = Arrays.asList(days);

        builder.setMultiChoiceItems(days, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedDays[which] = isChecked;
            }
        });


        builder.setCancelable(false);
        builder.setTitle("Every");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtChooseDay.setText("");
                for (int i = 0; i < checkedDays.length; i++) {
                    boolean checked = checkedDays[i];
                    if (checked) {
                        txtChooseDay.setText(txtChooseDay.getText() + daysList.get(i) + " ");
                    }
                }

                if (txtChooseDay.getText().toString().trim().equalsIgnoreCase("")) {
                    txtChooseDay.setText("Choose days");
                }
            }
        });



        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private String getTimeInAmPm(int hour, int minute) {
        Time time = new Time(hour, minute, 0);
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        return formatter.format(time);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_schedule_add) {
            addSchedule();
        }
        return true;
    }

    private void addSchedule() {

        if (!isAvailableForSchedule)
            return;

        if (spnrSubjectID.getSelectedItem().toString().equals("") ||
                txtstartDay.getText().toString().equals("") ||
                txtstartDay.getText().toString().equals("Choose start day") ||
                txtendDay.getText().toString().equals("") ||
                txtendDay.getText().toString().equals("Choose end day") ||
                txtstartTime.getText().toString().equals("") ||
                txtstartTime.getText().toString().equals("Choose start time") ||
                txtendTime.getText().toString().equals("") ||
                txtendTime.getText().toString().equals("Choose end time") ||
                txtChooseDay.getText().toString().equals("Choose days")) {

            ((HomeActivity) getActivity()).showSnackBar("Please choose data for all inputs.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));

        } else {

            GetEnrollBySubjectAPI getEnrollBySubjectAPI = new GetEnrollBySubjectAPI(ScheduleAddFragment.this.getActivity());
            getEnrollBySubjectAPI.execute();

        }

    }


    private String getSelectedDaysCalendar() {
        String[] data = txtChooseDay.getText().toString().split(" ");
        String days = "";
        for (int i = 0; i < data.length; i++) {
            if (data[i].equalsIgnoreCase("monday")) {
                days = days + "," + "MO";
            } else if (data[i].equalsIgnoreCase("tuesday")) {
                days = days + "," + "TU";
            } else if (data[i].equalsIgnoreCase("wednesday")) {
                days = days + "," + "WE";
            } else if (data[i].equalsIgnoreCase("thursday")) {
                days = days + "," + "TH";
            } else if (data[i].equalsIgnoreCase("friday")) {
                days = days + "," + "FR";
            } else if (data[i].equalsIgnoreCase("saturday")) {
                days = days + "," + "SA";
            } else if (data[i].equalsIgnoreCase("sunday")) {
                days = days + "," + "SU";
            }

        }

        if (days.startsWith(",")) {
            days = days.replaceFirst(",", "");
        }
        return days;
    }

    private String getSelectedDays() {
        String[] data = txtChooseDay.getText().toString().split(" ");
        String days = "";
        for (String day :
                data) {
            if (day.equalsIgnoreCase("monday")) {
                days = days + "m";
            } else if (day.equalsIgnoreCase("tuesday")) {
                days = days + "t";
            } else if (day.equalsIgnoreCase("wednesday")) {
                days = days + "w";
            } else if (day.equalsIgnoreCase("thursday")) {
                days = days + "r";
            } else if (day.equalsIgnoreCase("friday")) {
                days = days + "f";
            } else if (day.equalsIgnoreCase("saturday")) {
                days = days + "s";
            } else if (day.equalsIgnoreCase("sunday")) {
                days = days + "u";
            }

        }
        return days;
    }

    class GetEnrollBySubjectAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String idSubject1;
        String every1;
        String startDate1;
        String endDate1;


        public GetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

            idSubject1 = spnrSubjectID.getSelectedItem().toString();
            every1 = getSelectedDays();
            startDate1 = strStartDay + " " + strStartTime;
            endDate1 = strEndDay + " " + strEndTime;
            ((HomeActivity) getActivity()).showProgressDialog("Adding Schedule and Tasks...");
        }

        @Override
        protected void onPostExecute(String statusCode) {

            ((HomeActivity) getActivity()).hideProgressDialog();

            if (statusCode.equals("created")) //the item is created
            {
                String email_list = "";
                for (int i = 0; i < strEmailUser.length; i++) {
                    if ((i + 1) < strEmailUser.length)
                        email_list += strEmailUser[i] + " , ";
                    else
                        email_list += strEmailUser[i];
                }

                String st_h = scheduleEndHour + "";
                String st_m = scheduleEndMinute + "";
                String st_month = (scheduleEndMonth + 1) + "";
                String st_d = scheduleEndDay + "";
                if ((scheduleEndMonth + 1) < 10) {
                    st_month = "0" + (scheduleEndMonth + 1);
                }
                if (scheduleEndDay < 10) {
                    st_d = "0" + (scheduleEndDay);
                }
                if (scheduleEndHour < 10) {
                    st_h = "0" + (scheduleStartHour);
                }
                if (scheduleEndMinute < 10) {
                    st_m = "0" + (scheduleStartMinute);
                }
                String rpt = getSelectedDaysCalendar();

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(scheduleStartYear, scheduleStartMonth, scheduleStartDay, scheduleStartHour, scheduleStartMinute);
                Calendar endTime = Calendar.getInstance();
                endTime.set(scheduleStartYear, scheduleStartMonth, scheduleStartDay, scheduleEndHour, scheduleEndMinute);

                try {
                    ContentResolver cr = getActivity().getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                    values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
                    values.put(CalendarContract.Events.TITLE, subjectDetails.get(spnrSubjectID.getSelectedItemPosition()).title);
                    values.put(CalendarContract.Events.DESCRIPTION, subjectDetails.get(spnrSubjectID.getSelectedItemPosition()).description);
                    values.put(CalendarContract.Events.CALENDAR_ID, 1);
                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=" + scheduleEndYear + st_month + st_d + "T" + st_h + st_m + "00Z;WKST=SU;BYDAY=" + rpt);
                    values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                    TimeZone timeZone = TimeZone.getDefault();
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

                    if (Build.VERSION.SDK_INT < 23 || (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)) {
                        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    }


                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Dialog );
                    builder.setTitle(getString(R.string.app_name));
                    builder.setMessage("Would you like to add task Now or Later?");
                    builder.setPositiveButton("Add Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            ( (HomeActivity)getActivity()).setToolbarTitle("Edit Schedule");

                            EditTaskFragment updateTaskFragment = new EditTaskFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("subId", idSubject1);

                            updateTaskFragment.setArguments(bundle);


                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentholder, updateTaskFragment);
                            fragmentTransaction.commit();
                        }
                    });
                    builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ( (HomeActivity)getActivity()).onHomeClick();

                            dialogInterface.dismiss();

                        }
                    });
                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((HomeActivity) getActivity()).showSnackBar("The schedule and tasks are created.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));

                clearFields();
                final Intent intentService = new Intent(getActivity(), AlertTaskIntentService.class);
                getActivity().startService(intentService);
            } else {
                ((HomeActivity) getActivity()).showSnackBar("The schedule and tasks are not created as it might already have existing tasks.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));

            }

        }

        @Override
        protected String doInBackground(Void... params) {
            final ScheduleDetailPostData scheduleDetails = new ScheduleDetailPostData();
            scheduleDetails.setSubjectId(idSubject1);
            scheduleDetails.setInstructorId("");
            scheduleDetails.setTitle("");
            scheduleDetails.setDescription("");
            scheduleDetails.setScheduleStartTime(startDate1);
            scheduleDetails.setScheduleEndTime(endDate1);
            scheduleDetails.setIsQuiz("y");
            try {
                scheduleDetails.setRepeatTask(every1);
                Call<String> callPostSchedule = Api.getClient().addSchedule(scheduleDetails);
                Response<String> resPostSchedule = callPostSchedule.execute();
                if (resPostSchedule.code() == 201) {

                    Call<List<StudentDetails>> callStudentData = Api.getClient().getEnrollBySubject(idSubject1);
                    Response<List<StudentDetails>> responseStudentData = callStudentData.execute();
                    if (responseStudentData.isSuccessful() && responseStudentData.body() != null) {
                        List<StudentDetails> studentUserDetails = responseStudentData.body();
                        if (studentUserDetails != null) {
                            strUserNames = new String[studentUserDetails.size()];
                            strFirstLastNameUser = new String[studentUserDetails.size()];
                            strEmailUser = new String[studentUserDetails.size()];
                            for (int i = 0; i < studentUserDetails.size(); i++) {
                                strUserNames[i] = studentUserDetails.get(i).username;
                                strFirstLastNameUser[i] = studentUserDetails.get(i).lastName + ", " + studentUserDetails.get(i).firstName;
                                strFirstLastNameUser[i] += "    (" + studentUserDetails.get(i).username + ")";

                                strEmailUser[i] = studentUserDetails.get(i).email;
                            }
                        }
                    }
                } else {
                    return "";
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            return "created";
        }
    }

    private void clearFields() {
        txtendTime.setText("Choose end time");
        txtstartTime.setText("Choose start time");
        txtstartDay.setText("Choose start day");
        txtendDay.setText("Choose end day");
        txtChooseDay.setText("Choose days");
    }

    class GetAllSubjectWithTaskAPI extends AsyncTask<Void, Void, List<SubjectDetails>> {
        Context context;

        public GetAllSubjectWithTaskAPI(Context ctx) {
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
                    spnrSubjectID.setAdapter(arrayAdapter);
                    if (Build.VERSION.SDK_INT >= 23) {
                        ScheduleAddFragment.this.requestPermissions(new String[]{android.Manifest.permission.WRITE_CALENDAR}, 101);
                    }
                } else {
                    ((HomeActivity) getActivity()).showSnackBar("There are no subjects for adding schedule.", getView().findViewById(R.id.fragment_add_schedule_coordinatorLayout));
                    isAvailableForSchedule = false;

                }

            } else {
                ((HomeActivity) getActivity()).showSnackBar("There are no subjects for adding schedule.", getView().findViewById(R.id.fragment_add_schedule_coordinatorLayout));

            }
        }

        @Override
        protected List<SubjectDetails> doInBackground(Void... params) {

            try {
                Call<List<SubjectDetails>> callSubjectData = Api.getClient().getAllSubjectWithTask("false"); // False key is used, it indicates that we need to fetch all the subjects which does not have any schedule. So, we can add a schedule for it.
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
