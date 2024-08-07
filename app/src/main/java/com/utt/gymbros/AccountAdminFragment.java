package com.utt.gymbros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;

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

        // Encuentra el LinearLayout
        LinearLayout changePasswordItem = view.findViewById(R.id.change_password_item);
        LinearLayout logoutItem = view.findViewById(R.id.logout_item);

        // Establece el OnClickListener
        changePasswordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Token: " + getArguments().getString(ARG_TOKEN), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        logoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el MaterialAlertDialog para confirmar el logout
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                        .setCancelable(false)
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                            progressDialogLogout = new MaterialAlertDialogBuilder(requireContext())
                                    .setView(R.layout.progress_dialog_logout)
                                    .setCancelable(false)
                                    .create();

                            progressDialogLogout.show();

                            // Realizar petición de logout
                            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
                            Call<Void> call = apiService.logout("Bearer " + getArguments().getString(ARG_TOKEN));
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                    if (response.isSuccessful()) {
                                        // Borrar datos almacenados en SharedPreferences
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

                                        // Redirigir a la pantalla de login
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                        progressDialogLogout.dismiss();
                                    } else {
                                        Snackbar.make(v, "Error al cerrar sesión", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    progressDialogLogout.dismiss();
                                    Snackbar.make(v, "Error en la solicitud", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        })
                        .show();
            }
        });

        return view;
    }

    public static AccountAdminFragment newInstance(String token) {
        AccountAdminFragment fragment = new AccountAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}
