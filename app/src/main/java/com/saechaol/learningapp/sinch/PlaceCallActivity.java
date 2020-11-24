package com.saechaol.learningapp.sinch;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.BaseActivity;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

public class PlaceCallActivity extends BaseActivity {

    private Button callButton;
    private TextView callName;

    public static final String CALL_ID = "CALL_ID";
    public static final String VIDEO_CALL = "VIDEO_CALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        callName = (TextView) findViewById(R.id.callName);
        callName.setText(getIntent().getStringExtra(CALL_ID));
        callButton = (Button) findViewById(R.id.callButton);
        callButton.setEnabled(false);
        callButton.setOnClickListener(buttonClickListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setToolbarTitle("Place Call");

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);
        if (getIntent().hasExtra(VIDEO_CALL) && getIntent().getBooleanExtra(VIDEO_CALL, false)) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, 0);
        }
    }

    @Override
    protected void onServiceConnected() {
        callButton.setEnabled(true);
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void callButtonClicked() {
        String username = callName.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter the username of the person you would like to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Call call = null;
            if (getIntent().hasExtra(VIDEO_CALL) && getIntent().getBooleanExtra(VIDEO_CALL, false)) {
                call = getSinchServiceInterface().callUserVideo(username);
            } else {
                call = getSinchServiceInterface().callUser(username);
            }
            if (call == null) {
                Toast.makeText(this, "The Sinch Service has failed to start. Please restart the Sinch Service and place the call again", Toast.LENGTH_LONG).show();
                return;
            }

            String callId = call.getCallId();
            if (getIntent().hasExtra(VIDEO_CALL) && getIntent().getBooleanExtra(VIDEO_CALL, false)) {
                Intent callScreen = new Intent(this, VideoCallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            } else {
                Intent callScreen = new Intent(this, CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            }

        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[] {
                    e.getRequiredPermission()
            }, 0);
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You now have permissions to place calls", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application requires additional permissions", Toast.LENGTH_LONG).show();
        }
    }

    private OnClickListener buttonClickListener = (v) -> {
        switch(v.getId()) {
            case R.id.callButton:
                callButtonClicked();
                break;
            case R.id.stopButton:
                stopButtonClicked();
                break;
        }
    };
}
