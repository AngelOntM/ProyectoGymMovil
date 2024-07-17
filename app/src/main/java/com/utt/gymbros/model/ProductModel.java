package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProductModel {

    public static class Product implements Serializable {
        @SerializedName("id")
        private int id;
        @SerializedName("product_name")
        private String productName;
        @SerializedName("description")
        private String description;
        @SerializedName("price")
        private String price;
        @SerializedName("stock")
        private int stock;
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
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;

        public Product(int id, String productName, String description, String price, int stock, String discount, int active, int categoryId, String categoryName, String productImagePath, String createdAt, String updatedAt) {
            this.id = id;
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.stock = stock;
            this.discount = discount;
            this.active = active;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.productImagePath = productImagePath;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        // Getters and setters
        public int getId() {
            return id;
        }

        public String getProductName() {
            return productName == null ? "" : productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getDescription() {
            return description == null ? "" : description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPrice() {
            return price == null ? "" : price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public String getDiscount() {
            return discount == null ? "" : discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName == null ? "" : categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getProductImagePath() {
            return productImagePath == null ? "" : productImagePath;
        }

        public void setProductImagePath(String productImagePath) {
            this.productImagePath = productImagePath;
        }

        public String getCreatedAt() {
            return createdAt == null ? "" : createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt == null ? "" : updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class GetProductResponse {
        @SerializedName("products")
        private List<Product> products;

        public List<Product> getProducts() {
            return products;
        }
    }

    public static class CreateProductRequest {
        @SerializedName("product_name")
        private String productName;
        @SerializedName("description")
        private String description;
        @SerializedName("price")
        private String price;
        @SerializedName("stock")
        private int stock;
        @SerializedName("discount")
        private String discount;
        @SerializedName("category_id")
        private int categoryId;
        @SerializedName("active")
        private int active; // Use int for active state

        public CreateProductRequest(String productName, String description, String price, int stock, String discount, int categoryId, int active) {
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.stock = stock;
            this.discount = discount;
            this.categoryId = categoryId;
            this.active = active;
        }
    }
}
