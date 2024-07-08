package com.utt.gymbros;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.MembershipModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullscreenDialogEditMembership extends AppCompatDialogFragment {

    public static final String ARG_MEMBERSHIP = "membership";
    private MembershipModel.Membership membership;
    private ProgressBar progressBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_membership_admin, null);

        MaterialToolbar toolbar = dialogView.findViewById(R.id.toolbar);
        TextInputEditText productNameEditText = dialogView.findViewById(R.id.productNameEditText);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        TextInputEditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        TextInputEditText discountEditText = dialogView.findViewById(R.id.discountEditText);
        TextInputEditText durationDaysEditText = dialogView.findViewById(R.id.durationDaysEditText);
        TextInputEditText sizeEditText = dialogView.findViewById(R.id.sizeEditText);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        MaterialButton saveButton = dialogView.findViewById(R.id.saveButton);
        progressBar = dialogView.findViewById(R.id.progressBar);

        // Obtener la membresía del Bundle
        Bundle args = getArguments();
        if (args != null) {
            membership = (MembershipModel.Membership) args.getSerializable(ARG_MEMBERSHIP);
            if (membership != null) {
                // Rellenar los campos con los datos de la membresía
                productNameEditText.setText(membership.getProductName());
                descriptionEditText.setText(membership.getDescription());
                priceEditText.setText(membership.getPrice());
                discountEditText.setText(String.valueOf(membership.getDiscount()));
                durationDaysEditText.setText(String.valueOf(membership.getDurationDays()));
                sizeEditText.setText(String.valueOf(membership.getSize()));
            }
        }

        toolbar.setNavigationOnClickListener(v -> dismiss());
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> {
            if (validateInputs(productNameEditText, descriptionEditText, priceEditText, discountEditText, durationDaysEditText, sizeEditText)) {
                progressBar.setVisibility(View.VISIBLE);

                // Obtener los valores editados de los campos de texto
                String productName = productNameEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());
                int discount = Integer.parseInt(discountEditText.getText().toString());
                int durationDays = Integer.parseInt(durationDaysEditText.getText().toString());
                int size = Integer.parseInt(sizeEditText.getText().toString());

                // Actualizar el modelo membership
                membership.setProductName(productName);
                membership.setDescription(description);
                membership.setPrice(String.valueOf(price));
                membership.setDiscount(String.valueOf(discount));
                membership.setDurationDays(durationDays);
                membership.setSize(size);

                // Agregar el campo de imagen y activo
                membership.setProductImagePath(membership.getProductImagePath());
                membership.setActive(membership.getActive());
                // Hacer la petición para actualizar la membresía
                updateMembership(membership, dialogView);
            }
        });

        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private boolean validateInputs(TextInputEditText productNameEditText, TextInputEditText descriptionEditText, TextInputEditText priceEditText, TextInputEditText discountEditText, TextInputEditText durationDaysEditText, TextInputEditText sizeEditText) {
        if (productNameEditText.getText().toString().isEmpty()) {
            productNameEditText.setError("Nombre del producto es requerido");
            return false;
        }
        if (descriptionEditText.getText().toString().isEmpty()) {
            descriptionEditText.setError("Descripción es requerida");
            return false;
        }
        if (priceEditText.getText().toString().isEmpty()) {
            priceEditText.setError("Precio es requerido");
            return false;
        }
        if (discountEditText.getText().toString().isEmpty()) {
            discountEditText.setError("Descuento es requerido");
            return false;
        }
        if (durationDaysEditText.getText().toString().isEmpty()) {
            durationDaysEditText.setError("Duración en días es requerida");
            return false;
        }
        if (sizeEditText.getText().toString().isEmpty()) {
            sizeEditText.setError("Tamaño es requerido");
            return false;
        }
        return true;
    }

    private void updateMembership(MembershipModel.Membership membership, View dialogView) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        Call<Void> call = apiService.updateMembership(
                membership.getId(),
                new MembershipModel.Membership(
                        membership.getId(),
                        membership.getProductName(),
                        membership.getDescription(),
                        membership.getPrice(),
                        membership.getDiscount(),
                        membership.getActive(),
                        membership.getProductImagePath(),
                        membership.getDurationDays(),
                        membership.getSize()
                ),
                getToken()
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    dismiss();
                } else {
                    Snackbar.make(dialogView, "Error al actualizar membresia", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(dialogView, "Error en la solicitud", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String getToken() {
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    getContext().getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return "Bearer " + sharedPreferences.getString("auth_token", "");
    }
}
