package com.saechaol.learningapp.sinch;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoController;

public class SinchService extends Service {

    private static final String APP_KEY = "939347d5-885d-4f6e-93bd-585b9a050701";
    private static final String APP_SECRET = "8g7F5mkj/kq1ms3FhZyqwQ==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    static final String TAG = SinchService.class.getSimpleName();

    private SinchServiceInterface sinchServiceInterface = new SinchServiceInterface();
    private SinchClient sinchClient;
    private String localUserId;

    public static final String CALL_ID = "CALL_ID";
    public static final String VIDEO_CALL = "VIDEO_CALL";

    private FailedListenerInterface clientListenerInterface;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (sinchClient != null && sinchClient.isStarted()) {
            sinchClient.terminate();
        }
    }
    private void start(String userName) {
        if (sinchClient == null) {
            localUserId = userName;
            sinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT).build();

            sinchClient.setSupportCalling(true);
            sinchClient.startListeningOnActiveConnection();
            sinchClient.setSupportActiveConnectionInBackground(true);
            sinchClient.startListeningOnActiveConnection();

            sinchClient.addSinchClientListener(new ClientListener());
            sinchClient.getCallClient().setRespectNativeCalls(false);
            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
            sinchClient.start();
        }
    }

    private void stop() {
        if (sinchClient != null) {
            sinchClient.terminate();
            sinchClient = null;
        }
    }

    private boolean isStarted() {
        return (sinchClient != null && sinchClient.isStarted());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sinchServiceInterface;
    }

    /**
     * Provides a publicly accessible interface for interactive with the Sinch Client Service object
     */
    public class SinchServiceInterface extends Binder {

        /**
         * Calls the phone number using the Sinch Call Client object
         * @param phoneNumber
         * @return
         */
        public Call callPhoneNumber(String phoneNumber) {
            return sinchClient.getCallClient().callPhoneNumber(phoneNumber);
        }

        /**
         * Places a video call to the user ID using the Sinch Call Client object
         * @param userId
         * @return
         */
        public Call callUserVideo(String userId) {
            return sinchClient.getCallClient().callUserVideo(userId);
        }

        /**
         * Places a call to the user with the given ID using the Sinch Call Client object
         * @param userId
         * @return
         */
        public Call callUser(String userId) {
            // Do nothing if the SinchClient object is not initialized
            if (sinchClient == null)
                return null;
            return sinchClient.getCallClient().callUser(userId);
        }

        /**
         * Returns the user name associated with this client
         * @return
         */
        public String getUserName() {
            return localUserId;
        }

        /**
         * Returns if the service has started
         * @return
         */
        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        /**
         * Initializes the client
         * @param userName
         */
        public void startClient(String userName) {
            start(userName);
        }

        /**
         * Stops and terminates the client
         */
        public void stopClient() { stop(); }

        public VideoController getVideoController() {
            if (!isStarted()) {
                return null;
            }
            return sinchClient.getVideoController();
        }

        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return sinchClient.getAudioController();
        }

        public void setStartListener(FailedListenerInterface listener) { clientListenerInterface = listener; }

        public Call getCall(String callId) { return sinchClient.getCallClient().getCall(callId); }

    }

    public interface FailedListenerInterface {
        void onStartFailed(SinchError error);

        void onStarted();
    }

    private class ClientListener implements SinchClientListener {

        /**
         * Notifies the listener that the client has successfully started
         * and is ready to run functions that execute at this time.
         *
         * @param sinchClient
         */
        @Override
        public void onClientStarted(SinchClient sinchClient) {
            Log.d(TAG, "SinchClient started");
            if (clientListenerInterface != null) {
                clientListenerInterface.onStarted();
            }
        }

        /**
         * Notifies to the log that the client has stopped
         *
         * @param sinchClient
         */
        @Override
        public void onClientStopped(SinchClient sinchClient) {
            Log.d(TAG, "SinchClient stopped");
        }

        /**
         * Terminates the Sinch client if it fails and notifies the listener
         *
         * @param sinchClient
         * @param sinchError
         */
        @Override
        public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
            if (clientListenerInterface != null)
                clientListenerInterface.onStartFailed(sinchError);
            sinchClient.terminate();
            sinchClient = null;
        }

        /**
         * Currently does nothing
         *
         * @param sinchClient
         * @param clientRegistration
         */
        @Override
        public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

        }

        /**
         * Writes to the log based on the given parameters
         *
         * @param i  the log type
         * @param s  the log area
         * @param s1 the log message
         */
        @Override
        public void onLogMessage(int i, String s, String s1) {
            switch (i) {
                case Log.DEBUG:
                    Log.d(s, s1);
                    break;
                case Log.ERROR:
                    Log.e(s, s1);
                    break;
                case Log.INFO:
                    Log.i(s, s1);
                    break;
                case Log.VERBOSE:
                    Log.v(s, s1);
                    break;
                case Log.WARN:
                    Log.w(s, s1);
                    break;
            }
        }
    }

    /**
     * Implemented call listener that checks to see if the call is intended
     * to be received or made as an audio or video call.
     */
    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "Incoming call");
            Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
            intent.putExtra(CALL_ID, call.getCallId());
            intent.putExtra(VIDEO_CALL, call.getDetails().isVideoOffered());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SinchService.this.startActivity(intent);
        }
    }

}
