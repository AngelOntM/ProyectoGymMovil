package com.utt.gymbros;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.squareup.picasso.Picasso;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.ProductModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<ProductModel.Product> productList;
    private final Context context;
    private final ProductsAdminFragment fragment;

    public ProductAdapter(Context context, List<ProductModel.Product> productList, ProductsAdminFragment fragment) {
        this.context = context;
        this.productList = productList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductModel.Product product = productList.get(position);
        holder.title.setText(product.getProductName());
        holder.description.setText(product.getDescription());
        holder.price.setText(product.getPrice());
        holder.stock.setText("Stock: " + product.getStock());

        // Mostrar estado activo o inactivo
        if (product.isActive()) {
            holder.activeStatus.setText("Producto Activo");
            holder.activeStatus.setTextColor(Color.GREEN);
        } else {
            holder.activeStatus.setText("Producto Inactivo");
            holder.activeStatus.setTextColor(Color.RED);
        }

        String baseUrl = "https://pasameporfavor.site/storage/";

        if (product.getProductImagePath().isEmpty()) {
            holder.image.setImageResource(R.drawable.ic_placeholder);
        } else {
            Picasso.get().load(baseUrl + product.getProductImagePath()).into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_product_details, null);
            TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
            TextView dialogDescription = dialogView.findViewById(R.id.dialog_description);
            TextView dialogPrice = dialogView.findViewById(R.id.dialog_price);
            SwitchMaterial activeSwitch = dialogView.findViewById(R.id.switch_active);

            dialogTitle.setText(product.getProductName());
            dialogDescription.setText(product.getDescription());
            dialogPrice.setText(product.getPrice());

            activeSwitch.setChecked(product.isActive());

            activeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ProductModel.Product currentProduct = productList.get(position);
                currentProduct.setActive(isChecked ? true : false);
                ApiService apiService = ApiClient.getInstance().create(ApiService.class);
                Call<Void> call = apiService.toggleProductActive(currentProduct.getId(), getToken());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Actualizar visualmente el estado activo/inactivo
                            currentProduct.setActive(isChecked ? true : false);
                            notifyItemChanged(position); // Notificar al adaptador del cambio
                            Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Producto actualizado", Snackbar.LENGTH_SHORT).show();
                        } else {
                            // Mostrar mensaje de error si falla la actualización
                            Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error al actualizar el producto", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Mostrar mensaje de error si falla la solicitud
                        Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error en la solicitud", Snackbar.LENGTH_SHORT).show();


                    }
                });
            });

            // Mostrar el diálogo
            new MaterialAlertDialogBuilder(context)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Editar", (dialog, which) -> {
                        // Código para editar el producto
                        FullScreenDialogEditProduct fullscreenDialog = new FullScreenDialogEditProduct();
                        Bundle args = new Bundle();
                        args.putSerializable("product", product); // Pasamos el producto al diálogo
                        fullscreenDialog.setArguments(args);
                        fullscreenDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "fullscreen_dialog");
                    })
                    .setNeutralButton("Eliminar", (dialog, which) -> {
                        // Código para eliminar el producto
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("Eliminar producto")
                                .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                                .setCancelable(false)
                                .setPositiveButton("Sí", (dialog1, which1) -> deleteProduct(product.getId(), position))
                                .setNegativeButton("No", (dialog1, which1) -> dialog1.dismiss())
                                .show();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void removeProduct(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }

    private void deleteProduct(int productId, int position) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.deleteProduct(productId, getToken());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removeProduct(position);
                    Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Producto eliminado", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error al eliminar el producto", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), "Error en la solicitud", Snackbar.LENGTH_SHORT).show();
            }
        });
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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, stock, activeStatus;
        ImageView image;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_product);
            description = itemView.findViewById(R.id.description_product);
            price = itemView.findViewById(R.id.price_product);
            stock = itemView.findViewById(R.id.stock_product);
            activeStatus = itemView.findViewById(R.id.active_status); // TextView para estado activo/inactivo
            image = itemView.findViewById(R.id.image_product);
        }
    }
}
