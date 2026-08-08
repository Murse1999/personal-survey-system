package com.example.personalproject.dto;

import java.time.LocalDateTime;

/**
 * 回傳給前端的一筆問卷提交紀錄。
 *
 * 這個 DTO 對應的是「誰提交了哪一份問卷，以及什麼時候提交」，
 * 不是資料庫 Entity，也不是整份問卷內容。
 */
public class QuizSubmissionResponse {

  // 這一次提交紀錄自己的 id。
  private Long responseId;

  // 被提交的問卷 id。
  private Long quizId;

  // 填答者的 Email。
  private String userEmail;

  // 提交時間。
  private LocalDateTime submittedAt;

  // Jackson 建立回傳物件時需要無參數建構子。
  public QuizSubmissionResponse() {
  }

  // 把資料庫的 QuizResponse Entity 轉成前端使用的 DTO。
  public QuizSubmissionResponse(
    Long responseId,
    Long quizId,
    String userEmail,
    LocalDateTime submittedAt) {

    this.responseId = responseId;
    this.quizId = quizId;
    this.userEmail = userEmail;
    this.submittedAt = submittedAt;
  }

  public Long getResponseId() {
    return responseId;
  }

  public void setResponseId(Long responseId) {
    this.responseId = responseId;
  }

  public Long getQuizId() {
    return quizId;
  }

  public void setQuizId(Long quizId) {
    this.quizId = quizId;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public LocalDateTime getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(LocalDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }
}
