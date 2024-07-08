package com.utt.gymbros.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Create OkHttpClient with default timeouts
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();

            // Build Retrofit instance with the custom OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.100.2:8000/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient) // Use the custom client
                    .build();
        }
        return retrofit;
    }
}
