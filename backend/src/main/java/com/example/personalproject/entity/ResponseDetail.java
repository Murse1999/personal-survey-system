package com.example.personalproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 對應 response_detail 表。
 * 一筆資料代表一次提交中的一題答案。
 */
@Entity
@Table(name = "response_detail")
public class ResponseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "response_id", nullable = false)
    private Long responseId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    // 選擇題會使用 optionId；文字題可以沒有 optionId。
    @Column(name = "option_id")
    private Long optionId;

    // 文字題會把使用者輸入的內容放在 answerText。
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    // JPA 需要的無參數建構子。
    protected ResponseDetail() {
    }

    // 我們自己寫的建構子。
    public ResponseDetail(Long responseId, Long questionId,
                          Long optionId, String answerText) {
        this.responseId = responseId;
        this.questionId = questionId;
        this.optionId = optionId;
        this.answerText = answerText;
    }

    // 以下 getter/setter 是我們自己寫的 Java 方法。
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getResponseId() { return responseId; }
    public void setResponseId(Long responseId) { this.responseId = responseId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
}
