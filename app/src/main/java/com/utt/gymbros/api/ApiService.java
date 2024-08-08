package com.utt.gymbros.api;

import com.utt.gymbros.model.AuthModel;
import com.utt.gymbros.model.MembershipModel;
import com.utt.gymbros.model.OrderModel;
import com.utt.gymbros.model.PaymentsModel;
import com.utt.gymbros.model.ProductModel;
import com.utt.gymbros.model.VisitUserModel;

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
    // region Autenticación
    @POST("login/user")
    Call<AuthModel.LoginResponse> login(@Body AuthModel.LoginRequest loginRequest);

    @POST("verify-2fa")
    Call<AuthModel.VerifyCodeResponse> verifyCode(@Body AuthModel.VerifyCodeRequest verifyCodeRequest);

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String authToken);
    // endregion

    // region Membresías - Administrador
    @POST("membresias")
    Call<Void> createMembership(@Body MembershipModel.CreateMembershipRequest request, @Header("Authorization") String token);

    @GET("membresias/all")
    Call<List<MembershipModel.Membership>> getMemberships(@Header("Authorization") String authToken);

    @PUT("membresias/{id}/toggle-active")
    Call<Void> toggleMembershipActive(
            @Path("id") int membershipId,
            @Header("Authorization") String authToken
    );

    @PUT("membresias/{id}")
    Call<Void> updateMembership(
            @Path("id") int membershipId,
            @Body MembershipModel.Membership membership,
            @Header("Authorization") String authToken
    );

    @DELETE("membresias/{id}")
    Call<Void> deleteMembership(
            @Path("id") int membershipId,
            @Header("Authorization") String authToken
    );
    // endregion

    // region Membresías - Usuario
    @GET("membresias")
    Call<List<MembershipModel.Membership>> getMembershipsUser(@Header("Authorization") String authToken);



    // endregion

    // region Órdenes - Administrador
    @GET("orders")
    Call<List<OrderModel.Order>> getAllOrders(@Header("Authorization") String authToken);

    @DELETE("orders/{id}")
    Call<Void> cancelOrder(@Path("id") int orderId, @Header("Authorization") String authToken);
    // endregion

    // region Visitas - Administrador
    @GET("visits")
    Call<List<VisitUserModel.Visit>> getAllVisits(@Header("Authorization") String authToken);
    // endregion

    // region Productos - Administrador
    @POST("productos")
    Call<Void> createProduct(@Body ProductModel.CreateProductRequest request, @Header("Authorization") String token);

    @GET("productos/all")
    Call<List<ProductModel.Product>> getProducts(@Header("Authorization") String authToken);

    @PUT("productos/{id}")
    Call<Void> updateProduct(
            @Path("id") int productId,
            @Body ProductModel.Product product,
            @Header("Authorization") String authToken
    );

    @PUT("productos/{id}/toggle-active")
    Call<Void> toggleProductActive(
            @Path("id") int productId,
            @Header("Authorization") String authToken
    );

    @DELETE("productos/{id}")
    Call<Void> deleteProduct(
            @Path("id") int productId,
            @Header("Authorization") String authToken
    );
    // endregion

    // region Stripe
    @POST("orders/memberships")
    Call<PaymentsModel.BuyMembershipResponse> buyMembership(@Body PaymentsModel.BuyMembershipRequest request, @Header("Authorization") String authToken);

    @POST("orders/{orderId}/create-payment-intent-mobile")
    Call<PaymentsModel.CreatePaymentIntentResponse> createPaymentIntent(
            @Path("orderId") int orderId,
            @Body PaymentsModel.CreatePaymentIntentRequest request,
            @Header("Authorization") String authToken
    );

    @POST("orders/cancel-payment-intent-mobile")
    Call<Void> cancelPaymentIntent(@Body PaymentsModel.cancelPaymentIntentRequest request, @Header("Authorization") String authToken);

    @POST("orders/confirm-stripe-payment-mobile")
    Call<Void> confirmStripePayment(@Body PaymentsModel.confirmPaymentIntentRequest request, @Header("Authorization") String authToken);

    @POST("orders/{orderId}/stripe-payment-mobile")
    Call<Void> stripePayment(
            @Path("orderId") int orderId,
            @Body PaymentsModel.storePaymentRequest request,
            @Header("Authorization") String authToken
    );
    // endregion

}
