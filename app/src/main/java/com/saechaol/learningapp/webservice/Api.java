package com.saechaol.learningapp.webservice;

import com.saechaol.learningapp.util.CommonUtil;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Retrofit retrofit = null;

    public static ApiInterface getClient() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = UnsafeOkHttpClient.getLoginUnsafeOkHttpClient();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(CommonUtil.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }

}
