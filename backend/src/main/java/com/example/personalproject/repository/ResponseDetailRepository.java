package com.example.personalproject.repository;

import com.example.personalproject.entity.ResponseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseDetailRepository
  extends JpaRepository<ResponseDetail, Long> {

  /**
   * 查詢某一次提交裡的所有答案明細。
   *
   * 一次提交可能包含很多題答案，
   * 所以回傳 List<ResponseDetail>。
   * ?1 代表方法收到的第一個參數 responseId。
   */
  @Query(
    value = "SELECT * FROM response_detail "
      + "WHERE response_id = ?1",
    nativeQuery = true
  )
  List<ResponseDetail> findByResponseId(Long responseId);

  void deleteByResponseId(Long responseId);
}
