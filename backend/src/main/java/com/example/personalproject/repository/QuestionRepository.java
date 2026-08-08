package com.example.personalproject.repository;

import com.example.personalproject.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作 question 資料表。
 */
@Repository
public interface QuestionRepository
  extends JpaRepository<Question, Long> {

  /*
   * 依照 quizId 找出某一份問卷的所有題目，
   * 並按照題號由小到大排序。
   *
   * Spring Data JPA 會依照方法名稱自動產生查詢。
   */
  List<Question> findByQuizIdOrderByQuestionNumAsc(
    Long quizId
  );

  @Modifying
  @Query(
    value = "DELETE FROM question "
           + "WHERE quiz_id = :quizId",
    nativeQuery = true
  )
  void deleteByQuizId(@Param("quizId") Long quizId);

  @Modifying
  @Query(
    value = "DELETE FROM question "
      + "WHERE quiz_id IN (:quizIds)",
    nativeQuery = true
  )
  void deleteByQuizIds(
    @Param("quizIds") List<Long> quizIds
  );
}
