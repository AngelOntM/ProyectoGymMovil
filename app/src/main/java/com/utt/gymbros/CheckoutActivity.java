package com.utt.gymbros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.PaymentsModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private androidx.appcompat.app.AlertDialog progressDialog;

    private final String STRIPE_PUBLISHABLE_KEY = "pk_test_51PiLliAvmYkVRZQNHFgKZm3BYNAodJuzqZ8ApCZScOV2L5wPp2761HfhogfqEtMmKwMCTO6tR7wrog9UMwML2KAT000HWEGzKG";
    private String AUTH_TOKEN;
    private String paymentIntentId;
    private Integer orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkout_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getAuthToken();

        // Obtener los datos que se pasaron desde el fragmento
        orderId = getIntent().getIntExtra("orderID", 0);
        String clientSecret = getIntent().getStringExtra("client_secret");
        paymentIntentId = getIntent().getStringExtra("payment_intent_id");

        Log.i("CheckoutActivity", "orderId: " + orderId);
        Log.i("CheckoutActivity", "clientSecret: " + clientSecret);
        Log.i("CheckoutActivity", "paymentIntentId: " + paymentIntentId);

        // Configurar PaymentConfiguration con la clave pública
        PaymentConfiguration.init(getApplicationContext(), STRIPE_PUBLISHABLE_KEY);

        // Inicializar PaymentSheet
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Presentar la hoja de pago
        presentPaymentSheet(clientSecret);
    }

    private void presentPaymentSheet(String clientSecret) {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Gymbros")
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            showProgressDialog();
            PaymentsModel.cancelPaymentIntentRequest request = new PaymentsModel.cancelPaymentIntentRequest(paymentIntentId);
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            apiService.cancelPaymentIntent(request, "Bearer " + AUTH_TOKEN).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    hideProgressDialog();
                    if (response.isSuccessful()) {
                        Log.d("CheckoutActivity", "Payment canceled successfully");
                        finish();
                    } else {
                        Log.e("CheckoutActivity", "Error canceling payment: " + response.errorBody());
                        //Regresar en la pantalla anterior un snackbar con el error
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    hideProgressDialog();
                    Log.e("CheckoutActivity", "Error onFailure canceling payment: " + t.getMessage());
                    finish();
                }
            });
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e("CheckoutActivity", "Payment failed: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Log.d("CheckoutActivity", "Payment completed successfully");
            showProgressDialog();
            PaymentsModel.confirmPaymentIntentRequest request = new PaymentsModel.confirmPaymentIntentRequest(paymentIntentId);
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            apiService.confirmStripePayment(request, "Bearer " + AUTH_TOKEN).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        apiService.stripePayment(orderId, new PaymentsModel.storePaymentRequest(paymentIntentId), "Bearer " + AUTH_TOKEN).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                hideProgressDialog();
                                if (response.isSuccessful()) {
                                    Log.d("CheckoutActivity", "Payment confirmed successfully");
                                    finish();
                                } else {
                                    Log.e("CheckoutActivity", "Error storing payment: " + response.errorBody());
                                    finish();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                hideProgressDialog();
                                Log.e("CheckoutActivity", "Error onFailure storing payment: " + t.getMessage());
                                finish();
                            }
                        });
                    } else {
                        Log.e("CheckoutActivity", "Error confirming payment: " + response.errorBody());
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    hideProgressDialog();
                    Log.e("CheckoutActivity", "Error onFailure confirming payment: " + t.getMessage());
                    finish();
                }
            });
        }
    }

    public void showProgressDialog() {
        progressDialog = new MaterialAlertDialogBuilder(this)
                .setView(R.layout.progress_dialog_waiting)
                .setCancelable(false)
                .create();
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void getAuthToken() {
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        AUTH_TOKEN = sharedPreferences.getString("auth_token", "");
    }
}
