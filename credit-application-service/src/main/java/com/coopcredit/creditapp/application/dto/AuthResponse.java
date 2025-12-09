package com.coopcredit.creditapp.application.dto;

/**
 * DTO for JWT authentication response.
 */
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private String username;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long expiresIn, String username) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
    }

    // Getters and Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
