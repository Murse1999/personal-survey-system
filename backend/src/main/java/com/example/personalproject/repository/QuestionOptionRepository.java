package com.example.personalproject.repository;

import com.example.personalproject.entity.QuestionOption;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作 question_option 資料表。
 */
@Repository
public interface QuestionOptionRepository
  extends JpaRepository<QuestionOption, Long> {

  /*
   * 找出某一題底下的所有選項，
   * 並按照選項的排序欄位由小到大排列。
   */
  List<QuestionOption> findByQuestionIdOrderBySortOrderAsc(
    Long questionId
  );

  /**
   * 刪除指定問卷底下所有題目的選項。
   *
   * question_option 表只有 question_id，沒有 quiz_id，
   * 所以先找出該問卷的題目 id，再刪除那些題目的選項。
   */
  @Modifying
  @Query(
    value = "DELETE FROM question_option "
      + "WHERE question_id IN ("
      + "SELECT id FROM question WHERE quiz_id = :quizId"
      + ")",
    nativeQuery = true
  )
  void deleteByQuizId(@Param("quizId") Long quizId);

  @Modifying
  @Query(
    value = "DELETE FROM question_option "
      + "WHERE question_id IN ("
      + "SELECT id FROM question "
      + "WHERE quiz_id IN (:quizIds)"
      + ")",
    nativeQuery = true
  )
  void deleteByQuizIds(
    @Param("quizIds") List<Long> quizIds
  );
}
