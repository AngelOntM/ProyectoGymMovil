package com.utt.gymbros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.AuthModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnContraseñaOlvidada = findViewById(R.id.btnContraseñaOlvidada);
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etContrasena = findViewById(R.id.etContrasena);

        //Validar si ya hay un token guardado en EncryptedSharedPreference
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        String token = sharedPreferences.getString("auth_token", "");

        //Si ya hay un token guardado, redirigir a la siguiente pantalla
        if (!token.isEmpty()) {
            Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
        }

        btnLogin.setOnClickListener(v -> {
            login(etCorreo.getText().toString(), etContrasena.getText().toString());
        });

        btnContraseñaOlvidada.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setTitle("Recuperar contraseña");
            builder.setMessage("Ingresa tu correo electrónico para recuperar tu contraseña");
            EditText input = new EditText(MainActivity.this);
            builder.setView(input);
            //evitar salir del dialogo al presionar el botón
            builder.setCancelable(false);
            builder.setPositiveButton("Enviar", (dialog, which) -> {
                String email = input.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa un correo válido", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Enviar correo con la contraseña
                Toast.makeText(MainActivity.this, "Correo enviado", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> {
                dialog.cancel();
            });
            builder.show();
        });
    }

    private void login(String email, String password)
    {
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setEnabled(false);

        //Validar que los campos no estén vacíos
        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Por favor, llena todos los campos", Snackbar.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            return;
        }

        //Validar que el correo tenga un formato válido
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(findViewById(android.R.id.content), "Por favor, ingresa un correo válido", Snackbar.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            return;
        }

        AuthModel.LoginRequest loginRequest = new AuthModel.LoginRequest(email, password);

        Retrofit retrofit = ApiClient.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        apiService.login(loginRequest).enqueue(new Callback<AuthModel.LoginResponse>() {
            @Override
            public void onResponse(Call<AuthModel.LoginResponse> call, retrofit2.Response<AuthModel.LoginResponse> response) {
                if (response.isSuccessful()) {
                    AuthModel.LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    String message = loginResponse.getMessage();
                    String name = loginResponse.getUser().getName();

                    if(message.equals("Usuario no autorizado")) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        return;
                    }
                    if (token.isEmpty()) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        return;
                    }
                    try {
                        SharedPreferences sharedPrefs = EncryptedSharedPreferences.create(
                                "userData",                                    // Nombre del archivo de preferencias
                                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),  // Clave de cifrado
                                getApplicationContext(),
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("auth_token", token);
                        editor.apply();
                    } catch (GeneralSecurityException | IOException e) {
                        // Manejar errores de seguridad o de E/S
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error al guardar el token", Toast.LENGTH_SHORT).show();
                    }

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setTitle("Bienvenido");
                    builder.setMessage("Hola, " + name);
                    //evitar salir del dialogo al presionar el botón
                    builder.setCancelable(false);
                    builder.show();

                    //cerrar dialogo después de 2 segundos
                    new android.os.Handler().postDelayed(
                            () -> {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            },
                            2000
                    );

                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), "Verifica tus credenciales", Snackbar.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<AuthModel.LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
                btnLogin.setEnabled(true);
            }
        });
    }

}