package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MembershipModel {

    public static class Membership implements Serializable {
        @SerializedName("id")
        private int id;
        @SerializedName("product_name")
        private String productName;
        @SerializedName("description")
        private String description;
        @SerializedName("price")
        private String price;
        @SerializedName("discount")
        private String discount;
        @SerializedName("active")
        private boolean active; // Cambiado de int a boolean
        @SerializedName("category_id")
        private int categoryId;
        @SerializedName("category_name")
        private String categoryName;
        @SerializedName("product_image_path")
        private String productImagePath;
        @SerializedName("duration_days")
        private int durationDays;
        @SerializedName("size")
        private int size;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;

        public Membership(int id, String productName, String description, String price, String discount, boolean active, String productImagePath, int durationDays, int size) {
            this.id = id;
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.discount = discount;
            this.active = active; // Cambiado de int a boolean
            this.productImagePath = productImagePath;
            this.durationDays = durationDays;
            this.size = size;
        }

        // Getters
        public int getId() { return id; }
        public String getProductName() { return productName == null ? "" : productName; }
        public String getDescription() { return description == null ? "" : description; }
        public String getPrice() { return price == null ? "" : price; }
        public String getDiscount() { return discount == null ? "" : discount; }
        public boolean isActive() { return active; } // Cambiado de getActive() a isActive() y tipo boolean
        public int getCategoryId() { return categoryId; }
        public String getCategoryName() { return categoryName == null ? "" : categoryName; }
        public String getProductImagePath() { return productImagePath == null ? "" : productImagePath; }
        public int getDurationDays() { return durationDays; }
        public int getSize() { return size; }
        public String getCreatedAt() { return createdAt == null ? "" : createdAt; }
        public String getUpdatedAt() { return updatedAt == null ? "" : updatedAt; }

        // Setters
        public void setActive(boolean active) { this.active = active; } // Cambiado de int a boolean
        public void setProductName(String productName) { this.productName = productName; }
        public void setDescription(String description) { this.description = description; }
        public void setPrice(String price) { this.price = price; }
        public void setDiscount(String discount) { this.discount = discount; }
        public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public void setProductImagePath(String productImagePath) { this.productImagePath = productImagePath; }
        public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
        public void setSize(int size) { this.size = size; }
    }

    public static class GetMembershipResponse {
        @SerializedName("memberships")
        private List<Membership> memberships;

        public List<Membership> getMemberships() { return memberships; }
    }

    public static class CreateMembershipRequest {
        @SerializedName("product_name")
        private String product_name;
        @SerializedName("description")
        private String description;
        @SerializedName("price")
        private String price;
        @SerializedName("discount")
        private String discount;
        @SerializedName("category_id")
        private int category_id;
        @SerializedName("duration_days")
        private int duration_days;
        @SerializedName("size")
        private int size;
        @SerializedName("active")
        private boolean active = true; // Cambiado de int a boolean
        @SerializedName("product_image_path")
        private String product_image_path = null;

        public CreateMembershipRequest(String product_name, String description, String price, String discount, int category_id, int duration_days, int size) {
            this.product_name = product_name;
            this.description = description;
            this.price = price;
            this.discount = discount;
            this.category_id = category_id;
            this.duration_days = duration_days;
            this.size = size;
        }
    }

    public static class EditMembershipRequest {
        @SerializedName("product_name")
        private String product_name;
        @SerializedName("description")
        private String description;
        @SerializedName("price")
        private String price;
        @SerializedName("discount")
        private String discount;
        @SerializedName("duration_days")
        private int duration_days;
        @SerializedName("size")
        private int size;
        @SerializedName("active")
        private boolean active; // Cambiado de int a boolean

        public EditMembershipRequest(String product_name, String description, String price, String discount, int duration_days, int size, boolean active) {
            this.product_name = product_name;
            this.description = description;
            this.price = price;
            this.discount = discount;
            this.duration_days = duration_days;
            this.size = size;
            this.active = active;
        }
    }

    public static class RedeemMembershipCodeRequest {
        @SerializedName("code")
        private String code;

        public RedeemMembershipCodeRequest(String code) {
            this.code = code;
        }
    }

    public static class RedeemMembershipCodeResponse {
        @SerializedName("message")
        private String message;

        public String getMessage() { return message; }
    }
}
