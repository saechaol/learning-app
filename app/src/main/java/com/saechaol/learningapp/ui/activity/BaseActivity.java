package com.saechaol.learningapp.ui.activity;

import android.content.ServiceConnection;

import androidx.appcompat.app.AppCompatActivity;

import com.saechaol.learningapp.sinch.SinchService;

/**
 * Base activity class for the UI
 */
public class BaseActivity extends AppCompatActivity implements ServiceConnection, SinchService.FailedListenerInterface {

    private SinchService.SinchServiceInterface sinchServiceInterface;


}
