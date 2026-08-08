package com.example.personalproject.service;

import com.example.personalproject.dto.*;
import com.example.personalproject.entity.*;
import com.example.personalproject.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import com.example.personalproject.enums.QuestionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * 處理問卷相關的商業邏輯。
 */
@Service
public class QuizService {

  // 操作 quiz 資料表。
  private final QuizRepository quizRepository;

  // 操作 question 資料表。
  private final QuestionRepository questionRepository;

  // 操作 question_option 資料表。
  private final QuestionOptionRepository questionOptionRepository;

  private final QuizResponseRepository quizResponseRepository;

  private final ResponseDetailRepository responseDetailRepository;

  private final UserRepository userRepository;

  /*
   * 建構子注入。
   *
   * Spring 會自動把三個 Repository 物件傳進來。
   */
  public QuizService(
    QuizRepository quizRepository,
    QuestionRepository questionRepository,
    QuestionOptionRepository questionOptionRepository,
    QuizResponseRepository quizResponseRepository,
    ResponseDetailRepository responseDetailRepository,
    UserRepository userRepository) {

    this.quizRepository = quizRepository;
    this.questionRepository = questionRepository;
    this.questionOptionRepository = questionOptionRepository;
    this.quizResponseRepository = quizResponseRepository;
    this.responseDetailRepository = responseDetailRepository;
    this.userRepository = userRepository;
  }

  /**
   * 建立一份新的問卷。
   *
   * 這一步先儲存 quiz 主表。
   */
    @Transactional
    public Long addQuiz(QuizRequest quizRequest, String ownerEmail) {

      if (quizRequest.getEndDate().isBefore(quizRequest.getStartDate())) {
        throw new IllegalArgumentException("結束時間不能早於開始時間");
      }

      Quiz quizForQuizRequest = new Quiz(
        quizRequest.getTitle(),
        quizRequest.getDescription(),
        quizRequest.getStartDate(),
        quizRequest.getEndDate(),
        ownerEmail
      );

      if (quizRequest.getIsPublished()!=null) {
        quizForQuizRequest.setIsPublished(quizRequest.getIsPublished());
      }

      Quiz saveQuiz = quizRepository.save(quizForQuizRequest);

      if (quizRequest.getQuestions()!=null) {
        for (QuestionRequest questionRequest : quizRequest.getQuestions()) {

          validateQuestionOptions(questionRequest);

          Question questionForGetQuestions = new Question(
            saveQuiz.getId(),
            questionRequest.getQuestionNum(),
            questionRequest.getTitle(),
            questionRequest.getType()
          );
          if (questionRequest.getIsRequired()!=null) {
            questionForGetQuestions.setIsRequired(questionRequest.getIsRequired());
          }
          Question saveQuestion = questionRepository.save(questionForGetQuestions);

          if (questionRequest.getOptions()!=null){
            int num =0;
            for(OptionRequest  optionRequest : questionRequest.getOptions()){

              QuestionOption saveQuestionOption = new QuestionOption(
                saveQuestion.getId(),
                optionRequest.getOptionCode(),
                optionRequest.getOptionText()
              );
              saveQuestionOption.setSortOrder(num);
              questionOptionRepository.save(saveQuestionOption);
              num++;
            }
          }
        }
      }
      return saveQuiz.getId();
    }

    @Transactional (readOnly=true)
    public QuizResponseDto  seeQuiz (Long id){

      Optional<Quiz> findQuiz=quizRepository.findById(id);

      if (findQuiz.isEmpty()) {
        throw new IllegalArgumentException(id+"不存在此id問卷");
      }

      Quiz quiz = findQuiz.get();

      List<Question> questions =questionRepository.findByQuizIdOrderByQuestionNumAsc(quiz.getId());

      List<QuestionResponse>questionResponses = new ArrayList<>();

      for (Question question : questions) {
        List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderBySortOrderAsc(question.getId());
        List<OptionResponse> optionResponses = new ArrayList<>();
        for (QuestionOption option : options) {
          OptionResponse optionResponse = new OptionResponse(
            option.getId(),
            option.getOptionCode(),
            option.getOptionText()
          );
          optionResponses.add(optionResponse);
        }
        QuestionResponse questionResponse = new QuestionResponse(
          question.getId(),
          question.getQuestionNum(),
          question.getTitle(),
          question.getType(),
          question.getIsRequired(),
          optionResponses
        );
        questionResponses.add(questionResponse);
      }

      QuizResponseDto responseDto = new QuizResponseDto(
       quiz.getId(),
       quiz.getTitle(),
       quiz.getDescription(),
       quiz.getStartDate(),
       quiz.getEndDate(),
       quiz.getIsPublished(),
        questionResponses
      );
      return responseDto;
    }

    /**
     * 給公開問卷頁使用：已發布問卷可以公開查看，未發布問卷只能由建立者或管理員查看。
     */
    @Transactional(readOnly = true)
    public QuizResponseDto seeQuizForViewer(
      Long id,
      String currentUserEmail,
      boolean isAdmin) {

      Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(id + "不存在此id問卷"));

      boolean isOwner = currentUserEmail != null
        && currentUserEmail.equals(quiz.getOwnerEmail());

      if (!Boolean.TRUE.equals(quiz.getIsPublished()) && !isOwner && !isAdmin) {
        throw new AccessDeniedException("這份問卷尚未發布");
      }

      return seeQuiz(id);
    }

    @Transactional (readOnly=true)
    public List<QuizResponseDto> seeAllQuizzes(){
     List<Quiz>  quizzes = quizRepository.findByIsPublishedTrueOrderByIdDesc();
     List<QuizResponseDto> responseDtos = new ArrayList<>();
     for (Quiz quiz : quizzes) {
       QuizResponseDto responseDto = seeQuiz(quiz.getId());
       responseDtos.add(responseDto);
     }
     return responseDtos;
    }

    @Transactional(readOnly = true)
    public List<QuizResponseDto> seeMyQuizzes(String ownerEmail) {
      List<Quiz> quizzes = quizRepository.findByOwnerEmailOrderByIdDesc(ownerEmail);
      List<QuizResponseDto> responseDtos = new ArrayList<>();

      for (Quiz quiz : quizzes) {
        responseDtos.add(seeQuiz(quiz.getId()));
      }

      return responseDtos;
    }

    @Transactional
    public QuizResponseDto updateQuiz(
      QuizRequest quizRequest,
      Long id,
      String currentUserEmail,
      boolean isAdmin) {

      Optional<Quiz>findQuiz=quizRepository.findById(id);
      if (findQuiz.isEmpty()) {
        throw new IllegalArgumentException(id+"找不到此項問卷");
      }
      Quiz quiz = findQuiz.get();

      ensureQuizAccess(quiz, currentUserEmail, isAdmin, "只能修改自己建立的問卷");

      if (quizRequest.getEndDate().isBefore(quizRequest.getStartDate())) {
        throw new IllegalArgumentException("結束時間不能早於開始時間");
      }
      if (quizRequest.getIsPublished()!=null) {
        quiz.setIsPublished(quizRequest.getIsPublished());
      }

      quiz.setEndDate(quizRequest.getEndDate());
      quiz.setStartDate(quizRequest.getStartDate());
      quiz.setTitle(quizRequest.getTitle());
      quiz.setDescription(quizRequest.getDescription());

      quizRepository.save(quiz);


      if (quizRequest.getQuestions()!=null) {
        List<QuizResponse> responses = quizResponseRepository.findByQuizId(quiz.getId());

        if (!responses.isEmpty()) {
          ensureAnsweredQuizStructureUnchanged(
            quiz.getId(),
            quizRequest.getQuestions()
          );
        } else {
          replaceQuestions(quiz.getId(), quizRequest.getQuestions());
        }
      }
      return seeQuiz(quiz.getId());
    }

    @Transactional
    public String deleteQuiz(Long id, String currentUserEmail, boolean isAdmin) {

      Optional<Quiz> findQuiz=quizRepository.findById(id);
      if (findQuiz.isEmpty()) {
        throw new IllegalArgumentException(id+"不存在");
      }
      ensureQuizAccess(
        findQuiz.get(),
        currentUserEmail,
        isAdmin,
        "只能刪除自己建立的問卷"
      );
      deleteResponseData(id);
      questionOptionRepository.deleteByQuizId(id);
      questionRepository.deleteByQuizId(id);
      quizRepository.deleteById(id);
      return "success";

    }

  @Transactional
  public String deleteQuizzes(
    QuizDeleteRequest request,
    String currentUserEmail,
    boolean isAdmin) {

      for (Long id : request.getQuizIds()) {
        Optional<Quiz> findQuiz=quizRepository.findById(id);
        if (findQuiz.isEmpty()) {
          throw new IllegalArgumentException(id+"不存在");
        }
        ensureQuizAccess(
          findQuiz.get(),
          currentUserEmail,
          isAdmin,
          "只能刪除自己建立的問卷"
        );
        deleteResponseData(id);
      }
      questionOptionRepository.deleteByQuizIds(request.getQuizIds());
      questionRepository.deleteByQuizIds(request.getQuizIds());
      quizRepository.deleteByQuizIds(request.getQuizIds());
      return "success";
  }

  private void deleteResponseData(Long quizId) {
    for (QuizResponse response : quizResponseRepository.findByQuizId(quizId)) {
      responseDetailRepository.deleteByResponseId(response.getId());
    }
    quizResponseRepository.deleteByQuizId(quizId);
  }

  private void replaceQuestions(
    Long quizId,
    List<QuestionRequest> questionRequests) {

    questionOptionRepository.deleteByQuizId(quizId);
    questionRepository.deleteByQuizId(quizId);

    for (QuestionRequest questionRequest : questionRequests) {
      validateQuestionOptions(questionRequest);
      Question question = new Question(
        quizId,
        questionRequest.getQuestionNum(),
        questionRequest.getTitle(),
        questionRequest.getType()
      );
      if (questionRequest.getIsRequired()!=null) {
        question.setIsRequired(questionRequest.getIsRequired());
      }
      Question questionForOption = questionRepository.save(question);

      if (questionRequest.getOptions()!=null) {
        int num = 0;
        for (OptionRequest optionRequest : questionRequest.getOptions()) {
          QuestionOption questionOption = new QuestionOption(
            questionForOption.getId(),
            optionRequest.getOptionCode(),
            optionRequest.getOptionText()
          );
          questionOption.setSortOrder(num);
          questionOptionRepository.save(questionOption);
          num++;
        }
      }
    }
  }

  private void ensureAnsweredQuizStructureUnchanged(
    Long quizId,
    List<QuestionRequest> questionRequests) {

    List<Question> existingQuestions =
      questionRepository.findByQuizIdOrderByQuestionNumAsc(quizId);

    if (existingQuestions.size() != questionRequests.size()) {
      throw new IllegalArgumentException(
        "這份問卷已有填答，不能新增、刪除或重新排序題目"
      );
    }

    for (int index = 0; index < existingQuestions.size(); index++) {
      Question existingQuestion = existingQuestions.get(index);
      QuestionRequest requestedQuestion = questionRequests.get(index);

      if (!Objects.equals(existingQuestion.getQuestionNum(), requestedQuestion.getQuestionNum())
        || !Objects.equals(existingQuestion.getTitle(), requestedQuestion.getTitle())
        || !Objects.equals(existingQuestion.getType(), requestedQuestion.getType())
        || !Objects.equals(existingQuestion.getIsRequired(), requestedQuestion.getIsRequired())) {
        throw new IllegalArgumentException(
          "這份問卷已有填答，不能修改題目內容或必填設定"
        );
      }

      validateQuestionOptions(requestedQuestion);
      List<QuestionOption> existingOptions =
        questionOptionRepository.findByQuestionIdOrderBySortOrderAsc(
          existingQuestion.getId()
        );
      List<OptionRequest> requestedOptions = requestedQuestion.getOptions() == null
        ? List.of()
        : requestedQuestion.getOptions();

      if (existingOptions.size() != requestedOptions.size()) {
        throw new IllegalArgumentException(
          "這份問卷已有填答，不能新增或刪除選項"
        );
      }

      for (int optionIndex = 0; optionIndex < existingOptions.size(); optionIndex++) {
        QuestionOption existingOption = existingOptions.get(optionIndex);
        OptionRequest requestedOption = requestedOptions.get(optionIndex);

        if (!Objects.equals(existingOption.getOptionCode(), requestedOption.getOptionCode())
          || !Objects.equals(existingOption.getOptionText(), requestedOption.getOptionText())) {
          throw new IllegalArgumentException(
            "這份問卷已有填答，不能修改選項內容"
          );
        }
      }
    }
  }

  @Transactional
  public Long submitQuiz(
    Long quizId,
    QuizSubmitRequest request,
    String currentUserEmail) {

    Optional<Quiz> findQuiz = quizRepository.findById(quizId);
    if (findQuiz.isEmpty()) {
      throw new IllegalArgumentException(quizId + "不存在");
    }

    Quiz quiz = findQuiz.get();

    if (!quiz.getIsPublished()) {
      throw new IllegalArgumentException("此問卷尚未發布");
    }

    LocalDateTime now = LocalDateTime.now();

    if (now.isBefore(quiz.getStartDate())
      || now.isAfter(quiz.getEndDate())) {

      throw new IllegalArgumentException(
        "目前不在問卷開放時間內"
      );
    }

    if(quizResponseRepository.existsByQuizIdAndUserEmail(
      quiz.getId(),
      currentUserEmail
    )){
      throw new IllegalArgumentException("已經提交過此問卷");
    }

    QuizResponse response = new QuizResponse(
      quiz.getId(),
      currentUserEmail
    );
    QuizResponse savedResponse =
      quizResponseRepository.save(response);

    // 查出這份問卷的所有題目
    List<Question> allQuestions =
      questionRepository.findByQuizIdOrderByQuestionNumAsc(
        quiz.getId()
      );

// 檢查必填題是否整題漏傳
    for (Question quizQuestion : allQuestions) {

      if (!Boolean.TRUE.equals(quizQuestion.getIsRequired())) {
        continue;
      }

      boolean answered = false;

      // 尋找前端是否有傳這一題的答案
      for (AnswerRequest answer : request.getAnswers()) {

        if (quizQuestion.getId()
          .equals(answer.getQuestionId())) {

          answered = true;
          break;
        }
      }

      // 必填題完全沒有出現在 answers 裡
      if (!answered) {
        throw new IllegalArgumentException(
          "第 " + quizQuestion.getQuestionNum()
            + " 題為必填題，不能漏答"
        );
      }
    }

    for (AnswerRequest answer : request.getAnswers()) {

      Optional<Question> findQuestion =
        questionRepository.findById(answer.getQuestionId());

      if (findQuestion.isEmpty()) {
        throw new IllegalArgumentException(answer.getQuestionId()+"不存在");
      }
      Question question = findQuestion.get();

      if (!question.getQuizId().equals(quiz.getId())) {
        throw new IllegalArgumentException("不相符");
      }
      if (question.getType()== QuestionType.TEXT){
        if (Boolean.TRUE.equals(question.getIsRequired()) && (answer.getAnswerText() == null||answer.getAnswerText().isBlank())){
          throw new IllegalArgumentException("錯誤");
        }
        ResponseDetail responseDetail = new ResponseDetail(
          savedResponse.getId(),
          question.getId(),
          null,
          answer.getAnswerText()
        );
        responseDetailRepository.save(responseDetail);
      }else {
        if(Boolean.TRUE.equals(question.getIsRequired())&&(answer.getOptionIds()==null||answer.getOptionIds().isEmpty())){
        throw new IllegalArgumentException("錯誤");
      }if(answer.getOptionIds() !=null){
          if(question.getType() == QuestionType.SINGLE&&answer.getOptionIds().size()>1){
              throw new IllegalArgumentException("單選題只能選擇一個");
          }
        for(Long optionId:answer.getOptionIds()){

          Optional<QuestionOption> findOption =
            questionOptionRepository.findById(optionId);

          if (findOption.isEmpty()) {
            throw new IllegalArgumentException("不存在此選項");
          }

          QuestionOption option = findOption.get();

          if (!option.getQuestionId().equals(question.getId())) {
            throw new IllegalArgumentException("此選項不屬於這一題");
          }
          ResponseDetail responseDetail = new ResponseDetail(
            savedResponse.getId(),
            question.getId(),
             optionId,
             null
          );
          responseDetailRepository.save(responseDetail);
      }}

      }
    }
    return savedResponse.getId();
  }

    @Transactional(readOnly = true)
    public List<QuizSubmissionResponse> searchByQuizId(Long quizId) {

      Optional<Quiz> quiz = quizRepository.findById(quizId);
      if (quiz.isEmpty()) {
        throw new IllegalArgumentException("找不到此問卷");
      }

      List<QuizResponse> quizResponses = quizResponseRepository.findByQuizId(quizId);

      List<QuizSubmissionResponse> submissionResponses  = new ArrayList<>();
      for (QuizResponse quizResponse : quizResponses) {

        QuizSubmissionResponse submissionResponse = new QuizSubmissionResponse(
          quizResponse.getId(),
          quizResponse.getQuizId(),
          quizResponse.getUserEmail(),
          quizResponse.getSubmittedAt()
        );
        submissionResponses.add(submissionResponse);
      }
      return submissionResponses;
    }

    @Transactional(readOnly = true)
    public List<ResponseDetailResponse>  searchByResponseId(Long responseId) {

      Optional<QuizResponse>  quizResponse = quizResponseRepository.findById(responseId);
      if (quizResponse.isEmpty()) {
        throw new IllegalArgumentException("找不到此提交紀錄");
      }
      List<ResponseDetail> responseDetails = responseDetailRepository.findByResponseId(responseId);
      List<ResponseDetailResponse> submissionResponses = new ArrayList<>();
      for (ResponseDetail responseDetail : responseDetails) {
        ResponseDetailResponse submissionResponse = new ResponseDetailResponse(
          responseDetail.getResponseId(),
          responseDetail.getQuestionId(),
          responseDetail.getOptionId(),
          responseDetail.getAnswerText()
        );
        submissionResponses.add(submissionResponse);
      }
      return submissionResponses;
    }

    @Transactional(readOnly = true)
    public QuizStatResponse quizStat(
      Long quizId,
      String currentUserEmail,
      boolean isAdmin) {
      Optional<Quiz> quiz = quizRepository.findById(quizId);
      if (quiz.isEmpty()) {
        throw new IllegalArgumentException("找不到此問卷");
      }

      Quiz quiz1 = quiz.get();
      ensureQuizAccess(
        quiz1,
        currentUserEmail,
        isAdmin,
        "只能查看自己建立的問卷統計"
      );
      List<QuizResponse> quizResponses = quizResponseRepository.findByQuizId(quizId);
      long totalRespondents = quizResponses.size();

      // 先把這份問卷所有人的答案明細集中起來。
      // 每一筆 ResponseDetail 代表某個人回答某一題的結果。
      List<ResponseDetail> allDetails = new ArrayList<>();
      for (QuizResponse quizResponse : quizResponses) {
        allDetails.addAll(
          responseDetailRepository.findByResponseId(
            quizResponse.getId()
          )
        );
      }

      // 查出這份問卷的所有題目，並按照題目順序排列。
      List<Question> questions =
        questionRepository.findByQuizIdOrderByQuestionNumAsc(
          quiz1.getId()
        );

      // 準備一個列表，放入每一題的統計結果。
      List<QuestionStatDto> questionStats = new ArrayList<>();

      // 一題一題計算統計資料。
      for (Question question : questions) {

        // 只留下目前這一題的所有回答明細。
        List<ResponseDetail> questionDetails = new ArrayList<>();
        for (ResponseDetail detail : allDetails) {
          if (question.getId().equals(detail.getQuestionId())) {
            questionDetails.add(detail);
          }
        }

        List<OptionStatDto> optionStats = new ArrayList<>();
        List<String> textAnswers = new ArrayList<>();

        // 選擇題需要計算每個選項被選取的次數和百分比。
        if (question.getType() == QuestionType.SINGLE
          || question.getType() == QuestionType.MULTI) {

          List<QuestionOption> options =
            questionOptionRepository.findByQuestionIdOrderBySortOrderAsc(
              question.getId()
            );

          // 一個選項一個選項統計，例如先算 A，再算 B，最後算 C。
          for (QuestionOption option : options) {

            // 這個變數只負責計算「目前 option」被選了幾次。
            // 每換一個 option，就會重新從 0 開始計算。
            long selectedCount = 0;

            // 把目前這一題的所有回答拿出來逐筆檢查。
            for (ResponseDetail detail : questionDetails) {

              // 如果答案裡記錄的 optionId 等於目前 option 的 id，
              // 就代表這位使用者選了目前這個選項。
              if (option.getId().equals(detail.getOptionId())) {

                // 找到一筆，就把目前選項的計數加 1。
                selectedCount++;
              }
            }

            // 避免沒有填答者時除以 0。
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalRespondents > 0) {
              percentage = BigDecimal.valueOf(selectedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                  BigDecimal.valueOf(totalRespondents),
                  2,
                  RoundingMode.HALF_UP
                );
            }

            optionStats.add(
              new OptionStatDto(
                option.getId(),
                option.getOptionCode(),
                option.getOptionText(),
                selectedCount,
                percentage
              )
            );
          }

          // 選擇題不使用文字回答列表。
          textAnswers = null;
        }

        // 文字題收集所有非空白回答。
        if (question.getType() == QuestionType.TEXT) {
          for (ResponseDetail detail : questionDetails) {
            if (detail.getAnswerText() != null
              && !detail.getAnswerText().isBlank()) {
              textAnswers.add(detail.getAnswerText());
            }
          }

          // 文字題不使用選項統計列表。
          optionStats = null;
        }

        questionStats.add(
          new QuestionStatDto(
            question.getId(),
            question.getQuestionNum(),
            question.getTitle(),
            question.getType(),
            optionStats,
            textAnswers
          )
        );
      }

      // 組合整份問卷的統計回應。
      return new QuizStatResponse(
        quiz1.getId(),
        quiz1.getTitle(),
        totalRespondents,
        questionStats
      );
    }


    private void validateQuestionOptions (QuestionRequest questionRequest) {
        if (questionRequest.getType() == QuestionType.TEXT) {
          return;
        }
        else if (questionRequest.getOptions()== null||questionRequest.getOptions().isEmpty() ){
            throw new IllegalArgumentException(questionRequest.getQuestionNum()+"錯誤");
          }
        }

    private void ensureQuizAccess(
      Quiz quiz,
      String currentUserEmail,
      boolean isAdmin,
      String message) {

      if (!isAdmin && !Objects.equals(currentUserEmail, quiz.getOwnerEmail())) {
        throw new AccessDeniedException(message);
      }
    }

}
