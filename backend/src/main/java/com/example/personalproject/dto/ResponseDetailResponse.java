package com.example.personalproject.dto;

/**
 * 回傳給前端的一筆答案明細。
 *
 * 這是 DTO，不是資料庫 Entity。
 * 它用來把 ResponseDetail 的資料整理成 API 回應格式。
 */
public class ResponseDetailResponse {

  // 這筆答案屬於哪一次完整提交。
  private Long responseId;

  // 使用者回答的是哪一題。
  private Long questionId;

  // 選擇題被選到的選項 ID；文字題可以是 null。
  private Long optionId;

  // 文字題的回答內容；選擇題可以是 null。
  private String answerText;

  // Jackson 建立回傳物件時需要無參數建構子。
  public ResponseDetailResponse() {
  }

  // 建立一筆答案明細 DTO。
  public ResponseDetailResponse(
    Long responseId,
    Long questionId,
    Long optionId,
    String answerText) {

    this.responseId = responseId;
    this.questionId = questionId;
    this.optionId = optionId;
    this.answerText = answerText;
  }

  public Long getResponseId() {
    return responseId;
  }

  public void setResponseId(Long responseId) {
    this.responseId = responseId;
  }

  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public Long getOptionId() {
    return optionId;
  }

  public void setOptionId(Long optionId) {
    this.optionId = optionId;
  }

  public String getAnswerText() {
    return answerText;
  }

  public void setAnswerText(String answerText) {
    this.answerText = answerText;
  }
}
