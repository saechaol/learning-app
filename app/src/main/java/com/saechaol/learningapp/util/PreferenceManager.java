package com.saechaol.learningapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private Context context;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Learning App", Context.MODE_PRIVATE);
    }

    public void clearData() {
        sharedPreferences
                .edit()
                .clear()
                .commit();
    }

    public void saveData(String key, int value) {
        sharedPreferences
                .edit()
                .putInt(key, value)
                .commit();
    }

    public void saveData(String key, String value) {
        sharedPreferences
                .edit()
                .putString(key, value)
                .commit();
    }

    public void saveData(String key, long value) {
        sharedPreferences
                .edit()
                .putLong(key, value)
                .commit();
    }

    public void saveData(String key, boolean value) {
        sharedPreferences
                .edit()
                .putBoolean(key, value)
                .commit();
    }

    public String getStringData(String key) {
        return sharedPreferences.getString(key, "");
    }


    public int getIntData(String key) {
        return sharedPreferences.getInt(key, -1);
    }


    public long getLongData(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public boolean getBoolData(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

}
