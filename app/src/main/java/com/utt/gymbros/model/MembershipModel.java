package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MembershipModel {

    public static class Membership {
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
    }

    public static class GetMembershipResponse {
        @SerializedName("memberships")
        private List<Membership> memberships;

        public List<Membership> getMemberships() { return memberships; }
    }
}
