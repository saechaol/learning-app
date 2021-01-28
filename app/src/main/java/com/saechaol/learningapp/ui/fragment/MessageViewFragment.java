package com.saechaol.learningapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import android.app.Fragment;

public class MessageViewFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //This code will launch gmail app which is installed on the client
        Intent gmailIntent = MessageViewFragment.this.getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        startActivity(gmailIntent);

        return super.onCreateView(inflater, container, savedInstanceState);


    }

}
