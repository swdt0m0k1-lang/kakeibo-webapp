package test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * デモ用データを初期状態に戻すクラス
 */
public class DemoDataInitializer {

	private static final String JDBC_URL = "jdbc:h2:~/kakeibo_demo";
	private static final String DB_USER = "sa";
	private static final String DB_PASS = "";

	public void reset() {

		if (!JDBC_URL.contains("kakeibo_demo")) {
			throw new IllegalStateException("本番DBではデモ初期化できません");
		}

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
				Statement stmt = conn.createStatement()) {

			conn.setAutoCommit(false);

			/* ===== 全削除 ===== */
			stmt.executeUpdate("DELETE FROM KAKEIBO_HISTORY");
			stmt.executeUpdate("DELETE FROM KAKEIBO");
			stmt.executeUpdate("DELETE FROM USERS");

			/* ===== USERS 初期データ ===== */
			stmt.executeUpdate("""
					    INSERT INTO USERS (ID, USERNAME, PASSWORD) VALUES
					    (1, 'test', 'test'),
					    (2, 'イチロー', 'test'),
					    (3, 'サンプルA', 'test'),
					    (4, 'サンプルB', 'test'),
					    (5, 'サンプルC', 'test')
					""");

			/* ===== KAKEIBO 初期データ ===== */
			stmt.executeUpdate("""
					    INSERT INTO KAKEIBO
					    (ID, USER_ID, K_DATE, TYPE, ITEM, AMOUNT, MEMO, DELETED)
					    VALUES
					    (4, 1, '2025-12-19', '出金', '夕食', 2500, 'ハンバーグセット', FALSE),
					    (5, 2, '2025-12-19', '入金', '報酬', 100000001, '', FALSE),
					    (6, 1, '2025-12-20', '出金', '昼食', 980, 'うどん', FALSE),
					    (8, 1, '2025-12-20', '入金', '宝くじ', 5000000, '', FALSE),
					    (9, 2, '2025-12-20', '出金', 'トレーニング', 15000, '', FALSE),
					    (10, 1, '2025-12-21', '出金', 'switch2', 100000, 'クリスマス-', FALSE),
					    (11, 3, '2025-12-22', '出金', '昼食', 900, '定食', FALSE),
					    (12, 4, '2025-12-22', '入金', '副業', 12000, '', FALSE),
					    (13, 5, '2025-12-22', '出金', '交通費', 1500, '', FALSE)
					""");

			/* ===== KAKEIBO_HISTORY 初期データ ===== */
			stmt.executeUpdate(
					"""
							INSERT INTO KAKEIBO_HISTORY
							(ID, KAKEIBO_ID, USER_ID,
							 BEFORE_DATE, AFTER_DATE,
							 BEFORE_TYPE, AFTER_TYPE,
							 BEFORE_ITEM, AFTER_ITEM,
							 BEFORE_AMOUNT, AFTER_AMOUNT,
							 BEFORE_MEMO, AFTER_MEMO, UPDATED_AT)
							VALUES
							(1, 1, 1, '2025-12-16', '2025-12-16', '出金', '出金', '昼食', '昼食', 800, 800, 'ラーメン', '塩ラーメン', '2025-12-19 18:05:35.51421'),
							(2, 1, 1, '2025-12-16', '2025-12-16', '出金', '出金', '昼食', '昼食', 800, 800, '塩ラーメン', 'ラーメン', '2025-12-19 18:10:37.467747'),
							(3, 3, 1, '2025-12-16', '2025-12-16', '入金', '入金', 'たこやき', 'たこやき', 600, 800, NULL, NULL, '2025-12-19 18:44:20.191183'),
							(4, 3, 1, '2025-12-16', '2025-12-16', '入金', '入金', 'たこやき', 'たこやき', 800, 1000, NULL, NULL, '2025-12-19 18:52:24.93909'),
							(5, 4, 1, '2025-12-19', '2025-12-19', '出金', '出金', '夕食', '夕食', 150, 2500, 'おにぎり', 'ハンバーグセット', '2025-12-19 19:07:47.229375'),
							(6, 5, 2, '2025-12-19', '2025-12-19', '入金', '入金', '報酬', '報酬', 100000000, 100000001, NULL, NULL, '2025-12-19 22:11:43.454003'),
							(7, 3, 1, '2025-12-16', NULL, '入金', NULL, 'たこやき', NULL, 1000, NULL, NULL, NULL, '2025-12-20 14:58:42.114437'),
							(8, 1, 1, '2025-12-16', NULL, '出金', NULL, '昼食', NULL, 800, NULL, 'ラーメン', NULL, '2025-12-20 15:07:39.817325'),
							(9, 7, 1, '2025-12-20', NULL, '入金', NULL, 'お小遣い', NULL, 150, NULL, NULL, NULL, '2025-12-20 15:11:32.532523'),
							(10, 8, 1, '2025-12-20', NULL, '入金', NULL, '宝くじ', NULL, 5000000, NULL, NULL, NULL, '2025-12-20 16:41:50.088466'),
							(11, 8, 1, '2025-12-20', '2025-12-20', '入金', '入金', '宝くじ', '宝くじ', 5000000, 5000000, NULL, NULL, '2025-12-20 16:55:06.215089'),
							(12, 9, 2, '2025-12-20', NULL, '出金', NULL, 'トレーニング', NULL, 15000, NULL, NULL, NULL, '2025-12-20 17:31:33.308223'),
							(13, 9, 2, '2025-12-20', '2025-12-20', '出金', '出金', 'トレーニング', 'トレーニング', 15000, 15000, NULL, NULL, '2025-12-20 17:31:41.223625'),
							(14, 8, 1, '2025-12-20', NULL, '入金', NULL, '宝くじ', NULL, 5000000, NULL, NULL, NULL, '2025-12-20 17:50:22.279648'),
							(15, 8, 1, '2025-12-20', '2025-12-20', '入金', '入金', '宝くじ', '宝くじ', 5000000, 5000000, NULL, NULL, '2025-12-20 17:50:25.795471'),
							(16, 8, 1, '2025-12-20', NULL, '入金', NULL, '宝くじ', NULL, 5000000, NULL, NULL, NULL, '2025-12-20 17:56:10.451665'),
							(17, 8, 1, '2025-12-20', '2025-12-20', '入金', '入金', '宝くじ', '宝くじ', 5000000, 5000000, NULL, NULL, '2025-12-20 19:16:12.088592'),
							(18, 10, 1, '2025-12-21', '2025-12-21', '出金', '出金', 'switch2', 'switch2', 100000, 100000, 'クリスマス', 'クリスマス-', '2025-12-21 17:37:39.088322')
							""");

			conn.commit();

		} catch (Exception e) {
			throw new RuntimeException("デモデータ初期化に失敗しました", e);
		}
	}
}
