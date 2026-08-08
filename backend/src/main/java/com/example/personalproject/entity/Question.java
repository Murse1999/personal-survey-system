package com.example.personalproject.entity;

import com.example.personalproject.enums.QuestionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 對應 question 表。
 * quizId 是所屬問卷的 id；今天先用數字保存關係，關聯註解留到後續課程。
 */
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "question_num", nullable = false)
    private Integer questionNum;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    // 這兩個來自 Jakarta Persistence：把 enum 以 SINGLE/MULTI/TEXT 文字存進資料庫。
    @Column(nullable = false, length = 20)
    private QuestionType type;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    // JPA 需要的無參數建構子。
    protected Question() {
    }

    // 我們自己寫的建構子，方便之後建立問題。
    public Question(Long quizId, Integer questionNum, String title, QuestionType type) {
        this.quizId = quizId;
        this.questionNum = questionNum;
        this.title = title;
        this.type = type;
    }

    // 以下 getter/setter 是我們自己寫的 Java 方法。
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public Integer getQuestionNum() { return questionNum; }
    public void setQuestionNum(Integer questionNum) { this.questionNum = questionNum; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean required) { isRequired = required; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
