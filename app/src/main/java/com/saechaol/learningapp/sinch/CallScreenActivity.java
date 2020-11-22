package com.saechaol.learningapp.sinch;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.BaseActivity;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity {

    static final String CALL_TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer audioPlayer;
    private Timer timer;
    private UpdateCallDurationTask durationTask;

    private String callId;

    private TextView callDuration;
    private TextView callState;
    private TextView callerName;

    private class UpdateCallDurationTask extends TimerTask {
        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(()->{
                updateCallDuration();
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        audioPlayer = new AudioPlayer(this);
        callDuration = (TextView) findViewById(R.id.callDuration);
        callerName = (TextView) findViewById(R.id.remoteUser);
        callState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);
        endCallButton.setOnClickListener((v)->{
            endCall();
        });
        callId = getIntent().getStringExtra(SinchService.CALL_ID);
        setToolbarTitle("Calling...");
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            callerName.setText(call.getRemoteUserId());
            callState.setText(call.getState().toString());
        } else {
            Log.e(CALL_TAG, "Invalid Call ID");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        durationTask.cancel();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        durationTask = new UpdateCallDurationTask();
        timer.schedule(durationTask, 0, 500);
    }

    @Override
    public void onBackPressed() {

    }

    private void endCall() {
        audioPlayer.stopDialTone();
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(int totalSeconds) {
        return String.format(Locale.US, "%02d:%02d", (totalSeconds / 60), (totalSeconds % 60));
    }

    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            callDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(CALL_TAG, "Call ended because " + cause.toString());
            audioPlayer.stopDialTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMessage = "Call ended: " + call.getDetails().toString();
            Toast.makeText(CallScreenActivity.this, endMessage, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(CALL_TAG, "Call established...");
            audioPlayer.stopDialTone();
            callState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(CALL_TAG, "Call progressing...");
            audioPlayer.playDialTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {

        }

    }

}
