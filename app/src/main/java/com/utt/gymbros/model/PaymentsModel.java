package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;

public class PaymentsModel {

    public static class BuyMembershipRequest {
        @SerializedName("user_id")
        private int userId;
        @SerializedName("product_id")
        private int productId;

        public BuyMembershipRequest(int userId, int productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }
    }

    public static class BuyMembershipResponse {
        @SerializedName("user_id")
        private int userId;
        @SerializedName("order_date")
        private String orderDate;
        @SerializedName("total_amount")
        private String totalAmount;
        @SerializedName("estado")
        private String estado;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("id")
        private int id;

        // Getters and setters
        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class CreatePaymentIntentRequest {
        @SerializedName("description")
        private String description;
        @SerializedName("receipt_email")
        private String receiptEmail;

        public CreatePaymentIntentRequest(String description, String receiptEmail) {
            this.description = description;
            this.receiptEmail = receiptEmail;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getReceiptEmail() {
            return receiptEmail;
        }

        public void setReceiptEmail(String receiptEmail) {
            this.receiptEmail = receiptEmail;
        }
    }

    public static class CreatePaymentIntentResponse {
        @SerializedName("payment_intent_id")
        private String paymentIntentId;
        @SerializedName("client_secret")
        private String clientSecret;

        public String getPaymentIntentId() {
            return paymentIntentId;
        }

        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

    public static class cancelPaymentIntentRequest {
        @SerializedName("payment_intent_id")
        private String paymentIntentId;

        public cancelPaymentIntentRequest(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }

        public String getPaymentIntentId() {
            return paymentIntentId;
        }

        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }
    }

    public static class confirmPaymentIntentRequest {
        @SerializedName("payment_intent_id")
        private String paymentIntentId;

        public confirmPaymentIntentRequest(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }

        public String getPaymentIntentId() {
            return paymentIntentId;
        }

        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }
    }

    public static class storePaymentRequest{
        @SerializedName("payment_intent_id")
        private String paymentIntentId;


        public storePaymentRequest(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;

        }

        public String getPaymentIntentId() {
            return paymentIntentId;
        }

        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }

    }
}
