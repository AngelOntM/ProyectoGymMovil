package com.utt.gymbros;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.squareup.picasso.Picasso;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.ClienteModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ClienteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ClienteModel.Cliente> clienteList;
    private static final int VIEW_TYPE_CLIENTE = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public ClienteAdapter(Context context, List<ClienteModel.Cliente> clienteList) {
        this.context = context;
        this.clienteList = clienteList;
    }

    @Override
    public int getItemViewType(int position) {
        if (clienteList.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_CLIENTE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit_empty, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false);
            return new ClienteViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ClienteViewHolder) {
            ClienteModel.Cliente cliente = clienteList.get(position);
            ((ClienteViewHolder) holder).userNameTextView.setText(cliente.getName());
            ((ClienteViewHolder) holder).userEmailTextView.setText(cliente.getEmail());
            ((ClienteViewHolder) holder).userPhoneTextView.setText(cliente.getPhoneNumber());
            ((ClienteViewHolder) holder).userAddressTextView.setText(cliente.getAddress());

            // Imprimir el nombre y su id en la consola
            System.out.println("Nombre: " + cliente.getName() + " ID: " + cliente.getId());

            // Hacer peticion a la API para obtener la imagen del cliente
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<ResponseBody> call = apiService.getUserImage(cliente.getId(), getToken());
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            // Crear archivo temporal
                            File tempFile = File.createTempFile("temp_image", ".jpg", holder.itemView.getContext().getCacheDir());
                            FileOutputStream fos = new FileOutputStream(tempFile);
                            fos.write(response.body().bytes());
                            fos.flush();
                            fos.close();

                            // Cargar imagen desde el archivo temporal usando Picasso
                            Picasso.get()
                                    .load(tempFile)
                                    .placeholder(R.drawable.profile_placeholder)
                                    .into(((ClienteViewHolder) holder).userProfileImageView);
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
        }
    }


    @Override
    public int getItemCount() {
        return clienteList.isEmpty() ? 1 : clienteList.size();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView;
        TextView userEmailTextView;
        TextView userPhoneTextView;
        TextView userAddressTextView;
        ImageView userProfileImageView;

        ClienteViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name);
            userEmailTextView = itemView.findViewById(R.id.user_email);
            userPhoneTextView = itemView.findViewById(R.id.user_phone);
            userAddressTextView = itemView.findViewById(R.id.user_address);
            userProfileImageView = itemView.findViewById(R.id.user_profile_image);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
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
}
