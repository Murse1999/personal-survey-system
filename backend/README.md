# Personal Survey System Backend

這是問卷平台的 Spring Boot 後端，使用 Spring Data JPA、MySQL、Spring Security 和 JWT。

## 功能

- 使用者註冊與登入
- JWT 無狀態驗證
- 一般使用者建立問卷
- 問卷建立者只能修改自己的問卷
- 問卷建立者可以修改或刪除自己的問卷，管理員可以管理所有問卷
- 登入使用者可以提交問卷，每個人同一份問卷只能提交一次
- 管理員可以讀取提交紀錄、答案明細和統計資料

## 啟動前準備

1. 在 MySQL Workbench 執行 `src/main/resources/schema.sql`。
2. 舊資料庫若還沒有 `quiz.owner_email`，再執行 `src/main/resources/migrations/V2__add_quiz_owner.sql` 一次。
3. 依照 `.env.example` 設定環境變數。

最基本的本機設定：

```bash
export DB_USERNAME=root
export DB_PASSWORD=你的MySQL密碼
export JWT_SECRET=development-secret-change-before-production
./gradlew bootRun --no-daemon
```

後端預設在 `http://localhost:8082` 啟動。

## 權限規則

| API | 權限 |
| --- | --- |
| `POST /api/users` | 公開註冊 |
| `POST /api/users/login` | 公開登入 |
| `GET /api/quiz`、`GET /api/quiz/{id}` | 公開查看 |
| `POST /api/quiz` | 任何已登入使用者 |
| `PUT /api/quiz/{id}` | 該問卷建立者或 ADMIN |
| `DELETE /api/quiz...` | 該問卷建立者或 ADMIN |
| `POST /api/quiz/{id}/submit` | 任何已登入使用者 |
| 提交紀錄、答案明細 | ADMIN；統計則開放給建立者或 ADMIN |

提交問卷時，使用者 email 由 JWT 取得，不信任 request body 裡的 email。

## 常用指令

```bash
./gradlew clean compileJava --no-daemon
./gradlew bootRun --no-daemon
```

正式環境請使用環境變數，不要把資料庫密碼或 JWT 正式密鑰寫進版本庫。
