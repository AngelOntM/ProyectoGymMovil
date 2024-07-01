package com.utt.gymbros;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;
import com.utt.gymbros.model.MembershipModel;

import java.util.List;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MembershipViewHolder> {

    private final List<MembershipModel.Membership> membershipList;
    private Context context;

    public MembershipAdapter(Context context, List<MembershipModel.Membership> membershipList) {
        this.context = context;
        this.membershipList = membershipList;
    }

    @NonNull
    @Override
    public MembershipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membership, parent, false);
        return new MembershipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipViewHolder holder, int position) {
        MembershipModel.Membership membership = membershipList.get(position);
        holder.title.setText(membership.getProductName());
        holder.description.setText(membership.getDescription());
        holder.price.setText(membership.getPrice());

        if (membership.getProductImagePath().isEmpty()) {
            holder.image.setImageResource(R.drawable.ic_placeholder); // Usar un emoji o un recurso drawable de placeholder
        } else {
            Picasso.get().load(membership.getProductImagePath()).into(holder.image); // Usar Picasso o Glide para cargar imágenes
        }
        holder.itemView.setOnClickListener(v -> {
            // Build the details dialog
            new MaterialAlertDialogBuilder(context)
                    .setTitle(membership.getProductName())
                    .setMessage(membership.getDescription() + "\n\nPrecio: " + membership.getPrice())
                    .setCancelable(false)
                    .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Editar", (dialog, which) -> {
                        // Mostrar el FullscreenDialog
                        FullscreenDialogEditMembership fullscreenDialog = new FullscreenDialogEditMembership();
                        Bundle args = new Bundle();
                        fullscreenDialog.setArguments(args);
                        fullscreenDialog.show(
                                ((AppCompatActivity) context).getSupportFragmentManager(), // Asegurarse de tener una referencia a AppCompatActivity
                                "fullscreen_dialog"
                        );
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return membershipList.size();
    }

    public static class MembershipViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price;
        ImageView image;

        public MembershipViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_membership);
            description = itemView.findViewById(R.id.description_membership);
            price = itemView.findViewById(R.id.price_membership);
            image = itemView.findViewById(R.id.image_membership);
        }
    }
}

