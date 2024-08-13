package com.utt.gymbros;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.utt.gymbros.model.OrderModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MembershipOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OrderModel.OrderUserDetailResponse> orderDetails;
    private static final int VIEW_TYPE_ORDER = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public MembershipOrderAdapter(List<OrderModel.OrderUserDetailResponse> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public int getItemViewType(int position) {
        if (orderDetails.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_ORDER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membership_order_user_empty, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membership_order_user, parent, false);
            return new OrderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OrderViewHolder) {
            OrderModel.OrderUserDetailResponse orderDetail = orderDetails.get(position);

            // Formatear la fecha de la orden
            String orderDate = orderDetail.getOrderDate();
            String formattedDate = formatDate(orderDate);

            // Mostrar detalles de la orden
            ((OrderViewHolder) holder).textOrderDate.setText("Fecha de compra: " + formattedDate);
            ((OrderViewHolder) holder).textProductName.setText(orderDetail.getOrderDetails().get(0).getProduct().getProductName());
            ((OrderViewHolder) holder).textDescription.setText(orderDetail.getOrderDetails().get(0).getProduct().getDescription());

            // Cargar imagen del producto o asignar una imagen predeterminada si el path es null
            String imageUrl = orderDetail.getOrderDetails().get(0).getProduct().getProductImagePath();
            if (imageUrl == null || imageUrl.isEmpty()) {
                ((OrderViewHolder) holder).imageProduct.setImageResource(R.drawable.membership_circle); // Imagen predeterminada
            } else {
                Picasso.get().load(imageUrl).placeholder(R.drawable.membership_circle).into(((OrderViewHolder) holder).imageProduct);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderDetails.isEmpty() ? 1 : orderDetails.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderDate;
        TextView textProductName;
        TextView textDescription;
        ShapeableImageView imageProduct;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textDescription = itemView.findViewById(R.id.text_description);
            imageProduct = itemView.findViewById(R.id.image_product);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // Método para formatear la fecha
    private String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Retorna la fecha original en caso de error
        }
    }
}
