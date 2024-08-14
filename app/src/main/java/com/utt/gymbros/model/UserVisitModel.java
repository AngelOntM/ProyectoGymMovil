package com.utt.gymbros.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserVisitModel {
    // La clase Visit representa un único objeto en la lista
    public static class Visit {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("visit_date")
        private String visitDate;
        @SerializedName("check_in_time")
        private String checkInTime;
        @SerializedName("user")
        private String name;

        // Getters y Setters
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getVisitDate() { return visitDate; }
        public String getCheckInTime() { return checkInTime; }
        public String getName() { return name; }

        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
        public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }
        public void setName(String name) { this.name = name; }
    }
}
