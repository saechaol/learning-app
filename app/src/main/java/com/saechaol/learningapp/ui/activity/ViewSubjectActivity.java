package com.saechaol.learningapp.ui.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.InstructorDetails;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.util.UserTypeData;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ViewSubjectActivity extends BaseActivity {

    EditText txtSubjectId;
    EditText txtSubjectTitle;
    EditText txtSubjectDescription;
    Spinner spnrInstructorId;
    EditText txtAliasMailId;
    SubjectDetails subjectDetails;
    boolean isToAdd = false, enabledEditMode = false;
    List<InstructorDetails> instDetails = new ArrayList<InstructorDetails>();
    PreferenceManager prefsManager;
    String[] instId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectdisplay);
        isToAdd = getIntent().getBooleanExtra(CommonUtil.EXTRA_IS_TO_ADD, false);
        prefsManager = new PreferenceManager(this);
        enabledEditMode = getIntent().getBooleanExtra(CommonUtil.EXTRA_EDIT_MODE, false);
        txtSubjectId = (EditText) findViewById(R.id.subjectTextSubjectId);
        txtSubjectTitle = (EditText) findViewById(R.id.subjectTextSubjectTitle);
        txtSubjectDescription = (EditText) findViewById(R.id.subjectTextSubjectDescription);
        spnrInstructorId = (Spinner) findViewById(R.id.subjectSpinnerInstructorId);
        txtAliasMailId = (EditText) findViewById(R.id.subjectTextMailAlias);

        GetAllInstructorAPI getUserDetails = new GetAllInstructorAPI(this);
        getUserDetails.execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spnrInstructorId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InstructorDetails a = instDetails.get(position);
                System.out.println(a.aliasMailId);
                txtAliasMailId.setText(a.aliasMailId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtAliasMailId.setEnabled(false);

    }

    private void setUpData() {
        txtSubjectId.setText(subjectDetails.getSubjectId());
        txtSubjectTitle.setText(subjectDetails.getTitle());
        txtSubjectDescription.setText(subjectDetails.getDescription());
        for (int i = 0; i < instDetails.size(); i++) {
            if (instDetails.get(i).getInstructorId().equalsIgnoreCase(subjectDetails.getInstructorId())) {
                spnrInstructorId.setSelection(i);
                break;
            }
        }
        txtAliasMailId.setText(subjectDetails.getMailAlias());
        spnrInstructorId.setEnabled(false);
        txtSubjectId.setEnabled(false);
    }


    private void enableFields(boolean makeEditable) {
        txtSubjectTitle.setEnabled(makeEditable);
        txtSubjectDescription.setEnabled(makeEditable);
        //txtAliasMailId.setEnabled(makeEditable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (prefsManager.getStringData("userType").equals(UserTypeData.STUDENT)) {
            menu.findItem(R.id.menu_edit).setVisible(false);
            menu.findItem(R.id.menu_cancel).setVisible(false);
            menu.findItem(R.id.menu_save).setVisible(false);

        } else if (!isToAdd) {
            if (enabledEditMode) {
                menu.findItem(R.id.menu_edit).setVisible(false);
                menu.findItem(R.id.menu_cancel).setVisible(true);
                menu.findItem(R.id.menu_save).setVisible(true);
            } else {
                menu.findItem(R.id.menu_edit).setVisible(true);
                menu.findItem(R.id.menu_cancel).setVisible(false);
                menu.findItem(R.id.menu_save).setVisible(false);
            }
        } else {
            menu.findItem(R.id.menu_edit).setVisible(false);
            menu.findItem(R.id.menu_cancel).setVisible(false);
            menu.findItem(R.id.menu_save).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_edit) {
            enabledEditMode = true;
            invalidateOptionsMenu();
            enableFields(enabledEditMode);
        } else if (item.getItemId() == R.id.menu_cancel) {
            enabledEditMode = false;
            invalidateOptionsMenu();
            enableFields(enabledEditMode);
            setUpData();
        } else if (item.getItemId() == R.id.menu_save) {
            hideKeyboard();

            if (isToAdd) {

                if (txtSubjectId.getText().toString().equals("") ||
                        txtSubjectTitle.getText().toString().equals("") ||
                        txtSubjectDescription.getText().toString().equals("")) {

                    showSnackBar(getString(R.string.emptyFieldError), findViewById(R.id.activity_view_subject_coordinatorLayout));

                } else if (!CommonUtil.isValidMail(txtAliasMailId.getText().toString())) {
                    txtAliasMailId.requestFocus();
                    showSnackBar(getString(R.string.invalidEmailId), findViewById(R.id.activity_view_subject_coordinatorLayout));

                } else {
                    if (CommonUtil.checkInternetConnection(ViewSubjectActivity.this)) {
                        AddSubjectAPI addSubjectTask = new AddSubjectAPI(ViewSubjectActivity.this);
                        addSubjectTask.execute();
                    } else {
                        showSnackBar(getString(R.string.checkConnection), findViewById(R.id.activity_view_subject_coordinatorLayout));
                    }
                }
            } else {

            }
            if (txtSubjectId.getText().toString().equals("") || txtSubjectTitle.getText().toString().equals("") || txtSubjectDescription.getText().toString().equals("")) {
                showSnackBar(getString(R.string.emptyFieldError), findViewById(R.id.activity_view_subject_coordinatorLayout));
            } else if (!CommonUtil.isValidMail(txtAliasMailId.getText().toString())) {
                txtAliasMailId.requestFocus();
                showSnackBar(getString(R.string.invalidEmailId), findViewById(R.id.activity_view_subject_coordinatorLayout));
            } else {
                if (CommonUtil.checkInternetConnection(ViewSubjectActivity.this)) {
                    UpdateSubjectAPI updateSubjectTask = new UpdateSubjectAPI(ViewSubjectActivity.this);
                    updateSubjectTask.execute();
                } else {
                    showSnackBar(getString(R.string.checkConnection), findViewById(R.id.activity_view_subject_coordinatorLayout));
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }


    class UpdateSubjectAPI extends AsyncTask<Void, Void, String> {
        Context context;
        private SubjectDetails subjectDetails;

        // The static parameters will be removed in the future from the WebAPI.
        // Right now, I have set all the parameters according to the iOS application.

        public UpdateSubjectAPI(Context ctx) {
            context = ctx;
            subjectDetails = new SubjectDetails();
            subjectDetails.subjectId = txtSubjectId.getText().toString();
            subjectDetails.title = txtSubjectTitle.getText().toString();
            subjectDetails.description = txtSubjectDescription.getText().toString();
            subjectDetails.isVideoEnabled = "n";
            subjectDetails.isAudioEnabled = "y";
            subjectDetails.startTime = "05:13:09";
            subjectDetails.endTime = "2017-10-18T05:13:09";
            subjectDetails.instructorId = spnrInstructorId.getSelectedItem().toString();
            subjectDetails.startDate = "2016-10-18T05:13:09";
            subjectDetails.endDate = "2017-10-18T05:13:09";
            subjectDetails.mailAlias = txtAliasMailId.getText().toString();
            subjectDetails.duration = 0;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Update Subject Data...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();
            if (statusCode.equals("202"))
            {
                finish();
            } else {
                showSnackBar(getString(R.string.serverError), findViewById(R.id.activity_view_subject_coordinatorLayout));

            }
        }

        @Override
        protected String doInBackground(Void... params) {

            Call<String> callUpdate = Api.getClient().updateSubject(subjectDetails);
            try {
                Response<String> respUpdate = callUpdate.execute();
                return "202";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }


    class AddSubjectAPI extends AsyncTask<Void, Void, String> {
        Context context;
        SubjectDetails subjectDetails = new SubjectDetails();
        public AddSubjectAPI(Context ctx) {
            context = ctx;
        }

        // The static parameters will be removed in the future from the WebAPI.
        // Right now, I have set all the parameters according to the iOS application.
        @Override
        protected void onPreExecute() {
            subjectDetails.subjectId = txtSubjectId.getText().toString();
            subjectDetails.title = txtSubjectTitle.getText().toString();
            subjectDetails.description = txtSubjectDescription.getText().toString();
            subjectDetails.isVideoEnabled = "n";
            subjectDetails.isAudioEnabled = "y";
            subjectDetails.startTime = "05:13:09";
            subjectDetails.endTime = "2017-10-18T05:13:09";
            subjectDetails.instructorId = spnrInstructorId.getSelectedItem().toString();
            subjectDetails.startDate = "2016-10-18T05:13:09";
            subjectDetails.endDate = "2017-10-18T05:13:09";
            subjectDetails.mailAlias = txtAliasMailId.getText().toString();
            subjectDetails.duration = 0;
            showProgressDialog("Adding Subject...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();
            if (statusCode.equals("202")) //the item is created
            {
                finish();
            } else {
                showSnackBar(getString(R.string.serverError), findViewById(R.id.activity_view_subject_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Call<SubjectDetails> callAddSubject = Api.getClient().addSubject(subjectDetails);
            try {
                Response<SubjectDetails> respCallSubject = callAddSubject.execute();
                if (respCallSubject.isSuccessful())
                    return "202";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    class GetAllInstructorAPI extends AsyncTask<Void, Void, List<InstructorDetails>> {
        Context context;

        public GetAllInstructorAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Getting Instructor User Data...");
        }

        @Override
        protected void onPostExecute(List<InstructorDetails> userDetails) {

            hideProgressDialog();
            if (userDetails != null) {
                instDetails = userDetails;
                instId = new String[instDetails.size()];

                for (int i = 0; i < instDetails.size(); i++) {
                    instId[i] = instDetails.get(i).instructorId;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewSubjectActivity.this, android.R.layout.simple_spinner_dropdown_item, instId);

                spnrInstructorId.setAdapter(adapter);
                if (!isToAdd) {
                    subjectDetails = (SubjectDetails) getIntent().getSerializableExtra(CommonUtil.EXTRA_USER_ADMIN_DATA);
                    setToolbarTitle(subjectDetails.getTitle());
                    setUpData();
                    enableFields(enabledEditMode);
                } else {
                    setToolbarTitle("Add Subject");
                }
            } else {

                showSnackBar(getString(R.string.serverError), findViewById(R.id.activity_view_subject_coordinatorLayout));
            }
        }

        @Override
        protected List<InstructorDetails> doInBackground(Void... params) {

            try {
                Call<List<InstructorDetails>> callInstUserData = Api.getClient().getInstructors();
                Response<List<InstructorDetails>> responseInstUser = callInstUserData.execute();
                if (responseInstUser.isSuccessful() && responseInstUser.body() != null) {
                    return responseInstUser.body();
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
