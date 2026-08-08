package com.example.personalproject.repository;

import com.example.personalproject.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作 quiz 資料表。
 */
@Repository
public interface QuizRepository
  extends JpaRepository<Quiz, Long> {

  List<Quiz> findByOwnerEmailOrderByIdDesc(String ownerEmail);

  List<Quiz> findByIsPublishedTrueOrderByIdDesc();

  @Modifying
  @Query(
    value = "DELETE FROM quiz "
      + "WHERE id IN (:quizIds)",
    nativeQuery = true
  )
  void deleteByQuizIds(
    @Param("quizIds") List<Long> quizIds
  );

}
