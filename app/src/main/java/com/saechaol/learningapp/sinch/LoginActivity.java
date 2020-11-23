package com.saechaol.learningapp.sinch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.BaseActivity;
import com.sinch.android.rtc.SinchError;

/**
 * Provides support for user login and verification
 */
public class LoginActivity extends BaseActivity implements SinchService.FailedListenerInterface {

    private Button loginButton;
    private EditText loginName;
    private ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginName = (EditText) findViewById(R.id.loginName);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener((v)->{
            loginClicked();
        });
    }

    @Override
    protected void onServiceConnected() {
        loginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (spinner != null) {
            spinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        if (spinner != null) {
            spinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        startPlaceCallActivity();
    }

    private void loginClicked() {
        String username = loginName.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(username);
            showSpinner();
        } else {
            startPlaceCallActivity();
        }
    }

    private void showSpinner() {
        spinner = new ProgressDialog(this);
        spinner.setTitle("Logging in");
        spinner.setMessage("Please wait...");
        spinner.show();
    }

    private void startPlaceCallActivity() {
        Intent activity = new Intent(this, PlaceCallActivity.class);
        startActivity(activity);
    }

}
