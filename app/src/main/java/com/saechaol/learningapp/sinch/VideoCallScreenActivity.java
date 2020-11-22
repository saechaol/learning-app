package com.saechaol.learningapp.sinch;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.saechaol.learningapp.R;
import com.saechaol.learningapp.ui.activity.BaseActivity;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class VideoCallScreenActivity extends BaseActivity {

    static final String CALL_TAG = CallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer audioPlayer;
    private Timer timer;
    private UpdateCallDurationTask durationTask;

    private String callId;
    private long callStart = 0;
    private boolean addedListener = false;
    private boolean videoViewsAdded = false;

    private TextView callDuration;
    private TextView callState;
    private TextView callerName;

    private void addVideoViews() {
        if (videoViewsAdded || getSinchServiceInterface() == null) {
            return;
        }

        final VideoController videoController = getSinchServiceInterface().getVideoController();
        if (videoController != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(videoController.getLocalView());
            localView.setOnClickListener((v)->{
                videoController.toggleCaptureDevicePosition();
            });

            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.addView(videoController.getRemoteView());
            videoViewsAdded = true;
        }
    }

    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return;
        }

        VideoController videoController = getSinchServiceInterface().getVideoController();
        if (videoController != null) {
            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.removeView(videoController.getRemoteView());

            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.removeView(videoController.getLocalView());
            videoViewsAdded = false;
        }
    }

    private void updateCallDuration() {
        if (callStart > 0) {
            callDuration.setText(formatTimespan(System.currentTimeMillis() - callStart));
        }
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        return String.format(Locale.US, "%02d:%02d", (totalSeconds / 60), (totalSeconds % 60));
    }

    private void endCall() {
        audioPlayer.stopDialTone();
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStart() {
        super.onStart();
        timer = new Timer();
        durationTask = new UpdateCallDurationTask();
        timer.schedule(durationTask, 0, 500);
        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        durationTask.cancel();
        timer.cancel();
        removeVideoViews();
    }

    private void updateUI() {
        if (getSinchServiceInterface() == null) {
            return;
        }

        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            callerName.setText(call.getRemoteUserId());
            callState.setText(call.getState().toString());
            if (call.getState() == CallState.ESTABLISHED) {
                addVideoViews();
            }
        }
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            if (!addedListener) {
                call.addCallListener(new SinchCallListener());
                addedListener = true;
            }
        } else {
            Log.e(CALL_TAG, "Call ID is invalid");
            finish();
        }
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_screen_activity);

        audioPlayer = new AudioPlayer(this);
        callDuration = (TextView) findViewById(R.id.callDuration);
        callerName = (TextView) findViewById(R.id.remoteUser);
        callState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);
        endCallButton.setOnClickListener((v)-> {
            endCall();
        });
        callId = getIntent().getStringExtra(SinchService.CALL_ID);
        if (savedInstanceState == null) {
            callStart = System.currentTimeMillis();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        callStart = savedInstanceState.getLong(CALL_START_TIME);
        addedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(CALL_START_TIME, callStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, addedListener);
    }

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            VideoCallScreenActivity.this.runOnUiThread(() -> {
                updateCallDuration();
            });
        }
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(CALL_TAG, "Video track added");
            addVideoViews();
        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(CALL_TAG, "Call progressing...");
            audioPlayer.playDialTone();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(CALL_TAG, "Call established...");
            audioPlayer.stopDialTone();
            callState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            callStart = System.currentTimeMillis();
            Log.d(CALL_TAG, "Call offered video: " + call.getDetails().isVideoOffered());
        }

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(CALL_TAG, "The call has ended because " + cause.toString());
            audioPlayer.stopDialTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMessage = "Call ended: " + call.getDetails().toString();
            Toast.makeText(VideoCallScreenActivity.this, endMessage, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}
