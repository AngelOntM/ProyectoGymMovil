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
import com.utt.gymbros.model.ProductModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenDialogEditProduct extends AppCompatDialogFragment {

    public static final String ARG_PRODUCT = "product";
    private ProductModel.Product product;
    private ProgressBar progressBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_product_admin, null);

        MaterialToolbar toolbar = dialogView.findViewById(R.id.toolbar);
        TextInputEditText productNameEditText = dialogView.findViewById(R.id.productNameEditText);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        TextInputEditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        TextInputEditText discountEditText = dialogView.findViewById(R.id.discountEditText);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        MaterialButton saveButton = dialogView.findViewById(R.id.saveButton);
        progressBar = dialogView.findViewById(R.id.progressBar);

        // Obtener el producto del Bundle
        Bundle args = getArguments();
        if (args != null) {
            product = (ProductModel.Product) args.getSerializable(ARG_PRODUCT);
            if (product != null) {
                // Rellenar los campos con los datos del producto
                productNameEditText.setText(product.getProductName());
                descriptionEditText.setText(product.getDescription());
                priceEditText.setText(product.getPrice());
                discountEditText.setText(product.getDiscount());
            }
        }

        toolbar.setNavigationOnClickListener(v -> dismiss());
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> {
            if (validateInputs(productNameEditText, descriptionEditText, priceEditText, discountEditText)) {
                progressBar.setVisibility(View.VISIBLE);

                // Obtener los valores editados de los campos de texto
                String productName = productNameEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String price = priceEditText.getText().toString();
                String discount = discountEditText.getText().toString();

                // Actualizar el modelo de producto
                product.setProductName(productName);
                product.setDescription(description);
                product.setPrice(price);
                product.setDiscount(discount);

                // Hacer la petición para actualizar el producto
                updateProduct(product, dialogView);
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

    private boolean validateInputs(TextInputEditText productNameEditText, TextInputEditText descriptionEditText, TextInputEditText priceEditText, TextInputEditText discountEditText) {
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
        return true;
    }

    private void updateProduct(ProductModel.Product product, View dialogView) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        Call<Void> call = apiService.updateProduct(
                product.getId(),
                new ProductModel.Product(
                        product.getId(),
                        product.getProductName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock(),
                        product.getDiscount(),
                        product.isActive(),
                        product.getCategoryId(),
                        product.getCategoryName(),
                        product.getProductImagePath(),
                        product.getCreatedAt(),
                        product.getUpdatedAt()
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
                    Snackbar.make(dialogView, "Error al actualizar producto", Snackbar.LENGTH_LONG).show();
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
