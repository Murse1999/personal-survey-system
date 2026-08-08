package com.example.personalproject.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 問題類型的固定選單。
 *
 * enum 是 Java 內建的語法，適合表示「只能從幾個固定選項中選一個」。
 * 這裡不是讓使用者隨便輸入文字，而是只允許 SINGLE、MULTI、TEXT。
 */
public enum QuestionType {

    // 每個 enum 選項後面的中文，會傳進下面的建構子。
    SINGLE("單選題"),
    MULTI("複選題"),
    TEXT("文字題");

    // 這是每一個 enum 選項自己的中文說明，不是全部選項共用一個值。
    // String 是 Java 內建的文字型別；private 和 final 是 Java 語法。
    private final String description;

    // 這個建構子由我們自己定義，enum 建立 SINGLE、MULTI、TEXT 時會自動呼叫。
    // enum 建構子不能由外部用 new 呼叫，只能在這個 enum 內部建立固定選項。
    QuestionType(String description) {
        // this.description 左邊是這個 enum 選項的欄位；右邊是傳進來的中文。
        this.description = description;
    }

    // 這個 getter 是我們自己寫的方法，讓其他 class 取得中文說明。
    public String getDescription() {
        return description;
    }

  @JsonCreator
  public static QuestionType fromString(String input) {

    // 沒有輸入內容，先回傳 null。
    // 之後會由 @NotNull 檢查。
    if (input == null || input.isBlank()) {
      return null;
    }

    // QuestionType.values() 會取得所有題型：
    // SINGLE、MULTI、TEXT。
    for (QuestionType questionType : QuestionType.values()) {

      // 比對英文名稱，例如 SINGLE。
      if (questionType.name().equalsIgnoreCase(input)) {
        return questionType;
      }

      // 比對中文說明，例如 單選題。
      if (questionType.getDescription().equalsIgnoreCase(input)) {
        return questionType;
      }
    }

    // 都比對不到，代表前端傳了錯誤題型。
    throw new IllegalArgumentException(
      "無效的問題類型：" + input
    );
  }
}
