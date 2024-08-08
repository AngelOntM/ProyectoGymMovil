package com.utt.gymbros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.MembershipModel;
import com.utt.gymbros.model.PaymentsModel.BuyMembershipRequest;
import com.utt.gymbros.model.PaymentsModel;

import com.stripe.android.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyMembershipAdapter extends RecyclerView.Adapter<BuyMembershipAdapter.MembershipViewHolder> {

    private static Integer USER_ID;
    private static String AUTH_TOKEN;
    private static String USER_EMAIL;
    private static String USER_NAME;
    private static String STRIPE_PUBLISHABLE_KEY = "pk_test_51PiLliAvmYkVRZQNHFgKZm3BYNAodJuzqZ8ApCZScOV2L5wPp2761HfhogfqEtMmKwMCTO6tR7wrog9UMwML2KAT000HWEGzKG";
    private static String STRIPE_SECRET_KEY = "sk_test_51PiLliAvmYkVRZQNbZwbXJ0icXaF8fGjZWsH8BIm0DI5ufcYQJrtcQU7pCsDm4pC98euDETxIKgBKZdtPniS74MA00MQ7mzQRG";
    private final List<MembershipModel.Membership> membershipList;
    private final Context context;
    private final FullScreenDialogBuyMembership dialogFragment;
    private androidx.appcompat.app.AlertDialog progressDialog;
    PaymentSheet paymentSheet;

    public BuyMembershipAdapter(Context context, List<MembershipModel.Membership> membershipList, FullScreenDialogBuyMembership dialogFragment) {
        this.context = context;
        this.membershipList = membershipList;
        this.dialogFragment = dialogFragment;
    }

    @NonNull
    @Override
    public MembershipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_membership, parent, false);
        return new MembershipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipViewHolder holder, int position) {
        progressDialog = new MaterialAlertDialogBuilder(context)
                .setView(R.layout.progress_dialog_waiting)
                .setCancelable(false)
                .create();

        MembershipModel.Membership membership = membershipList.get(position);
        holder.title.setText(membership.getProductName());
        holder.description.setText(membership.getDescription());
        holder.price.setText("Precio: $" + membership.getPrice());

        if (membership.getProductImagePath().isEmpty()) {
            holder.image.setImageResource(R.drawable.ic_placeholder);
        } else {
            Picasso.get().load(membership.getProductImagePath()).into(holder.image);
        }

        holder.buyButton.setOnClickListener(v -> {
            progressDialog.show();
            getUserId();
            getAuthToken();
            getUserEmail();
            getUserName();
            int membershipId = membership.getId();

            if (USER_ID != null && !AUTH_TOKEN.isEmpty() && !USER_EMAIL.isEmpty() && !USER_NAME.isEmpty()) {
                BuyMembershipRequest request = new BuyMembershipRequest(USER_ID, membershipId);
                String membershipDescription = "Compra de Membresía: "+membership.getProductName() + " - " + USER_NAME +" - ID:"+ USER_ID;
                makeBuyMembershipRequest(request, AUTH_TOKEN, holder.itemView, membershipDescription);
            } else {
                Snackbar.make(v, "Error al obtener datos del usuario.", Snackbar.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return membershipList.size();
    }

    public static class MembershipViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price;
        ImageView image;
        Button buyButton;

        public MembershipViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_membership);
            description = itemView.findViewById(R.id.description_membership);
            price = itemView.findViewById(R.id.price_membership);
            image = itemView.findViewById(R.id.image_membership);
            buyButton = itemView.findViewById(R.id.button_buy_membership);
        }
    }

    public void getUserId() {
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        USER_ID = sharedPreferences.getInt("id", -1);
    }

    public void getAuthToken() {
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        AUTH_TOKEN = sharedPreferences.getString("auth_token", "");
    }

    public void getUserEmail(){
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        USER_EMAIL = sharedPreferences.getString("email", "");
    }

    public void getUserName(){
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        USER_NAME = sharedPreferences.getString("name", "");
    }

    private void makeBuyMembershipRequest(BuyMembershipRequest request, String authToken, View itemView, String membershipDescription) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<PaymentsModel.BuyMembershipResponse> call = apiService.buyMembership(request, "Bearer " + authToken);
        call.enqueue(new Callback<PaymentsModel.BuyMembershipResponse>() {
            @Override
            public void onResponse(Call<PaymentsModel.BuyMembershipResponse> call, Response<PaymentsModel.BuyMembershipResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentsModel.BuyMembershipResponse buyMembershipResponse = response.body();
                    createPaymentIntent(buyMembershipResponse.getId(), authToken, itemView, membershipDescription);
                } else {
                    Snackbar.make(itemView, "Error al comprar membresía.", Snackbar.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<PaymentsModel.BuyMembershipResponse> call, Throwable t) {
                Snackbar.make(itemView, "Error al comprar membresía.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void createPaymentIntent(int orderId, String authToken, View itemView, String membershipDescription) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        PaymentsModel.CreatePaymentIntentRequest request = new PaymentsModel.CreatePaymentIntentRequest(membershipDescription, USER_EMAIL);
        Call<PaymentsModel.CreatePaymentIntentResponse> call = apiService.createPaymentIntent(orderId, request, "Bearer " + authToken);
        call.enqueue(new Callback<PaymentsModel.CreatePaymentIntentResponse>() {
            @Override
            public void onResponse(Call<PaymentsModel.CreatePaymentIntentResponse> call, Response<PaymentsModel.CreatePaymentIntentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentsModel.CreatePaymentIntentResponse paymentIntentResponse = response.body();

                    //Enviar orderID, client_secret y payment_intent_id a la actividad de pago
                    Intent intent = new Intent(context, CheckoutActivity.class);
                    intent.putExtra("orderID", orderId);
                    intent.putExtra("client_secret", paymentIntentResponse.getClientSecret());
                    intent.putExtra("payment_intent_id", paymentIntentResponse.getPaymentIntentId());
                    progressDialog.dismiss();
                    context.startActivity(intent);
                } else {
                    Snackbar.make(itemView, "Error al crear intención de pago.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentsModel.CreatePaymentIntentResponse> call, Throwable t) {
                Snackbar.make(itemView, "Error al crear intención de pago.", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
