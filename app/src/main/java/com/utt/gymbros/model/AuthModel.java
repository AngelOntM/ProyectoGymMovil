package com.utt.gymbros.model;
import com.google.gson.annotations.SerializedName;
public class AuthModel {

    public static class LoginRequest {
        @SerializedName("email")
        private String email;
        @SerializedName("password")
        private String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class LoginResponse {
        @SerializedName("token")
        private String token;
        @SerializedName("message")
        private String message;
        @SerializedName("user")
        private User user;

        public String getToken() {
            return token == null ? "" : token;
        }

        public String getMessage() {
            return message == null ? "" : message;
        }

        public User getUser() {
            return user;
        }
    }

    public static class VerifyCodeRequest {
        @SerializedName("email")
        private String email;
        @SerializedName("two_factor_code")
        private Integer code;

        public VerifyCodeRequest(String email, Integer code) {
            this.email = email;
            this.code = code;
        }
    }

    public static class VerifyCodeResponse {
        @SerializedName("message")
        private String message;
        @SerializedName("token")
        private String token;

        @SerializedName("user")
        private User user;

        public String getMessage() {
            return message == null ? "" : message;
        }

        public String getToken() {
            return token == null ? "" : token;
        }

        public User getUser() {
            return user;
        }
    }

    public static class User{
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;
        @SerializedName("phone_number")
        private String phone_number;
        @SerializedName("address")
        private String address;
        @SerializedName("date_of_birth")
        private String date_of_birth;
        @SerializedName("rol_id")
        private int rol_id;
        @SerializedName("two_factor_code")
        private String two_factor_code;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("updated_at")
        private String updated_at;
        @SerializedName("rol")
        private Rol rol;

        public int getId(){return id;}
        public String getName(){return name == null ? "" : name;}
        public String getEmail(){return email == null ? "" : email;}
        public String getPhone_number(){return phone_number == null ? "" : phone_number;}
        public String getAddress(){return address == null ? "" : address;}
        public String getDate_of_birth(){return date_of_birth == null ? "" : date_of_birth;}
        public int getRol_id(){return rol_id;}
        public String getTwo_factor_code(){return two_factor_code == null ? "" : two_factor_code;}
        public String getCreated_at(){return created_at == null ? "" : created_at;}
        public String getUpdated_at(){return updated_at == null ? "" : updated_at;}
        public Rol getRol(){return rol;}

    }

    public static class Rol{
        @SerializedName("id")
        private int id;
        @SerializedName("rol_name")
        private String rol_name;

        public int getId(){return id;}

        public String getRole(){return rol_name == null ? "" : rol_name;}
    }

    public static class changePasswordRequest{
        @SerializedName("current_password")
        private String current_password;
        @SerializedName("new_password")
        private String new_password;
        @SerializedName("new_password_confirmation")
        private String new_password_confirmation;

        public changePasswordRequest(String current_password, String new_password, String new_password_confirmation){
            this.current_password = current_password;
            this.new_password = new_password;
            this.new_password_confirmation = new_password_confirmation;
        }
    }
    public static class changePasswordResponse{
        @SerializedName("message")
        private String message;

        public String getMessage(){
            return message == null ? "" : message;
        }
    }
}

