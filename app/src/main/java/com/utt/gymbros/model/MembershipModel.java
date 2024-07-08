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
        private int active;
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

        public Membership(int id, String productName, String description, String price, String discount, int active, String productImagePath, int durationDays, int size) {
            this.id = id;
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.discount = discount;
            this.active = active;
            this.productImagePath = null;
            this.durationDays = durationDays;
            this.size = size;
        }

        // Getters
        public int getId() { return id; }
        public String getProductName() { return productName == null ? "" : productName; }
        public String getDescription() { return description == null ? "" : description; }
        public String getPrice() { return price == null ? "" : price; }
        public String getDiscount() { return discount == null ? "" : discount; }
        public int getActive() { return active; }
        public int getCategoryId() { return categoryId; }
        public String getCategoryName() { return categoryName == null ? "" : categoryName; }
        public String getProductImagePath() { return productImagePath == null ? "" : productImagePath; }
        public int getDurationDays() { return durationDays; }
        public int getSize() { return size; }
        public String getCreatedAt() { return createdAt == null ? "" : createdAt; }
        public String getUpdatedAt() { return updatedAt == null ? "" : updatedAt; }

        // Setters
        public void setActive(int i) {
            active = i;
        }
        public void setProductName(String s) {
            productName = s;
        }
        public void setDescription(String s) {
            description = s;
        }
        public void setPrice(String s) {
            price = s;
        }
        public void setDiscount(String s) {
            discount = s;
        }
        public void setCategoryId(int i) {
            categoryId = i;
        }
        public void setCategoryName(String s) {
            categoryName = s;
        }
        public void setProductImagePath(String s) {
            productImagePath = s;
        }
        public void setDurationDays(int i) {
            durationDays = i;
        }
        public void setSize(int i) {
            size = i;
        }



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
        private int active = 1;
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
}
