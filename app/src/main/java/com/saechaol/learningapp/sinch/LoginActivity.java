package com.saechaol.learningapp.sinch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.saechaol.learningapp.ui.activity.BaseActivity;

public class LoginActivity extends BaseActivity implements SinchService.FailedListenerInterface {

    private Button loginButton;
    private EditText loginName;
    private ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

}
