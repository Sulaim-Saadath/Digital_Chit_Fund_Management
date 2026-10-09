package com.project.chitFund.module.auth.dto;

public class LoginResponse {

    private String token;
    private String userType;
    private String role;

    public LoginResponse(String token, String userType, String role) {
        this.token = token;
        this.userType = userType;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getUserType() {
        return userType;
    }

    public String getRole() {
        return role;
    }
}