package com.example.personalproject.dto;

/** 登入成功後回傳給前端的資料。 */
public class LoginResponse {

    private final String token;
    private final String email;
    private final String role;
    private final String name;
    private final String phone;
    private final Integer age;
    private final String avatarType;

    public LoginResponse(
            String token,
            String email,
            String role,
            String name,
            String phone,
            Integer age,
            String avatarType) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.avatarType = avatarType;
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

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getAge() {
        return age;
    }

    public String getAvatarType() {
        return avatarType;
    }
}
