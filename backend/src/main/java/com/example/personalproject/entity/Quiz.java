package com.example.personalproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 對應資料庫的 quiz 表，也就是一份問卷的基本資料。
 */
@Entity // 來自 Jakarta Persistence：把 Java class 當成資料表對應物件。
@Table(name = "quiz") // 來自 Jakarta Persistence：對應 MySQL 的 quiz 表。
public class Quiz {

    @Id // 來自 Jakarta Persistence：id 是主鍵。
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 來自 Jakarta Persistence：使用資料庫的 AUTO_INCREMENT。
    private Long id;

    // 建立這份問卷的使用者 email。
    @Column(name = "owner_email", nullable = false, length = 100)
    private String ownerEmail;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JPA 需要的無參數建構子；不是拿來給前端使用的。
    protected Quiz() {
    }

    // 我們自己寫的建構子，之後建立新問卷時可以直接傳入資料。
    public Quiz(String title, String description,
                LocalDateTime startDate, LocalDateTime endDate,
                String ownerEmail) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ownerEmail = ownerEmail;
        this.isPublished = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 以下是我們自己寫的 getter/setter，JPA 和其他 Java class 可以透過它們存取欄位。
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean published) { isPublished = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
