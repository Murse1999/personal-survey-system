package com.example.personalproject.entity;

// 以下類別都來自 Jakarta Persistence API，Spring Data JPA 會使用它們對應資料表。
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 對應資料庫的 user 表。
 *
 * 這個 class 是 Java 看到的使用者資料；真正的資料仍然放在 MySQL。
 */
@Entity // Jakarta Persistence 的註解：告訴 JPA 這是一個 Entity。
@Table(name = "user") // Jakarta Persistence 的註解：指定對應的資料表名稱。
public class User {

    @Id // Jakarta Persistence 的註解：指定主鍵。
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // GeneratedValue 和 GenerationType 來自 Jakarta Persistence；讓 MySQL 自動產生 id。
    private Long id;

    @Column(nullable = false, length = 50)
    // Column 來自 Jakarta Persistence；nullable=false 對應 NOT NULL。
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    private Integer age;

    // 使用者角色，例如 USER 或 ADMIN；新使用者預設是一般使用者。
    @Column(nullable = false, length = 20)
    private String role = "USER";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // JPA 透過反射建立物件時需要這個無參數建構子；這是 JPA 的使用規則。
    protected User() {
    }

    // 這是我們自己寫的建構子，方便 Service 之後建立新使用者。
    public User(String name, String phone, String password, String email, Integer age) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.age = age;
        // LocalDateTime.now() 來自 Java 標準函式庫 java.time，取得目前時間。
        this.createdAt = LocalDateTime.now();
    }

    // 以下 getter/setter 都是我們自己寫的 Java 方法，讓其他 class 讀取或修改欄位。
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
