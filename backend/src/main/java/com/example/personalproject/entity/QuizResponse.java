package com.example.personalproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 對應 quiz_response 表。
 * 一筆資料代表一個使用者提交了一整份問卷。
 */
@Entity
@Table(name = "quiz_response")
public class QuizResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    // 這個欄位對應老師影片後段使用的 user_email，而不是 user_id。
    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    // JPA 需要的無參數建構子。
    protected QuizResponse() {
    }

    // 我們自己寫的建構子。
    public QuizResponse(Long quizId, String userEmail) {
        this.quizId = quizId;
        this.userEmail = userEmail;
        this.submittedAt = LocalDateTime.now();
    }

    // 以下 getter/setter 是我們自己寫的 Java 方法。
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
