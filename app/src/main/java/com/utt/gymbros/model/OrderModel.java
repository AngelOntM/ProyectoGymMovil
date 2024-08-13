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
        private Integer employeeId;
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

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public Integer getEmployeeId() { return employeeId; }
        public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
        public String getTotalAmount() { return totalAmount; }
        public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public List<OrderDetail> getOrderDetails() { return orderDetails; }
        public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
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
        @SerializedName("product")
        private Product product; // Añadido para los detalles del producto

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getOrderId() { return orderId; }
        public void setOrderId(int orderId) { this.orderId = orderId; }
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getUnitPrice() { return unitPrice; }
        public void setUnitPrice(String unitPrice) { this.unitPrice = unitPrice; }
        public String getTotalPrice() { return totalPrice; }
        public void setTotalPrice(String totalPrice) { this.totalPrice = totalPrice; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }
    }

    public static class Product {
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
        private boolean active;
        @SerializedName("category_id")
        private int categoryId;
        @SerializedName("product_image_path")
        private String productImagePath;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("category")
        private Category category;

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
        public String getDiscount() { return discount; }
        public void setDiscount(String discount) { this.discount = discount; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public int getCategoryId() { return categoryId; }
        public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
        public String getProductImagePath() { return productImagePath; }
        public void setProductImagePath(String productImagePath) { this.productImagePath = productImagePath; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public Category getCategory() { return category; }
        public void setCategory(Category category) { this.category = category; }
    }

    public static class Category {
        @SerializedName("id")
        private int id;
        @SerializedName("category_name")
        private String categoryName;

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    }

    public static class OrderRequest {
        @SerializedName("start_date")
        private String startDate;
        @SerializedName("end_date")
        private String endDate;

        // Getters y Setters
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        // Constructor
        public OrderRequest(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public static class OrderUserResponse {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("employee_id")
        private Integer employeeId;
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

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public Integer getEmployeeId() { return employeeId; }
        public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
        public String getTotalAmount() { return totalAmount; }
        public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public List<OrderDetail> getOrderDetails() { return orderDetails; }
        public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
    }

    public static class OrderUserDetailResponse {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("employee_id")
        private Integer employeeId;
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

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public Integer getEmployeeId() { return employeeId; }
        public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
        public String getTotalAmount() { return totalAmount; }
        public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public List<OrderDetail> getOrderDetails() { return orderDetails; }
        public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
    }
}
