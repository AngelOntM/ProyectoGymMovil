package com.utt.gymbros;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.squareup.picasso.Picasso;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.MembershipModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MembershipViewHolder> {

    private final List<MembershipModel.Membership> membershipList;
    private final Context context;
    private final MembershipsAdminFragment fragment;

    public MembershipAdapter(Context context, List<MembershipModel.Membership> membershipList, MembershipsAdminFragment fragment) {
        this.context = context;
        this.membershipList = membershipList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MembershipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membership, parent, false);
        return new MembershipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipViewHolder holder, int position) {
        MembershipModel.Membership membership = membershipList.get(position);
        holder.title.setText(membership.getProductName());

        if (membership.isActive()) {
            holder.description.setText("Membresía activa");
            holder.description.setTextColor(Color.GREEN);
        } else {
            holder.description.setText("Membresía inactiva");
            holder.description.setTextColor(Color.RED);
        }

        holder.price.setText(membership.getPrice());

        if (membership.getProductImagePath().isEmpty()) {
            holder.image.setImageResource(R.drawable.ic_placeholder);
        } else {
            Picasso.get().load(membership.getProductImagePath()).into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_membership_details, null);
            TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
            TextView dialogDescription = dialogView.findViewById(R.id.dialog_description);
            TextView dialogPrice = dialogView.findViewById(R.id.dialog_price);
            SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switch_active);

            dialogTitle.setText(membership.getProductName());
            dialogDescription.setText(membership.getDescription());
            dialogPrice.setText(membership.getPrice());

            activeSwitch.setChecked(membership.isActive());

            activeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                MembershipModel.Membership currentMembership = membershipList.get(position);
                currentMembership.setActive(isChecked ? true : false);
                updateMembershipActive(currentMembership.getId(), isChecked);
                fragment.updateMembershipActive(currentMembership);
            });

            new MaterialAlertDialogBuilder(context)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Editar", (dialog, which) -> {
                        FullscreenDialogEditMembership fullscreenDialog = new FullscreenDialogEditMembership();
                        Bundle args = new Bundle();
                        args.putSerializable("membership", membership); // Pasamos la membresía al diálogo
                        fullscreenDialog.setArguments(args);
                        fullscreenDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "fullscreen_dialog");
                    })
                    .setNeutralButton("Eliminar", (dialog, which) -> {
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("Eliminar membresía")
                                .setMessage("¿Estás seguro de que deseas eliminar esta membresía?")
                                .setCancelable(false)
                                .setPositiveButton("Sí", (dialog1, which1) -> deleteMembership(membership.getId(), position))
                                .setNegativeButton("No", (dialog1, which1) -> dialog1.dismiss())
                                .show();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return membershipList.size();
    }

    public void removeMembership(int position) {
        membershipList.remove(position);
        notifyItemRemoved(position);
    }
    private void deleteMembership(int membershipId, int position) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.deleteMembership(membershipId, getToken());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removeMembership(position);
                    Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Membresía eliminada", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error al eliminar la membresía", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error en la solicitud", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMembershipActive(int membershipId, boolean isActive) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.toggleMembershipActive(membershipId, getToken());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error al actualizar la membresía", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error en la solicitud", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private String getToken() {
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context.getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return "Bearer " + sharedPreferences.getString("auth_token", "");
    }

    public static class MembershipViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price;
        ImageView image;

        public MembershipViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_membership);
            description = itemView.findViewById(R.id.description_membership);
            price = itemView.findViewById(R.id.price_membership);
            image = itemView.findViewById(R.id.image_membership);
        }
    }
}
