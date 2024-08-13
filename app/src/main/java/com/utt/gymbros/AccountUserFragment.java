package com.utt.gymbros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.AuthModel;
import com.utt.gymbros.model.UserModel;
import com.utt.gymbros.model.MembershipModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountUserFragment extends Fragment {
    public static final String ARG_TOKEN = "token";
    androidx.appcompat.app.AlertDialog progressDialogLogout;
    androidx.appcompat.app.AlertDialog progressDialogWaiting;

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
        View view = inflater.inflate(R.layout.fragment_account_user, container, false);

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
        TextView profileMembershipStatus = view.findViewById(R.id.membership_status);
        LinearLayout changePasswordItem = view.findViewById(R.id.change_password_item);
        LinearLayout redeemCodeItem = view.findViewById(R.id.redeem_code_item);
        LinearLayout logoutItem = view.findViewById(R.id.logout_item);

        // Establece el OnClickListener para los elementos
        changePasswordItem.setOnClickListener(v -> {
            if (getArguments() != null) {
                changePassword(getArguments().getString(ARG_TOKEN));
            }
        });

        redeemCodeItem.setOnClickListener(v -> showRedeemCodeDialog());

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

        // Fetch user data
        if (getArguments() != null) {
            String token = getArguments().getString(ARG_TOKEN);

            fetchUserData(token, profileImage, profileName, profileEmail, profileRole, profileBirthday, profileMembershipStatus);
        }

        return view;
    }

    private void showRedeemCodeDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_redeem_code, null);

        EditText codeInput = dialogView.findViewById(R.id.code_input);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Canjear Código")
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Canjear", null); // Dejar el botón "Canjear" sin acción por defecto

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String code = codeInput.getText().toString();
            if (!code.isEmpty()) {
                redeemCode(code, dialog); // Pasa el diálogo a la función redeemCode
            } else {
                Snackbar.make(getView(), "El código no puede estar vacío", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void redeemCode(String code, AlertDialog dialog) {
        progressDialogWaiting.show();
        MembershipModel.RedeemMembershipCodeRequest request = new MembershipModel.RedeemMembershipCodeRequest(code);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);

        Call<MembershipModel.RedeemMembershipCodeResponse> call = apiService.redeemMembership(
                request,
                token,
                "application/json",
                "application/json",
                "XMLHttpRequest"
        );

        call.enqueue(new Callback<MembershipModel.RedeemMembershipCodeResponse>() {
            @Override
            public void onResponse(Call<MembershipModel.RedeemMembershipCodeResponse> call, Response<MembershipModel.RedeemMembershipCodeResponse> response) {
                progressDialogWaiting.dismiss();

                if (response.isSuccessful()) {
                    MembershipModel.RedeemMembershipCodeResponse redeemMembershipCodeResponse = response.body();
                    if (redeemMembershipCodeResponse != null) {
                        String message = redeemMembershipCodeResponse.getMessage();
                        Snackbar.make(getView(), "Se canjeó con éxito: " + message, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        dialog.dismiss(); // Cierra el diálogo al canjear con éxito
                    } else {
                        Snackbar.make(getView(), "Respuesta vacía de la API", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    switch (response.code()) {
                        case 404:
                            Snackbar.make(getView(), "El código de membresía ya fue utilizado", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            break;
                        case 400:
                            Snackbar.make(getView(), "El usuario ya tiene una membresía activa", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            break;
                        case 422:
                            Snackbar.make(getView(), "El código no existe", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            break;
                        default:
                            Snackbar.make(getView(), "Error al canjear el código: " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<MembershipModel.RedeemMembershipCodeResponse> call, Throwable t) {
                progressDialogWaiting.dismiss();
                Snackbar.make(getView(), "Error en la solicitud: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                t.printStackTrace();
            }
        });
    }

    public void fetchUserData(String token, ShapeableImageView profileImage, TextView profileName, TextView profileEmail, TextView profileRole, TextView profileBirthday, TextView profileMembershipStatus) {
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
                        UserModel.ActiveMembership activeMembership = userResponse.getActiveMembership();

                        // Mostrar datos del usuario
                        profileName.setText(user.getName());
                        profileEmail.setText(user.getEmail());
                        profileRole.setText("Usuario");
                        profileBirthday.setText("Fecha de Nacimiento: " + user.getDateOfBirth());

                        if (activeMembership != null) {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDate startDate = LocalDateTime.parse(activeMembership.getStartDate(), formatter).toLocalDate();
                                LocalDate endDate = LocalDateTime.parse(activeMembership.getEndDate(), formatter).toLocalDate();

                                long daysRemaining = ChronoUnit.DAYS.between(startDate, endDate);

                                profileMembershipStatus.setTextColor(getResources().getColor(R.color.white_light));
                                profileMembershipStatus.setText("Días Restantes: ");
                                profileMembershipStatus.append(Html.fromHtml("<font color='#80FF80'>" + daysRemaining + "</font>"));
                            } catch (DateTimeParseException e) {
                                e.printStackTrace();
                                Snackbar.make(getView(), "Error al parsear las fechas de la membresía", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } else {
                            profileMembershipStatus.setTextColor(Color.parseColor("#e24c5e"));
                            profileMembershipStatus.setText("Estado de Membresía: No tiene membresía activa");
                        }

                        // Mostrar la imagen de perfil si existe
                        if (user.getFaceImagePath() != null) {
                            Picasso.get().load(user.getFaceImagePath()).into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.profile_placeholder);
                        }
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

    public static AccountUserFragment newInstance(String token) {
        AccountUserFragment fragment = new AccountUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
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
