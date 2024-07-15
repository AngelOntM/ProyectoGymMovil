package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VisitUserModel {

    public static class Visit {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("visit_date")
        private String visitDate;
        @SerializedName("check_in_time")
        private String checkInTime;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("user")
        private User user;

        // Getters
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getVisitDate() { return visitDate; }
        public String getCheckInTime() { return checkInTime; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public User getUser() { return user; }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
        public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setUser(User user) { this.user = user; }
    }

    public static class User {
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

    @SerializedName("visits")
    private List<Visit> visits;

    // Getters
    public List<Visit> getVisits() { return visits; }

    // Setters
    public void setVisits(List<Visit> visits) { this.visits = visits; }
}
