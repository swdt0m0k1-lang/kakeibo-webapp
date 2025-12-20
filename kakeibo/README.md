# kakeibo 開発メモ（README）

## アプリ概要

Servlet + JSP + H2 を使った家計簿 Web アプリです。  
ログイン必須で、**users と kakeibo を user_id で紐付けてデータを分離**する設計になっています。

---

## 技術スタック

* Java 21
* Apache Tomcat 10.1.x
* JSP / Servlet（jakarta）
* H2 Database（組み込みモード）
* JDBC

---

## 現在できていること（2025-12 時点）

### 認証周り

* ユーザー登録（`RegisterServlet` / `UserDao.insert`）
* ログイン（`LoginServlet` / `UserDao.login`）
* ログアウト（`LogoutServlet` / `session.invalidate`）
* `LoginFilter` による未ログインアクセス制御

### 家計簿機能

* `kakeibo` テーブル作成
* user_id に紐づく家計簿一覧取得（`KakeiboDao.findByUserId` / `findAll`）
* 家計簿登録（`KakeiboDao.insert`）
* 家計簿編集・更新（`KakeiboDao.update`）
* 編集フォームは `list.jsp` に統合済み
* 修正履歴管理（`kakeibo_history` テーブル + `KakeiboHistoryDao` / `HistoryServlet` / `history.jsp`）

---

## 画面構成 / URL

* **`/login`** → `LoginServlet` (`login.jsp`) ： **ログイン画面**
* **`/register`** → `RegisterServlet` (`register.jsp`) ： **ユーザー登録**
* **`/list`** → `ListServlet` (`list.jsp`) ： **家計簿一覧・編集**
* **`/history`** → `HistoryServlet` (`history.jsp`) ： **履歴表示**
* **`/logout`** → `LogoutServlet` ： **セッション破棄**
* **`/test`** → `TestServlet` (`test.jsp`) ： **デバッグ用**


> ※ JSP はすべて `/WEB-INF/jsp/` 配下、直接アクセス禁止



---

## DB設計

### users テーブル

```sql
CREATE TABLE users (
    id IDENTITY PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```
---

###kakeibo テーブル
```sql
CREATE TABLE kakeibo (
    id IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    k_date DATE NOT NULL,
    type VARCHAR(10) NOT NULL,   -- 出金 / 入金
    item VARCHAR(100) NOT NULL,
    amount INT NOT NULL,
    memo VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```
###kakeibo_history テーブル
```sql
CREATE TABLE kakeibo_history (
    id IDENTITY PRIMARY KEY,
    kakeibo_id INT NOT NULL,
    user_id INT NOT NULL,
    before_date DATE,
    after_date DATE,
    before_type VARCHAR(10),
    after_type VARCHAR(10),
    before_item VARCHAR(100),
    after_item VARCHAR(100),
    before_amount INT,
    after_amount INT,
    before_memo VARCHAR(255),
    after_memo VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (kakeibo_id) REFERENCES kakeibo(id)
);
```
###セッション設計

セッション属性名: loginUser

型: User（予定）／現在は userId / username

LoginFilter で未ログイン時は /login にリダイレクト

###ディレクトリ構成（最新）
```
src
├─ dao
│  ├─ UserDao.java
│  ├─ KakeiboDao.java
│  ├─ KakeiboHistoryDao.java
│  └─ DBUtil.java
├─ model
│  ├─ User.java
│  ├─ Kakeibo.java
│  └─ KakeiboHistory.java
├─ servlet
│  ├─ LoginServlet.java
│  ├─ LogoutServlet.java
│  ├─ ListServlet.java
│  ├─ RegisterServlet.java
│  └─ HistoryServlet.java
├─ filter
│  └─ LoginFilter.java
└─ test.servlet
   └─ TestServlet.java

webapp
├─ WEB-INF
│  └─ jsp
│     ├─ login.jsp
│     ├─ list.jsp
│     ├─ register.jsp
│     └─ history.jsp
│  └─ test
│     └─ test.jsp
├─ css
├─ js
└─ images

```

###現在の方針（重要）

* JSP 直接アクセスは禁止（必ず Servlet 経由）

* list.jsp に「一覧 + 登録・編集フォーム」を集約

* 修正履歴は KakeiboHistoryDao を通して管理

* H2 予約語回避のため、日付列は k_date に統一

###次にやること（TODO）

 * 家計簿削除機能（KakeiboDao に削除メソッド追加）

 * 月別集計 / 収支計算機能

###メモ / 注意点

* H2 の mv.db ロックに注意（同時起動不可）

* redirect 先は必ず Servlet URL

* contextPath を使ったパス指定を徹底

* TestServlet / test.jsp はデバッグ・動作確認用