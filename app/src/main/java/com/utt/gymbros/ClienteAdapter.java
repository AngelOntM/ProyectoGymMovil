package com.utt.gymbros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.utt.gymbros.model.ClienteModel;

import java.util.List;

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

            // Cargar imagen de perfil usando Picasso
            Picasso.get()
                    .load(cliente.getFaceImagePath())
                    .placeholder(R.drawable.avatar_placerholder)
                    .into(((ClienteViewHolder) holder).userProfileImageView);
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
}
