package com.utt.gymbros;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.AuthModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnContrasenaOlvidada = findViewById(R.id.btnContrasenaOlvidada);
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
        String role = sharedPreferences.getString("role", "");

        //Si ya hay un token guardado, redirigir a la pantalla correspondiente
        if (!token.isEmpty() && role.equals("Admin")) {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        } else if (!token.isEmpty() && role.equals("Cliente")) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }


        btnLogin.setOnClickListener(v -> {
            login(etCorreo.getText().toString(), etContrasena.getText().toString());
        });

        btnContrasenaOlvidada.setOnClickListener(v -> {
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

    private void login(String email, String password) {
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setEnabled(false);

        progressDialog = new MaterialAlertDialogBuilder(MainActivity.this)
                .setView(R.layout.progress_dialog)
                .setCancelable(false)
                .create();

        progressDialog.show();

        // Validación de campos vacíos
        if (email.isEmpty() || password.isEmpty()) {
            showSnackbar("Por favor, llena todos los campos");
            btnLogin.setEnabled(true);
            runOnUiThread(progressDialog::dismiss);
            return;
        }

        // Validación de formato de correo
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showSnackbar("Por favor, ingresa un correo válido");
            btnLogin.setEnabled(true);
            runOnUiThread(progressDialog::dismiss);
            return;
        }

        AuthModel.LoginRequest loginRequest = new AuthModel.LoginRequest(email, password);

        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        apiService.login(loginRequest).enqueue(new Callback<AuthModel.LoginResponse>() {
            @Override
            public void onResponse(Call<AuthModel.LoginResponse> call, retrofit2.Response<AuthModel.LoginResponse> response) {
                if (response.isSuccessful()) {
                    AuthModel.LoginResponse loginResponse = response.body();
                    String role = String.valueOf(loginResponse.getUser().getRol().getRole());
                    String token = loginResponse.getToken();
                    String message = loginResponse.getMessage();
                    String email = loginResponse.getUser().getEmail();

                    if (role.equals("Admin")) {
                        showVerificationCodeDialog(email);
                        btnLogin.setEnabled(true);
                    } else if (message.equals("Usuario no autorizado") || token.isEmpty()) {
                        showSnackbar("Usuario no autorizado");
                        btnLogin.setEnabled(true);
                        runOnUiThread(progressDialog::dismiss);
                    } else {
                        saveToken(token);
                        saveDataUser(loginResponse.getUser().getName(), email, role);
                        btnLogin.setEnabled(true);
                        runOnUiThread(progressDialog::dismiss);
                    }
                } else {
                    showSnackbar("Verifica tus credenciales");
                    btnLogin.setEnabled(true);
                    runOnUiThread(progressDialog::dismiss);
                }
            }

            @Override
            public void onFailure(Call<AuthModel.LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.err.println(t.getMessage()); // Usar System.err para errores
                btnLogin.setEnabled(true);
                runOnUiThread(progressDialog::dismiss);
            }
        });
    }
    private void showVerificationCodeDialog(String email) {
        runOnUiThread(progressDialog::dismiss);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setTitle("Código de verificación");
        builder.setMessage("Ingresa el código de verificación enviado a tu correo electrónico");

        // Diseño del EditText para el código
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText input = new EditText(MainActivity.this);
        input.setHint("Código de verificación");
        input.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        layout.addView(input);

        // TextView para mostrar el tiempo restante
        TextView timeRemainingTextView = new TextView(MainActivity.this);
        timeRemainingTextView.setText("Tiempo restante: 10:00"); // Tiempo inicial
        layout.addView(timeRemainingTextView);

        builder.setView(layout);
        builder.setCancelable(false);

        // Contador de tiempo
        final long[] timeRemaining = {10 * 60 * 1000}; // 10 minutos en milisegundos
        final Handler handler = new Handler();

        AtomicReference<AlertDialog> dialogRef = new AtomicReference<>();

        final Runnable runnable = new Runnable() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void run() {
                if (timeRemaining[0] > 0) {
                    timeRemaining[0] -= 1000; // Restar 1 segundo
                    long minutes = timeRemaining[0] / (60 * 1000);
                    long seconds = (timeRemaining[0] % (60 * 1000)) / 1000;
                    timeRemainingTextView.setText(String.format("Tiempo restante: %02d:%02d", minutes, seconds));
                    handler.postDelayed(this, 1000); // Volver a ejecutar en 1 segundo
                } else {
                    timeRemainingTextView.setText("El código ha caducado");
                    // Desactivar el botón "Verificar"
                    AlertDialog dialog = dialogRef.get();
                    if (dialog != null) {
                        Button verifyButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        if (verifyButton != null) {
                            verifyButton.setEnabled(false);
                        }
                    }
                }
            }
        };
        handler.postDelayed(runnable, 1000); // Iniciar el contador

        builder.setPositiveButton("Verificar", null); // Proveer el botón "Verificar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            handler.removeCallbacks(runnable); // Detener el contador al cancelar
            dialog.cancel();
        });

        // Crear el diálogo
        AlertDialog dialog = builder.create();
        dialogRef.set(dialog);
        dialog.show();

        // Configurar el botón "Verificar" después de mostrar el diálogo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String verificationCode = input.getText().toString();

            //Si el codigo de verificacion esta vacio mostrar un mensaje pero no cerrar el dialogo
            if (verificationCode.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Por favor, ingresa el código de verificación", Snackbar.LENGTH_SHORT).show();
                runOnUiThread(progressDialog::dismiss);
                return;
            }

            //Si el codigo de verificacion no tiene 6 digitos mostrar un mensaje pero no cerrar el dialogo
            if (verificationCode.length() != 6) {
                Snackbar.make(findViewById(android.R.id.content), "El código de verificación debe tener 6 dígitos", Snackbar.LENGTH_SHORT).show();
                runOnUiThread(progressDialog::dismiss);
                return;
            }

            //Peticion a la API para verificar el codigo
            AuthModel.VerifyCodeRequest verifyCodeRequest = new AuthModel.VerifyCodeRequest(email, Integer.parseInt(verificationCode));
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            apiService.verifyCode(verifyCodeRequest).enqueue(new Callback<AuthModel.VerifyCodeResponse>() {
                @Override
                public void onResponse(Call<AuthModel.VerifyCodeResponse> call, Response<AuthModel.VerifyCodeResponse> response) {
                    if (response.isSuccessful()) {
                        //parar el contador al verificar el codigo
                        handler.removeCallbacks(runnable);
                        AuthModel.VerifyCodeResponse verifyCodeResponse = response.body();
                        String message = verifyCodeResponse.getMessage();
                        String token = verifyCodeResponse.getToken();
                        String user = verifyCodeResponse.getUser().getName();
                        if (message.equals("User logged in successfully")) {
                            saveToken(token);
                            saveDataUser(user, email, "Admin");
                            dialog.dismiss();
                        } else {
                            showSnackbar("Codigo incorrecto");
                        }
                    } else {
                        showSnackbar("Codigo incorrecto");
                    }
                }

                @Override
                public void onFailure(Call<AuthModel.VerifyCodeResponse> call, Throwable t) {
                    Snackbar.make(findViewById(android.R.id.content), "Error al verificar el código", Snackbar.LENGTH_SHORT).show();
                    System.err.println(t.getMessage());
                }
            });
        });
    }



    // Métodos auxiliares para mejorar la organización
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
    private void saveToken(String token) {
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

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

    private void saveDataUser(String name, String email, String role){
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

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.apply();

        if (role.equals("Admin")) {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}