package com.utt.gymbros;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.MembershipModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenDialogCreateMembership extends DialogFragment {

    private TextInputEditText productNameEditText, descriptionEditText, priceEditText, discountEditText, durationDaysEditText, sizeEditText;
    private MaterialButton saveButton, cancelButton;
    private ProgressBar progressBar;
    private String token;

    public static FullScreenDialogCreateMembership newInstance(String token) {
        FullScreenDialogCreateMembership fragment = new FullScreenDialogCreateMembership();
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_membership_admin, container, false);

        productNameEditText = view.findViewById(R.id.productNameEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        discountEditText = view.findViewById(R.id.discountEditText);
        durationDaysEditText = view.findViewById(R.id.durationDaysEditText);
        sizeEditText = view.findViewById(R.id.sizeEditText);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        progressBar = view.findViewById(R.id.progressBar);

        token = getArguments().getString("token");

        saveButton.setOnClickListener(v -> createMembership());
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().setCancelable(false); // Evitar cerrar al tocar fuera del diálogo
        }

        setCancelable(false);
    }

    private void createMembership() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        String productName = productNameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String price = priceEditText.getText().toString();
        String discount = discountEditText.getText().toString();
        int durationDays = Integer.parseInt(durationDaysEditText.getText().toString());
        int size = Integer.parseInt(sizeEditText.getText().toString());

        MembershipModel.CreateMembershipRequest request = new MembershipModel.CreateMembershipRequest(
                productName,
                description,
                price,
                discount,
                2, // category_id hardcoded
                durationDays,
                size
        );

        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        apiService.createMembership(request, "Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Snackbar.make(getView(), "Membresía creada", Snackbar.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Snackbar.make(getView(), "Error al crear la membresía", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                Snackbar.make(getView(), "Error al crear la membresía", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(productNameEditText.getText())) {
            productNameEditText.setError("El nombre de la membresía es obligatorio");
            return false;
        }

        if (TextUtils.isEmpty(descriptionEditText.getText())) {
            descriptionEditText.setError("La descripción es obligatoria");
            return false;
        }

        if (TextUtils.isEmpty(priceEditText.getText())) {
            priceEditText.setError("El precio es obligatorio");
            return false;
        }

        if (TextUtils.isEmpty(discountEditText.getText())) {
            discountEditText.setError("El descuento es obligatorio");
            return false;
        }

        if (TextUtils.isEmpty(durationDaysEditText.getText())) {
            durationDaysEditText.setError("La duración en días es obligatoria");
            return false;
        }

        if (TextUtils.isEmpty(sizeEditText.getText())) {
            sizeEditText.setError("El tamaño es obligatorio");
            return false;
        }

        return true;
    }
}
