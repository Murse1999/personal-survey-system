-- 這份 SQL 是給已經建立過 personal_quiz_db 的舊資料庫執行。
-- schema.sql 是給新資料庫使用；既有資料庫不會自動重新建立欄位。

USE personal_quiz_db;

ALTER TABLE quiz
    ADD COLUMN owner_email VARCHAR(100) NULL AFTER id;

-- 舊問卷需要先歸給一個已存在的使用者，避免既有資料沒有建立者。
-- 這裡使用資料庫中最早建立的帳號，不再寫死測試帳號 email。
SET @migration_owner_email = (
    SELECT email FROM `user` ORDER BY id LIMIT 1
);

SELECT @migration_owner_email AS migration_owner_email;

UPDATE quiz
SET owner_email = @migration_owner_email
WHERE owner_email IS NULL;

ALTER TABLE quiz
    MODIFY COLUMN owner_email VARCHAR(100) NOT NULL;

ALTER TABLE quiz
    ADD CONSTRAINT fk_quiz_owner_email
    FOREIGN KEY (owner_email) REFERENCES `user`(email)
    ON UPDATE CASCADE;
