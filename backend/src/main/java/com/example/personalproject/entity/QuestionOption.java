package com.example.personalproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 對應 question_option 表，記錄問題底下的 A、B、C 選項。
 */
@Entity
@Table(name = "question_option")
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "option_code", nullable = false, length = 10)
    private String optionCode;

    @Column(name = "option_text", nullable = false, length = 255)
    private String optionText;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    // JPA 需要的無參數建構子。
    protected QuestionOption() {
    }

    // 我們自己寫的建構子，方便之後建立選項。
    public QuestionOption(Long questionId, String optionCode, String optionText) {
        this.questionId = questionId;
        this.optionCode = optionCode;
        this.optionText = optionText;
    }

    // 以下 getter/setter 是我們自己寫的 Java 方法。
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getOptionCode() { return optionCode; }
    public void setOptionCode(String optionCode) { this.optionCode = optionCode; }
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
