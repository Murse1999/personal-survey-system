package com.example.personalproject.dto;

/** 登入使用者自己的公開設定資料，不包含密碼。 */
public class UserProfileResponse {

    private final Long id;
    private final String name;
    private final String phone;
    private final String email;
    private final Integer age;
    private final String avatarType;
    private final String role;

    public UserProfileResponse(
            Long id,
            String name,
            String phone,
            String email,
            Integer age,
            String avatarType,
            String role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.age = age;
        this.avatarType = avatarType;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public Integer getAge() { return age; }
    public String getAvatarType() { return avatarType; }
    public String getRole() { return role; }
}
