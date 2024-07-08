package com.utt.gymbros;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.OrderModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private final List<OrderModel.Order> orderList;

    public OrderAdapter(Context context, List<OrderModel.Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel.Order order = orderList.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String getToken() {
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context.getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return "Bearer " + sharedPreferences.getString("auth_token", "");
    }

    private void cancelOrder(int orderId, int position) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.cancelOrder(orderId, getToken());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    orderList.get(position).setEstado("Cancelada");
                    notifyItemChanged(position);
                    Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Orden cancelada", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error al cancelar la orden", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error en la solicitud", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView orderIdTextView;
        private final TextView orderDateTextView;
        private final TextView orderStatusTextView;
        private final TextView orderTotalTextView;
        private final ImageButton btnCancelOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.text_order_id);
            orderDateTextView = itemView.findViewById(R.id.text_order_date);
            orderStatusTextView = itemView.findViewById(R.id.text_order_status);
            orderTotalTextView = itemView.findViewById(R.id.text_order_total);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);

            // Configurar clic listener para el botón Cancelar Orden
            btnCancelOrder.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(itemView.getContext())
                        .setTitle("Cancelar Orden")
                        .setMessage("¿Estás seguro de que deseas cancelar esta orden?")
                        .setCancelable(false)
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Lógica para cancelar la orden aquí
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                OrderModel.Order order = orderList.get(position);
                                cancelOrder(order.getId(), position);
                            }
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }

        public void bind(OrderModel.Order order, int position) {
            orderIdTextView.setText("Folio: " + order.getId());
            orderDateTextView.setText("Fecha de Orden: " + order.getOrderDate());
            orderStatusTextView.setText("Estado: " + order.getEstado());
            orderTotalTextView.setText("Total $" + order.getTotalAmount());

            // Cambiar el color de fondo y texto según el estado de la orden
            switch (order.getEstado()) {
                case "Pagada":
                    itemView.setBackgroundColor(Color.parseColor("#b8f397")); // Verde seleccionado
                    setTextViewColors(Color.BLACK); // Textos en negro para contraste
                    btnCancelOrder.setVisibility(View.GONE); // Ocultar botón de cancelar orden
                    break;
                case "Cancelada":
                    itemView.setBackgroundColor(Color.parseColor("#ef4a72")); // Rojo seleccionado
                    setTextViewColors(Color.WHITE); // Textos en blanco para contraste
                    btnCancelOrder.setVisibility(View.GONE); // Ocultar botón de cancelar orden
                    break;
                case "Proceso":
                    itemView.setBackgroundColor(Color.parseColor("#394457")); // Gris seleccionado
                    setTextViewColors(Color.WHITE); // Textos en blanco para contraste
                    btnCancelOrder.setVisibility(View.VISIBLE); // Mostrar botón de cancelar orden
                    break;
                default:
                    itemView.setBackgroundColor(Color.WHITE); // Color por defecto
                    setTextViewColors(Color.BLACK); // Textos en negro para contraste
                    btnCancelOrder.setVisibility(View.GONE); // Ocultar botón de cancelar orden
                    break;
            }
        }

        private void setTextViewColors(int color) {
            orderIdTextView.setTextColor(color);
            orderDateTextView.setTextColor(color);
            orderStatusTextView.setTextColor(color);
            orderTotalTextView.setTextColor(color);
        }
    }
}
