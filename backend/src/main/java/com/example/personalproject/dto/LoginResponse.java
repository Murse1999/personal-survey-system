package com.example.personalproject.dto;

/** 登入成功後回傳給前端的資料。 */
public class LoginResponse {

    private final String token;
    private final String email;
    private final String role;

    public LoginResponse(String token, String email, String role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
