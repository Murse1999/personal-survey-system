# Personal Survey System 問卷平台

這是一個 Angular + Spring Boot + MySQL 的問卷平台。使用者可以註冊、登入、建立問卷、填寫已發布的問卷；管理員可以查看提交紀錄、答案明細和統計圖表。

## 專案結構

- `src/`: Angular 前端
- `backend/`: Spring Boot 後端
- `backend/src/main/resources/schema.sql`: 新資料庫的完整建表 SQL
- `backend/src/main/resources/migrations/V2__add_quiz_owner.sql`: 舊資料庫補上問卷建立者欄位的 migration
- `src/environments/environment.prod.ts`: 部署前設定正式後端網址

## 第一次啟動

### 1. 建立 MySQL 資料庫

在 MySQL Workbench 執行 `backend/src/main/resources/schema.sql`。它會建立 `personal_quiz_db` 和六張資料表。

如果資料庫已經存在且是舊版本，請另外執行 `backend/src/main/resources/migrations/V2__add_quiz_owner.sql` 一次，不要在新資料庫重複執行。這份 migration 會把舊問卷交給資料庫中最早建立的使用者，執行前請確認該帳號是合適的管理員。

### 2. 啟動後端

先設定 MySQL 帳密。沒有密碼時可以省略 `DB_PASSWORD`，正式環境不要省略密碼。

```bash
cd backend
export DB_USERNAME=root
export DB_PASSWORD=你的MySQL密碼
export JWT_SECRET=請換成長且隨機的正式密鑰
./gradlew bootRun --no-daemon
```

後端預設網址是 `http://localhost:8082`。

### 3. 啟動前端

另開一個終端機：

```bash
npm install
npm start
```

前端預設網址是 `http://localhost:4200`。

## 使用流程

1. 開啟 `/register` 註冊一般使用者。
2. 從 `/login` 登入。
3. 登入後按「建立問卷」，填寫題目和選項。
4. 已發布且在開放時間內的問卷可以填寫。
5. 管理員登入後，可以從列表開啟統計頁。

新註冊的帳號預設是 `USER`。要把帳號提升成管理員，請在 MySQL 執行：

```sql
UPDATE personal_quiz_db.`user`
SET role = 'ADMIN'
WHERE email = '你的管理員Email';
```

## API 重點

- `POST /api/users`: 註冊
- `POST /api/users/login`: 登入並取得 JWT
- `GET /api/quiz`: 問卷列表
- `GET /api/quiz/{id}`: 問卷內容
- `POST /api/quiz`: 登入後建立問卷
- `PUT /api/quiz/{id}`: 建立者或管理員修改問卷
- `POST /api/quiz/{id}/submit`: 登入後提交問卷
- `GET /api/quiz/{id}/statistics`: 問卷建立者或管理員查看統計

登入後的 API 會由 Angular 自動附上：

```text
Authorization: Bearer <JWT>
```

## 測試與打包

```bash
# 前端 production build
npm run build

# 前端單元測試
npm test -- --watch=false --browsers=ChromeHeadless

# 後端編譯
cd backend
./gradlew clean compileJava --no-daemon
```

`dist/` 和 `backend/build/` 是編譯產物，不需要提交到 Git；部署時依照主機平台的方式建立前端靜態檔和後端服務即可。

## 上線前必改

1. 把 `src/environments/environment.prod.ts` 的 `apiUrl` 改成正式後端網址。
2. 把後端的 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 設成正式資料庫連線。
3. 把 `JWT_SECRET` 換成正式密鑰，不要使用 `application.properties` 的開發預設值。
4. 把 `CORS_ALLOWED_ORIGIN` 設成正式前端網址。
5. 前端網址和後端網址都使用 HTTPS。

正式環境啟動後端時，請額外設定 `SPRING_PROFILES_ACTIVE=prod`。這會要求提供
`DB_URL`、`DB_USERNAME`、`DB_PASSWORD`、`JWT_SECRET` 和
`CORS_ALLOWED_ORIGIN`，避免誤用本機預設設定。
