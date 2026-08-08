package com.example.personalproject.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class QuizDeleteRequest {

  @NotEmpty
  private List<Long> quizIds;

  public List<Long> getQuizIds() {
    return quizIds;
  }

  public void setQuizIds(List<Long> quizIds) {
    this.quizIds = quizIds;
  }

}
