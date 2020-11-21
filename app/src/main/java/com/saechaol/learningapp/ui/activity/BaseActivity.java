package com.saechaol.learningapp.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.Snackbar;
import com.saechaol.learningapp.sinch.SinchService;
import com.saechaol.learningapp.util.PreferenceManager;
import com.sinch.android.rtc.SinchError;

/**
 * Base activity class for the UI
 */
public class BaseActivity extends AppCompatActivity implements ServiceConnection, SinchService.FailedListenerInterface {

    private SinchService.SinchServiceInterface sinchServiceInterface;
    PreferenceManager preferenceManager;
    private ProgressDialog progressDialog;
    public static String ACTION_FINISH = "ACTION_FINISH";
    private FinishReceiver receiver;

    @Override
    public void onStarted() {

    }

    @Override
    public void onStartFailed(SinchError e) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
        receiver = new FinishReceiver();
        preferenceManager = new PreferenceManager(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ACTION_FINISH));
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder binderInterface) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            sinchServiceInterface = (SinchService.SinchServiceInterface) binderInterface;
            onServiceConnected();
        }
    }

    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        if (!preferenceManager.getStringData("userName").equalsIgnoreCase("")) {
            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(preferenceManager.getStringData("userName"));
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(getComponentName().getClassName())) {
            sinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceDisconnected() {

    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return sinchServiceInterface;
    }

    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public class FinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void showSnackBar(String message, View view) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showProgressDialog(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(this, "Learning App", message, true, false);
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void hideKeyboard() {
        // check if any views have focus
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
