package com.example.personalproject.dto;

import com.example.personalproject.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

/**
 * 回傳給前端的一個題目。
 *
 * 一個題目裡面可以包含很多個 OptionResponse。
 */
public class QuestionResponse {

  // 資料庫中的題目 id。
  private Long id;

  // 題號，例如 1、2、3。
  private Integer questionNum;

  // 題目文字。
  private String title;

  // 題型，例如 SINGLE、MULTI、TEXT。
  private QuestionType type;

  // 是否為必填題。
  private Boolean isRequired;

  // 這一題的所有選項。
  private List<OptionResponse> options = new ArrayList<>();

  // 建立空的回傳資料盒。
  public QuestionResponse() {
  }

  // 建立完整的回傳資料盒。
  public QuestionResponse(
    Long id,
    Integer questionNum,
    String title,
    QuestionType type,
    Boolean isRequired,
    List<OptionResponse> options) {

    this.id = id;
    this.questionNum = questionNum;
    this.title = title;
    this.type = type;
    this.isRequired = isRequired;
    this.options = options;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getQuestionNum() {
    return questionNum;
  }

  public void setQuestionNum(Integer questionNum) {
    this.questionNum = questionNum;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  public void setIsRequired(Boolean required) {
    isRequired = required;
  }

  public List<OptionResponse> getOptions() {
    return options;
  }

  public void setOptions(List<OptionResponse> options) {
    this.options = options;
  }
}
