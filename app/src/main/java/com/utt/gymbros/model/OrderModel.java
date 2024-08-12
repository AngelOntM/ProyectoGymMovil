package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderModel {

    public static class Order {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("employee_id")
        private int employeeId;
        @SerializedName("order_date")
        private String orderDate;
        @SerializedName("total_amount")
        private String totalAmount;
        @SerializedName("estado")
        private String estado;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("order_details")
        private List<OrderDetail> orderDetails;

        // Getters
        public int getId() { return id; }
        public Integer getUserId() { return userId; }
        public int getEmployeeId() { return employeeId; }
        public String getOrderDate() { return orderDate; }
        public String getTotalAmount() { return totalAmount; }
        public String getEstado() { return estado; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public List<OrderDetail> getOrderDetails() { return orderDetails; }

        public void setEstado(String cancelada) {
            this.estado = cancelada;
        }
    }

    public static class OrderDetail {
        @SerializedName("id")
        private int id;
        @SerializedName("order_id")
        private int orderId;
        @SerializedName("product_id")
        private int productId;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("unit_price")
        private String unitPrice;
        @SerializedName("total_price")
        private String totalPrice;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;

        // Getters
        public int getId() { return id; }
        public int getOrderId() { return orderId; }
        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public String getUnitPrice() { return unitPrice; }
        public String getTotalPrice() { return totalPrice; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }

    public static class OrderRequest {
        @SerializedName("start_date")
        private String start_date;
        @SerializedName("end_date")
        private String end_date;

        // Getters
        public String getStartDate() { return start_date; }
        public String getEndDate() { return end_date; }

        // Setters
        public void setStartDate(String start_date) { this.start_date = start_date; }
        public void setEndDate(String end_date) { this.end_date = end_date; }

        // Constructor
        public OrderRequest(String start_date, String end_date) {
            this.start_date = start_date;
            this.end_date = end_date;
        }
    }
}
