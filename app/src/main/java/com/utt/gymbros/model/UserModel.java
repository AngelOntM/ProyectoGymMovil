package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserModel {

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

    public static class ActiveMembership {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("membership_id")
        private int membershipId;
        @SerializedName("start_date")
        private String startDate;
        @SerializedName("end_date")
        private String endDate;
        @SerializedName("membership_name")
        private String membershipName;

        // Getters
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getMembershipId() { return membershipId; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getMembershipName() { return membershipName; }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setMembershipId(int membershipId) { this.membershipId = membershipId; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public void setMembershipName(String membershipName) { this.membershipName = membershipName; }
    }

    public static class UserResponse {
        @SerializedName("user")
        private User user;
        @SerializedName("active_membership")
        private ActiveMembership activeMembership;

        // Getters
        public User getUser() { return user; }
        public ActiveMembership getActiveMembership() { return activeMembership; }

        // Setters
        public void setUser(User user) { this.user = user; }
        public void setActiveMembership(ActiveMembership activeMembership) { this.activeMembership = activeMembership; }
    }
}
