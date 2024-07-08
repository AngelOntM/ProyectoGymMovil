package com.utt.gymbros.api;

import com.utt.gymbros.model.AuthModel;
import com.utt.gymbros.model.MembershipModel;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Autenticación
    @POST("login/user")
    Call<AuthModel.LoginResponse> login(@Body AuthModel.LoginRequest loginRequest);

    @POST("verify-2fa")
    Call<AuthModel.VerifyCodeResponse> verifyCode(@Body AuthModel.VerifyCodeRequest verifyCodeRequest);


    ////////////// Membresias //////////////
    //Crear una membresia
    @POST("membresias")
    Call<Void> createMembership(@Body MembershipModel.CreateMembershipRequest request, @Header("Authorization") String token);

    //Obtener todas las membresias
    @GET("membresias/all")
    Call<List<MembershipModel.Membership>> getMemberships(@Header("Authorization") String authToken);

    //Cambiar el estado de una membresia (activa/inactiva)
    @PUT("membresias/{id}/toggle-active")
    Call<Void> toggleMembershipActive(
            @Path("id") int membershipId,
            @Header("Authorization") String authToken
    );

    //Modificar una membresia
    @PUT("membresias/{id}")
    Call<Void> updateMembership(
            @Path("id") int membershipId,
            @Body MembershipModel.Membership membership,
            @Header("Authorization") String authToken
    );

    //Eliminar una membresia
    @DELETE("membresias/{id}")
    Call<Void> deleteMembership(
            @Path("id") int membershipId,
            @Header("Authorization") String authToken
    );
}

