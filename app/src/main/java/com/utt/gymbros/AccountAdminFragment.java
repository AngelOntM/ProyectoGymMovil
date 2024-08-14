package com.utt.gymbros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import okhttp3.ResponseBody;
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

        progressDialogLogout = new MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.progress_dialog_logout)
                .setCancelable(false)
                .create();

        // Encuentra las vistas
        ShapeableImageView profileImage = view.findViewById(R.id.profile_image);
        TextView profileName = view.findViewById(R.id.profile_name);
        TextView profileEmail = view.findViewById(R.id.profile_email);
        TextView profileRole = view.findViewById(R.id.profile_role);
        TextView profileBirthday = view.findViewById(R.id.profile_birthday);
        LinearLayout changePasswordItem = view.findViewById(R.id.change_password_item);
        LinearLayout logoutItem = view.findViewById(R.id.logout_item);

        // Establece el OnClickListener para cambiar la contraseña
        changePasswordItem.setOnClickListener(v -> {
            if (getArguments() != null) {
                changePassword(getArguments().getString(ARG_TOKEN));
            }
        });

        logoutItem.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                    .setCancelable(false)
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                        progressDialogLogout.show();

                        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
                        Call<Void> call = apiService.logout("Bearer " + getArguments().getString(ARG_TOKEN));
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                progressDialogLogout.dismiss();
                                if (response.isSuccessful()) {
                                    try {
                                        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                                                "userData",
                                                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                                                requireContext(),
                                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                                        );
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.apply();
                                    } catch (GeneralSecurityException | IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Snackbar.make(v, "Error al cerrar sesión: " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                progressDialogLogout.dismiss();
                                Snackbar.make(v, "Error en la solicitud: " + t.getMessage(), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    })
                    .show();
        });


        if (getArguments() != null) {
            String token = getArguments().getString(ARG_TOKEN);
            fetchUserData(token, profileImage, profileName, profileEmail, profileRole, profileBirthday);
        }


        return view;
    }

    public void fetchUserData(String token, ShapeableImageView profileImage, TextView profileName, TextView profileEmail, TextView profileRole, TextView profileBirthday) {
        progressDialogWaiting.show();
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<UserModel.UserResponse> call = apiService.getUser("Bearer " + token);

        call.enqueue(new Callback<UserModel.UserResponse>() {
            @Override
            public void onResponse(Call<UserModel.UserResponse> call, Response<UserModel.UserResponse> response) {
                progressDialogWaiting.dismiss();
                if (response.isSuccessful()) {
                    UserModel.UserResponse userResponse = response.body();
                    if (userResponse != null) {
                        UserModel.User user = userResponse.getUser();

                        // Mostrar datos del usuario
                        profileName.setText(user.getName());
                        profileEmail.setText(user.getEmail());
                        profileRole.setText("Administrador");
                        profileBirthday.setText("Fecha de Nacimiento: " + user.getDateOfBirth());


                        // Hacer peticion a la API para obtener la imagen del cliente
                        Call<ResponseBody> callImage = apiService.getUserImage(user.getId(), "Bearer " + token);
                        callImage.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        // Crear archivo temporal
                                        File tempFile = File.createTempFile("temp_image", ".jpg", requireContext().getCacheDir());
                                        FileOutputStream fos = new FileOutputStream(tempFile);
                                        fos.write(response.body().bytes());
                                        fos.flush();
                                        fos.close();

                                        // Cargar imagen desde el archivo temporal usando Picasso
                                        Picasso.get()
                                                .load(tempFile)
                                                .placeholder(R.drawable.profile_placeholder)
                                                .into(profileImage);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    } else {
                        Snackbar.make(getView(), "Respuesta vacía de la API", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(getView(), "Error al obtener los datos del usuario: " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel.UserResponse> call, Throwable t) {
                progressDialogWaiting.dismiss();
                // Registra el error detallado
                Snackbar.make(getView(), "Error en la solicitud: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
