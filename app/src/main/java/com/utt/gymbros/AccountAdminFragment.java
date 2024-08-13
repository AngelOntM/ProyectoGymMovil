package com.utt.gymbros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.AuthModel;
import com.utt.gymbros.model.UserModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAdminFragment extends Fragment {
    public static final String ARG_TOKEN = "token";
    androidx.appcompat.app.AlertDialog progressDialogLogout;
    androidx.appcompat.app.AlertDialog progressDialogWaiting;

    public static Fragment newInstance(String token) {
        AccountAdminFragment fragment = new AccountAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String token = getArguments().getString(ARG_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_admin, container, false);

        progressDialogWaiting = new MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.progress_dialog_waiting)
                .setCancelable(false)
                .create();

        // Encuentra las vistas
        ShapeableImageView profileImage = view.findViewById(R.id.profile_image);
        TextView profileName = view.findViewById(R.id.profile_name);
        TextView profileEmail = view.findViewById(R.id.profile_email);
        TextView profileRole = view.findViewById(R.id.profile_role);
        TextView profileBirthday = view.findViewById(R.id.profile_birthday);
        LinearLayout changePasswordItem = view.findViewById(R.id.change_password_item);

        // Establece el OnClickListener para cambiar la contraseña
        changePasswordItem.setOnClickListener(v -> {
            if (getArguments() != null) {
                changePassword(getArguments().getString(ARG_TOKEN));
            }
        });

        // Aquí puedes agregar más código para cargar los datos del administrador, similar a como lo haces en AccountUserFragment.

        return view;
    }

    public void changePassword(String token) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        EditText currentPasswordInput = dialogView.findViewById(R.id.current_password_input);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        EditText confirmPasswordInput = dialogView.findViewById(R.id.confirm_password_input);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cambiar Contraseña")
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Cambiar", null); // Dejar el botón "Cambiar" sin acción por defecto

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Snackbar.make(getView(), "Todos los campos son obligatorios", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Snackbar.make(getView(), "Las contraseñas no coinciden", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                performChangePassword(token, currentPassword, newPassword, confirmPassword, dialog);
            }
        });
    }

    private void performChangePassword(String token, String currentPassword, String newPassword, String confirmPassword, AlertDialog dialog) {
        progressDialogWaiting.show();
        AuthModel.changePasswordRequest request = new AuthModel.changePasswordRequest(currentPassword, newPassword, confirmPassword);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        Call<AuthModel.changePasswordResponse> call = apiService.changePassword(request, "Bearer " + token);

        call.enqueue(new Callback<AuthModel.changePasswordResponse>() {
            @Override
            public void onResponse(Call<AuthModel.changePasswordResponse> call, Response<AuthModel.changePasswordResponse> response) {
                progressDialogWaiting.dismiss();

                if (response.isSuccessful()) {
                    AuthModel.changePasswordResponse changePasswordResponse = response.body();
                    if (changePasswordResponse != null) {
                        Snackbar.make(getView(), "Contraseña cambiada con éxito: " + changePasswordResponse.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        dialog.dismiss(); // Cierra el diálogo al cambiar la contraseña con éxito
                    } else {
                        Snackbar.make(getView(), "Respuesta vacía de la API", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(getView(), "Error al cambiar la contraseña: " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<AuthModel.changePasswordResponse> call, Throwable t) {
                progressDialogWaiting.dismiss();
                Snackbar.make(getView(), "Error en la solicitud: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}

