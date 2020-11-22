package com.saechaol.learningapp.sinch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.BaseActivity;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String CALL_TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String callId;
    private boolean isVideoOn;
   private AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);

        Button answer = (Button) findViewById(R.id.answerButton);
        answer.setOnClickListener(clickListener);

        Button decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(clickListener);

        audioPlayer = new AudioPlayer(this);
        audioPlayer.playRingtone();
        callId = getIntent().getStringExtra(SinchService.CALL_ID);
        isVideoOn = getIntent().getBooleanExtra(SinchService.VIDEO_CALL, false);
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
            remoteUser.setText(call.getRemoteUserId());

            if (call.getDetails().isVideoOffered()) {
                if (Build.VERSION.SDK_INT >= 23) {
                    ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.CAMERA
                    }, 0);
                }
            }
        } else {
            Log.e(CALL_TAG, "Invalid call ID");
            finish();
        }
    }

    private void answerClicked() {
        audioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            try {
                call.answer();
                if (call.getDetails().isVideoOffered()) {
                    Intent intent = new Intent(this, VideoCallScreenActivity.class);
                    intent.putExtra(SinchService.CALL_ID, callId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, CallScreenActivity.class);
                    intent.putExtra(SinchService.CALL_ID, callId);
                    startActivity(intent);
                }
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[] {
                        e.getRequiredPermission()
                }, 0);
            }
        } else {
            finish();
        }
    }

    private void declineClicked() {
        audioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You now have permissions to answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application requires additional permissions to make this call", Toast.LENGTH_LONG).show();
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(CALL_TAG, "Call ended because " + cause.toString());
            audioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(CALL_TAG, "Call established...");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(CALL_TAG, "Call progressing...");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // send push notification through push provider
        }

    }

    private View.OnClickListener clickListener = (v) -> {
      switch (v.getId()) {
          case R.id.answerButton:
              answerClicked();
              break;

          case R.id.declineButton:
              declineClicked();
              break;
      }
    };
}
