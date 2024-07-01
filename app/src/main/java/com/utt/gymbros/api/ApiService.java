package com.utt.gymbros.api;

import com.utt.gymbros.model.AuthModel;
import com.utt.gymbros.model.MembershipModel;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login/user")
    Call<AuthModel.LoginResponse> login(@Body AuthModel.LoginRequest loginRequest);

    @POST("verify-2fa")
    Call<AuthModel.VerifyCodeResponse> verifyCode(@Body AuthModel.VerifyCodeRequest verifyCodeRequest);

    @GET("membresias/all")
    Call<List<MembershipModel.Membership>> getMemberships(@Header("Authorization") String authToken);
}

