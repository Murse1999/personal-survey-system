package com.example.personalproject.repository;

import com.example.personalproject.entity.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResponseRepository
  extends JpaRepository<QuizResponse, Long> {

  boolean existsByQuizIdAndUserEmail(
    Long quizId,
    String userEmail
  );

  /**
   * 查詢某份問卷的所有填答紀錄。
   *
   * quiz_response 是資料庫裡存放「一次完整提交」的資料表。
   * ?1 代表把方法收到的 quizId 放進 SQL 的第一個參數位置。
   * DESC 代表 submitted_at 由新到舊排序。
   */
  @Query(
    value = "SELECT * FROM quiz_response "
      + "WHERE quiz_id = ?1 "
      + "ORDER BY submitted_at DESC",
    nativeQuery = true
  )
  List<QuizResponse> findByQuizId(Long quizId);

  void deleteByQuizId(Long quizId);
}
