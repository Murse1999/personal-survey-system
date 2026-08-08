package com.example.personalproject;

// SpringApplication 來自 Spring Boot，負責啟動整個 Spring 應用程式。
import org.springframework.boot.SpringApplication;
// @SpringBootApplication 來自 Spring Boot，會開啟自動設定和元件掃描。
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 後端程式的入口。
 *
 * 今天先建立 Spring Boot 專案、資料表和 Entity，
 * 還沒有進入 Controller、Service 或 API。
 */
@SpringBootApplication
public class PersonalProjectApplication {

    public static void main(String[] args) {
        // run() 是 SpringApplication 提供的 static 方法，不是我們自己寫的。
        // 它會建立 Spring 容器，找到 @Entity 等元件，然後啟動應用程式。
        SpringApplication.run(PersonalProjectApplication.class, args);
    }
}
