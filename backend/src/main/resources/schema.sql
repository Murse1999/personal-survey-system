-- 這份檔案是給 MySQL Workbench 執行的 SQL，先建立資料庫，再建立六張表。

CREATE DATABASE IF NOT EXISTS personal_quiz_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE personal_quiz_db;

-- 1. 使用者主表：記錄誰填寫問卷。
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    age INT,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. 問卷主表：記錄一份問卷的基本資料。
CREATE TABLE IF NOT EXISTS quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_email VARCHAR(100) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_owner_email
        FOREIGN KEY (owner_email) REFERENCES `user`(email)
        ON UPDATE CASCADE
);

-- 3. 問題表：一份問卷可以有很多個問題。
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_num INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL COMMENT 'SINGLE、MULTI、TEXT',
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_question_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(id)
        ON DELETE CASCADE
);

-- 4. 問題選項表：單選題或複選題可以有很多個選項。
CREATE TABLE IF NOT EXISTS question_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_code VARCHAR(10) NOT NULL COMMENT '例如 A、B、C',
    option_text VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_option_question
        FOREIGN KEY (question_id) REFERENCES question(id)
        ON DELETE CASCADE
);

-- 5. 問卷回答主表：代表某位使用者提交了一整份問卷。
-- 老師影片後段使用 user_email 連到 user.email，而不是 user_id。
CREATE TABLE IF NOT EXISTS quiz_response (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    user_email VARCHAR(100) NOT NULL,
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_response_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(id),
    CONSTRAINT fk_response_user_email
        FOREIGN KEY (user_email) REFERENCES `user`(email)
        ON UPDATE CASCADE,
    CONSTRAINT uk_quiz_user_email
        UNIQUE (quiz_id, user_email)
);

-- 6. 回答明細表：記錄這次提交中，每一題實際回答了什麼。
CREATE TABLE IF NOT EXISTS response_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    response_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    option_id BIGINT,
    answer_text TEXT,
    CONSTRAINT fk_detail_response
        FOREIGN KEY (response_id) REFERENCES quiz_response(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_detail_question
        FOREIGN KEY (question_id) REFERENCES question(id),
    CONSTRAINT fk_detail_option
        FOREIGN KEY (option_id) REFERENCES question_option(id)
);
