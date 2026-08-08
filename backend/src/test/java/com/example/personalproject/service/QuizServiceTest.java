package com.example.personalproject.service;

import com.example.personalproject.dto.OptionRequest;
import com.example.personalproject.dto.QuestionRequest;
import com.example.personalproject.dto.QuizDeleteRequest;
import com.example.personalproject.dto.QuizRequest;
import com.example.personalproject.entity.Question;
import com.example.personalproject.entity.Quiz;
import com.example.personalproject.entity.QuizResponse;
import com.example.personalproject.enums.QuestionType;
import com.example.personalproject.repository.QuestionOptionRepository;
import com.example.personalproject.repository.QuestionRepository;
import com.example.personalproject.repository.QuizRepository;
import com.example.personalproject.repository.QuizResponseRepository;
import com.example.personalproject.repository.ResponseDetailRepository;
import com.example.personalproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

  @Mock
  private QuizRepository quizRepository;

  @Mock
  private QuestionRepository questionRepository;

  @Mock
  private QuestionOptionRepository questionOptionRepository;

  @Mock
  private QuizResponseRepository quizResponseRepository;

  @Mock
  private ResponseDetailRepository responseDetailRepository;

  @Mock
  private UserRepository userRepository;

  private QuizService quizService;

  @BeforeEach
  void setUp() {
    quizService = new QuizService(
      quizRepository,
      questionRepository,
      questionOptionRepository,
      quizResponseRepository,
      responseDetailRepository,
      userRepository
    );
  }

  @Test
  void batchDeleteRejectsQuizOwnedByAnotherUser() {
    Quiz quiz = quiz(42L, "owner@example.com");
    QuizDeleteRequest request = new QuizDeleteRequest();
    request.setQuizIds(List.of(42L));
    when(quizRepository.findById(42L)).thenReturn(Optional.of(quiz));

    assertThrows(
      AccessDeniedException.class,
      () -> quizService.deleteQuizzes(request, "other@example.com", false)
    );

    verify(quizResponseRepository, never()).findByQuizId(42L);
    verify(quizRepository, never()).deleteByQuizIds(any());
  }

  @Test
  void answeredQuizRejectsQuestionContentChanges() {
    Quiz quiz = quiz(42L, "owner@example.com");
    Question existingQuestion = new Question(
      42L,
      1,
      "Original question",
      QuestionType.SINGLE
    );
    existingQuestion.setId(7L);
    when(quizRepository.findById(42L)).thenReturn(Optional.of(quiz));
    when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
    when(quizResponseRepository.findByQuizId(42L))
      .thenReturn(List.of(new QuizResponse(42L, "respondent@example.com")));
    when(questionRepository.findByQuizIdOrderByQuestionNumAsc(42L))
      .thenReturn(List.of(existingQuestion));
    QuizRequest request = new QuizRequest();
    request.setTitle("Updated title");
    request.setDescription("Description");
    request.setStartDate(LocalDateTime.of(2026, 8, 1, 0, 0));
    request.setEndDate(LocalDateTime.of(2026, 12, 31, 23, 59));
    request.setIsPublished(true);

    QuestionRequest changedQuestion = new QuestionRequest();
    changedQuestion.setQuestionNum(1);
    changedQuestion.setTitle("Changed question");
    changedQuestion.setType(QuestionType.SINGLE);
    changedQuestion.setIsRequired(true);
    OptionRequest option = new OptionRequest();
    option.setOptionCode("A");
    option.setOptionText("Yes");
    changedQuestion.setOptions(List.of(option));
    request.setQuestions(List.of(changedQuestion));

    assertThrows(
      IllegalArgumentException.class,
      () -> quizService.updateQuiz(request, 42L, "owner@example.com", false)
    );

    verify(questionOptionRepository, never()).deleteByQuizId(anyLong());
    verify(questionRepository, never()).deleteByQuizId(anyLong());
  }

  private Quiz quiz(Long id, String ownerEmail) {
    Quiz quiz = new Quiz(
      "Quiz",
      "Description",
      LocalDateTime.of(2026, 8, 1, 0, 0),
      LocalDateTime.of(2026, 12, 31, 23, 59),
      ownerEmail
    );
    quiz.setId(id);
    quiz.setIsPublished(true);
    return quiz;
  }
}
