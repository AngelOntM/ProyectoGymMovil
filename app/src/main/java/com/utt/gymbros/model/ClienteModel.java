package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClienteModel {

    public static class Cliente {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;
        @SerializedName("phone_number")
        private String phoneNumber;
        @SerializedName("address")
        private String address;
        @SerializedName("date_of_birth")
        private String dateOfBirth;
        @SerializedName("rol_id")
        private int rolId;
        @SerializedName("face_image_path")
        private String faceImagePath;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("deleted_at")
        private String deletedAt;

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAddress() { return address; }
        public String getDateOfBirth() { return dateOfBirth; }
        public int getRolId() { return rolId; }
        public String getFaceImagePath() { return faceImagePath; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public String getDeletedAt() { return deletedAt; }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public void setAddress(String address) { this.address = address; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public void setRolId(int rolId) { this.rolId = rolId; }
        public void setFaceImagePath(String faceImagePath) { this.faceImagePath = faceImagePath; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }
    }

    public static class ClienteResponse {
        @SerializedName("clientes")
        private List<Cliente> clientes;

        // Getter
        public List<Cliente> getClientes() { return clientes; }

        // Setter
        public void setClientes(List<Cliente> clientes) { this.clientes = clientes; }
    }
}
