-- 既有資料庫新增忘記密碼的一次性驗證碼資料表。
USE personal_quiz_db;

CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    code_hash VARCHAR(100) NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME,
    attempts INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_password_reset_email (email),
    CONSTRAINT fk_password_reset_email
        FOREIGN KEY (email) REFERENCES `user`(email)
        ON DELETE CASCADE
);
