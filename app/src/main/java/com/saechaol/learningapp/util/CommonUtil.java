package com.saechaol.learningapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

public class CommonUtil {

    public static String baseUrl = "https://mla-lb-924149125.us-west-1.elb.amazonaws.com/MlaWebApi/";
    public static String EXTRA_USER_ADMIN_DATA = "extra_user_admin_data";
    public static String EXTRA_IS_TO_ADD = "extra_is_to_add";
    public static String EXTRA_EDIT_MODE = "extra_edit_mode";

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean isValidMobile(String phone) {
        return phone.length() == 10;
    }

    public static boolean isValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
