package com.example.personalproject.enums;

/**
 * 問卷狀態的固定選單。
 *
 * 目前 SQL 的 quiz 表是用日期和 is_published 保存資料，
 * 這個 enum 先把畫面可能顯示的狀態整理好，之後 Service 可以依條件計算狀態。
 */
public enum QuizStatus {

    DRAFT("草稿"),
    NOT_STARTED("尚未開始"),
    IN_PROGRESS("進行中"),
    ENDED("已結束");

    private final String description;

    // 這是我們自己寫的 enum 建構子，接收每個狀態的中文說明。
    QuizStatus(String description) {
        this.description = description;
    }

    // 這是我們自己寫的 getter，回傳狀態的中文說明。
    public String getDescription() {
        return description;
    }
}
