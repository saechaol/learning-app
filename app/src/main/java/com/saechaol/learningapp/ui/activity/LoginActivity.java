package com.saechaol.learningapp.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.saechaol.learningapp.R;
import com.saechaol.learningapp.model.RegisterUsers;
import com.saechaol.learningapp.util.CommonUtil;
import com.saechaol.learningapp.util.PreferenceManager;
import com.saechaol.learningapp.webservice.Api;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    EditText txtUserName;
    EditText txtPassword;
    Button btnLogin;
    RegisterUsers register;
    private ProgressDialog progressDialog;
    private PreferenceManager prefsManager;


    public void showProgressDialog(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(this, getString(R.string.app_name), message, true, false);

        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
    }

    void loadingUserInformation() {
        register.userId = prefsManager.getStringData("userId");
        register.username = prefsManager.getStringData("userName");
        register.userType = prefsManager.getStringData("userType");
    }

    public void showSnackBar(String message, View view) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    void savingUserInformation() {
        prefsManager.saveData("userId", register.userId);
        prefsManager.saveData("userName", register.username);
        prefsManager.saveData("userType", register.userType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Sign In");
        prefsManager=new PreferenceManager(this);
        register = new RegisterUsers();

        loadingUserInformation();
        if (register.userType != "" && register.userType != null) {
            Intent activity = new Intent();
            activity.setClass(LoginActivity.this, HomeActivity.class);
            activity.putExtra("userId", register.userId);
            activity.putExtra("userName", register.username);
            activity.putExtra("userType", register.userType);
            activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(activity);
            finish();
        }

        btnLogin = (Button) findViewById(R.id.loginButtonLogin);
        txtUserName = (EditText) findViewById(R.id.loginTextUserName);
        txtPassword = (EditText) findViewById(R.id.loginTextPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (TextUtils.isEmpty(txtPassword.getText().toString()) || TextUtils.isEmpty(txtUserName.getText().toString())) {

                    showSnackBar(getString(R.string.emptyFieldError), findViewById(R.id.activity_main_coordinatorLayout));

                } else {
                    if (CommonUtil.checkInternetConnection(LoginActivity.this)) {
                        LoginAPI authentication = new LoginAPI(getApplicationContext());
                        authentication.execute(txtUserName.getText().toString(), txtPassword.getText().toString());
                    } else {
                        showSnackBar(getString(R.string.checkConnection), findViewById(R.id.activity_main_coordinatorLayout));
                    }
                }

            }
        });

    }


    class LoginAPI extends AsyncTask<String, Void, RegisterUsers> {
        Context appContext;

        public LoginAPI (Context context) {
            appContext = context;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Verifying Credentials...");
        }

        @Override
        protected void onPostExecute(RegisterUsers registerArg) {
            hideProgressDialog();
            register = registerArg;
            if (register.userType != null) {
                Intent activity = new Intent();
                activity.setClass(LoginActivity.this, HomeActivity.class);
                activity.putExtra("userId", register.userId);
                activity.putExtra("userName", register.username);
                activity.putExtra("userType", register.userType);
                savingUserInformation();
                activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activity);
                finish();
            } else {
                showSnackBar("User Name/Password is incorrect. Please enter correct credentials.", findViewById(R.id.activity_main_coordinatorLayout));
            }
        }

        @Override
        protected RegisterUsers doInBackground(String... params) {
            RegisterUsers register = new RegisterUsers();
            Call<List<RegisterUsers>> callAuth = Api.getClient().authenticate(params[0], params[1]);
            try {
                Response<List<RegisterUsers>> respAuth = callAuth.execute();
                if (respAuth != null && respAuth.isSuccessful() & respAuth.body() != null && respAuth.body().size() > 0) {
                    register = respAuth.body().get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return register;
        }
    }

}
