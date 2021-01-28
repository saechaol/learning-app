package com.saechaol.learningapp.ui.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.reinaldoarrosi.maskededittext.MaskedEditText;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.AdminDetails;
import com.saechaol.learningapp.model.InstructorDetails;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ViewInstructorActivity extends BaseActivity {

    private InstructorDetails userDetails;
    TextView txtUserId;
    EditText txtUserName;
    EditText txtFirstName;
    EditText txtLastName;
    EditText txtEmailId;
    MaskedEditText txtTelephone;
    EditText txtAliasMailId;
    EditText txtAddress;
    EditText txtHangoutId;
    EditText txtPassword;

    private boolean isToAdd = false;

    private boolean enabledEditMode = false;
    private LinearLayout linUserIdCont, linUserNameCont, linPasswordCont;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructordisplay);
        isToAdd = getIntent().getBooleanExtra(CommonUtil.EXTRA_IS_TO_ADD, false);

        txtPassword = (EditText) findViewById(R.id.instructorTextPassword);
        txtUserId = (TextView) findViewById(R.id.instructorTextUserId);
        txtUserName = (EditText) findViewById(R.id.instructorTextUserName);
        txtFirstName = (EditText) findViewById(R.id.instructorTextFirstName);
        txtLastName = (EditText) findViewById(R.id.instructorTextLastName);
        txtEmailId = (EditText) findViewById(R.id.instructorTextEmailId);
        txtTelephone = (MaskedEditText) findViewById(R.id.instructorTextTelephone);
        txtAliasMailId = (EditText) findViewById(R.id.instructorTextEmailId);
        txtAddress = (EditText) findViewById(R.id.instructorTextAddress);
        txtHangoutId = (EditText) findViewById(R.id.instructorTextHangoutId);
        linUserIdCont = (LinearLayout) findViewById(R.id.activityViewAdminLoginUserId);
        linUserNameCont = (LinearLayout) findViewById(R.id.activityViewAdminLoginUserName);
        linPasswordCont = (LinearLayout) findViewById(R.id.activityViewAdminLoginPassword);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (!isToAdd) {
            userDetails = (InstructorDetails) getIntent().getSerializableExtra(CommonUtil.EXTRA_USER_ADMIN_DATA);

            setToolbarTitle(userDetails.getFirstName() + " " + userDetails.getLastName());
            setUpData();
            enabledEditMode = getIntent().getBooleanExtra(CommonUtil.EXTRA_EDIT_MODE, false);
            enableFields(enabledEditMode);
            linUserIdCont.setVisibility(View.VISIBLE);
            linUserNameCont.setVisibility(View.VISIBLE);
            linPasswordCont.setVisibility(View.GONE);
        } else {

            setToolbarTitle("Add Instructor");
            linUserIdCont.setVisibility(View.GONE);
            txtUserName.setEnabled(true);
            linUserNameCont.setVisibility(View.VISIBLE);
            linPasswordCont.setVisibility(View.VISIBLE);
        }

    }

    private void setUpData() {
        txtUserId.setText("" + userDetails.getUserId());
        txtUserName.setText(userDetails.getInstructorId());
        txtFirstName.setText(userDetails.getFirstName());
        txtLastName.setText(userDetails.getLastName());
        txtEmailId.setText(userDetails.getEmail());
        txtTelephone.setText(userDetails.getPhone());
        txtAliasMailId.setText(userDetails.getAliasMailId());
        txtAddress.setText(userDetails.getAddress());
        txtHangoutId.setText(userDetails.getSkypeId());
    }


    private void enableFields(boolean makeEditable) {
        txtAddress.setEnabled(makeEditable);
        txtAliasMailId.setEnabled(makeEditable);
        txtEmailId.setEnabled(makeEditable);
        txtFirstName.setEnabled(makeEditable);
        txtLastName.setEnabled(makeEditable);
        txtHangoutId.setEnabled(makeEditable);
        txtTelephone.setEnabled(makeEditable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isToAdd) {
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

                if (TextUtils.isEmpty(txtAddress.getText().toString()) ||
                        TextUtils.isEmpty(txtUserName.getText().toString()) ||
                        TextUtils.isEmpty(txtEmailId.getText().toString()) ||
                        TextUtils.isEmpty(txtAliasMailId.getText().toString()) ||
                        TextUtils.isEmpty(txtLastName.getText().toString()) ||
                        TextUtils.isEmpty(txtTelephone.getText(true).toString()) ||
                        TextUtils.isEmpty(txtHangoutId.getText().toString()) ||
                        TextUtils.isEmpty(txtFirstName.getText().toString()) ||
                        TextUtils.isEmpty(txtPassword.getText().toString())) {

                    showSnackBar(getString(R.string.emptyFieldError), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                } else if (!CommonUtil.isValidMail(txtEmailId.getText().toString()) ||
                        !CommonUtil.isValidMail(txtAliasMailId.getText().toString()) ||
                        !CommonUtil.isValidMobile(txtTelephone.getText(true).toString())) {

                    if (!CommonUtil.isValidMail(txtEmailId.getText().toString())) {
                        txtEmailId.requestFocus();
                        showSnackBar(getString(R.string.invalidEmailId), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    } else if (!CommonUtil.isValidMail(txtAliasMailId.getText().toString())) {
                        txtAliasMailId.requestFocus();
                        showSnackBar(getString(R.string.invalidAlternateEmail), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    } else {
                        txtTelephone.requestFocus();
                        showSnackBar(getString(R.string.invalidPhoneNumber), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    }

                } else {
                    if (CommonUtil.checkInternetConnection(ViewInstructorActivity.this)) {
                        AddInstructorTask addAdminTask = new AddInstructorTask(ViewInstructorActivity.this);
                        addAdminTask.execute();
                    } else {
                        showSnackBar(getString(R.string.checkConnection), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    }
                }
            } else {

                if (TextUtils.isEmpty(txtAddress.getText().toString()) ||
                        TextUtils.isEmpty(txtUserName.getText().toString()) ||
                        TextUtils.isEmpty(txtEmailId.getText().toString()) ||
                        TextUtils.isEmpty(txtAliasMailId.getText().toString()) ||
                        TextUtils.isEmpty(txtLastName.getText().toString()) ||
                        TextUtils.isEmpty(txtTelephone.getText().toString()) ||
                        TextUtils.isEmpty(txtHangoutId.getText().toString()) ||
                        TextUtils.isEmpty(txtFirstName.getText().toString()) ) {

                    showSnackBar(getString(R.string.emptyFieldError), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                } else if (!CommonUtil.isValidMail(txtEmailId.getText().toString()) ||
                        !CommonUtil.isValidMail(txtAliasMailId.getText().toString()) ||
                        !CommonUtil.isValidMobile(txtTelephone.getText(true).toString())) {

                    if (!CommonUtil.isValidMail(txtEmailId.getText().toString())) {
                        txtEmailId.requestFocus();
                        showSnackBar(getString(R.string.invalidEmailId), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    } else if (!CommonUtil.isValidMail(txtAliasMailId.getText().toString())) {
                        txtAliasMailId.requestFocus();
                        showSnackBar(getString(R.string.invalidAlternateEmail), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    } else {
                        txtTelephone.requestFocus();
                        showSnackBar(getString(R.string.invalidPhoneNumber), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    }

                } else {
                    if (CommonUtil.checkInternetConnection(ViewInstructorActivity.this)) {
                        UpdateInstructorAPI updateAdminTask = new UpdateInstructorAPI(ViewInstructorActivity.this);
                        updateAdminTask.execute();
                    } else {
                        showSnackBar(getString(R.string.checkConnection), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    }
                }
            }

        }


        return super.onOptionsItemSelected(item);
    }

    class UpdateInstructorAPI extends AsyncTask<Void, Void, String> {
        Context context;
        private InstructorDetails details;

        public UpdateInstructorAPI(Context ctx) {
            context = ctx;
            details = new InstructorDetails();
            details.setAddress(txtAddress.getText().toString());
            details.setAliasMailId(txtAliasMailId.getText().toString());
            details.setEmail(txtEmailId.getText().toString());
            details.setFirstName(txtFirstName.getText().toString());
            details.setInstructorId(txtUserName.getText().toString());
            details.setLastName(txtLastName.getText().toString());
            details.setSkypeId(txtHangoutId.getText().toString());
            details.setPhone(txtTelephone.getText(true).toString());
            details.setUserId(Integer.parseInt(txtUserId.getText().toString()));
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Update Instructor User Data...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();

            if (statusCode.equals("202")) //the item is updated
            {
                finish();
            } else {
                showSnackBar(getString(R.string.serverError), findViewById(R.id.activity_view_instructor_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            Call<String> callUpdate = Api.getClient().updateInstructor(details);
            try {
                Response<String> respUpdate = callUpdate.execute();
                return "202";
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
    }


    class AddInstructorTask extends AsyncTask<Void, Void, String> {
        Context context;
        AdminDetails userDetails = new AdminDetails();

        public AddInstructorTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            userDetails.setPhone(txtTelephone.getText(true).toString());
            userDetails.setAdminId(txtUserName.getText().toString());
            userDetails.setSkypeId(txtHangoutId.getText().toString());
            userDetails.setLastName(txtLastName.getText().toString());
            userDetails.setAddress(txtAddress.getText().toString());
            userDetails.setAliasMailId(txtAliasMailId.getText().toString());
            userDetails.setFirstName(txtFirstName.getText().toString());
            userDetails.setEmail(txtEmailId.getText().toString());
            userDetails.setPassword(txtPassword.getText().toString());
            showProgressDialog("Add Instructor User Data...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();
            if (statusCode.equals("201")) //the item is created
            {
                finish();
            } else {
                showSnackBar(getString(R.string.serverError), findViewById(R.id.activity_view_instructor_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Call<InstructorDetails> callAddAdmin = Api.getClient().addInstructor(userDetails.getAdminId(), userDetails.getPassword(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getPhone(), userDetails.getAddress(), userDetails.getAliasMailId(), userDetails.getEmail(), userDetails.getSkypeId());
            try {
                Response<InstructorDetails> respCallAdmin = callAddAdmin.execute();
                return "" + respCallAdmin.code();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
    }

}
