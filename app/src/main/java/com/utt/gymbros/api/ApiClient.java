package com.utt.gymbros.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Create OkHttpClient with 90 seconds timeouts
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            // Build Retrofit instance with the custom OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://pasameporfavor.site/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient) // Use the custom client
                    .build();
        }
        return retrofit;
    }
}
