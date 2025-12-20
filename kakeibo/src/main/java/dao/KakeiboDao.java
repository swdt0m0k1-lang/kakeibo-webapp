package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Kakeibo;

public class KakeiboDao {

	private static final String JDBC_URL = "jdbc:h2:~/kakeibo";
	private static final String DB_USER = "sa";
	private static final String DB_PASS = "";

	/**
	 * user_id に紐づく家計簿一覧取得
	 */
	public List<Kakeibo> findByUserId(int userId) {

		List<Kakeibo> list = new ArrayList<>();

		String sql = """
				    SELECT id, user_id, k_date, type, item, amount, memo
				    FROM kakeibo
				    WHERE user_id = ?
				      AND deleted = FALSE
				    ORDER BY k_date DESC
				""";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Kakeibo k = new Kakeibo();
					k.setId(rs.getInt("id"));
					k.setUserId(rs.getInt("user_id"));
					k.setDate(rs.getDate("k_date"));
					k.setType(rs.getString("type"));
					k.setItem(rs.getString("item"));
					k.setAmount(rs.getInt("amount"));
					k.setMemo(rs.getString("memo"));
					list.add(k);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("家計簿一覧取得に失敗しました", e);
		}

		return list;
	}

	/**
	 * 家計簿 新規登録
	 */
	public void insert(Kakeibo k) {

		String sql = """
				    INSERT INTO kakeibo
				    (user_id, k_date, type, item, amount, memo)
				    VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, k.getUserId());
			ps.setDate(2, k.getDate());
			ps.setString(3, k.getType());
			ps.setString(4, k.getItem());
			ps.setInt(5, k.getAmount());
			ps.setString(6, k.getMemo());

			ps.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("家計簿登録に失敗しました", e);
		}
	}

	/**
	 * 家計簿 更新（履歴保存あり）
	 */
	public void update(Kakeibo k) {

		String updateSql = """
				    UPDATE kakeibo
				    SET k_date = ?, type = ?, item = ?, amount = ?, memo = ?
				    WHERE id = ? AND user_id = ?
				""";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {

			conn.setAutoCommit(false);

			try {
				// ① 更新前データ取得
				Kakeibo before = findById(conn, k.getId(), k.getUserId());
				if (before == null) {
					throw new RuntimeException("更新対象が存在しません");
				}

				// ② 更新
				try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
					ps.setDate(1, k.getDate());
					ps.setString(2, k.getType());
					ps.setString(3, k.getItem());
					ps.setInt(4, k.getAmount());
					ps.setString(5, k.getMemo());
					ps.setInt(6, k.getId());
					ps.setInt(7, k.getUserId());
					ps.executeUpdate();
				}

				// ③ 履歴保存
				KakeiboHistoryDao historyDao = new KakeiboHistoryDao();
				historyDao.insert(conn, before, k);

				conn.commit();

			} catch (Exception e) {
				conn.rollback();
				throw new RuntimeException("家計簿更新処理中にエラーが発生しました", e);
			}

		} catch (Exception e) {
			throw new RuntimeException("家計簿更新に失敗しました", e);
		}
	}

	/**
	 * Servlet 用（Connection を意識させない）
	 */
	public Kakeibo findById(int id, int userId) {

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
			return findById(conn, id, userId);
		} catch (Exception e) {
			throw new RuntimeException("家計簿取得に失敗しました", e);
		}
	}

	/**
	 * update 内部専用
	 */
	private Kakeibo findById(Connection conn, int id, int userId) {

		String sql = """
				    SELECT id, user_id, k_date, type, item, amount, memo
				    FROM kakeibo
				    WHERE id = ? AND user_id = ?
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			ps.setInt(2, userId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Kakeibo k = new Kakeibo();
					k.setId(rs.getInt("id"));
					k.setUserId(rs.getInt("user_id"));
					k.setDate(rs.getDate("k_date"));
					k.setType(rs.getString("type"));
					k.setItem(rs.getString("item"));
					k.setAmount(rs.getInt("amount"));
					k.setMemo(rs.getString("memo"));
					return k;
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	// -------------------------------
	// 新規追加：全家計簿取得（テスト用）
	public List<Kakeibo> findAll() {
		List<Kakeibo> list = new ArrayList<>();
		String sql = "SELECT id, user_id, k_date, type, item, amount, memo FROM kakeibo";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Kakeibo k = new Kakeibo();
				k.setId(rs.getInt("id"));
				k.setUserId(rs.getInt("user_id"));
				k.setDate(rs.getDate("k_date")); // ← date ではなく k_date
				k.setType(rs.getString("type"));
				k.setItem(rs.getString("item"));
				k.setAmount(rs.getInt("amount"));
				k.setMemo(rs.getString("memo"));
				list.add(k);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public void delete(int id, int userId) {

		String sql = """
				    UPDATE kakeibo
				    SET deleted = TRUE
				    WHERE id = ? AND user_id = ?
				""";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
			conn.setAutoCommit(false);

			try {
				// ① 削除前データ取得
				Kakeibo before = findById(conn, id, userId);
				if (before == null) {
					throw new RuntimeException("削除対象が存在しません");
				}

				// ② deleted = TRUE に更新
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, id);
					ps.setInt(2, userId);
					ps.executeUpdate();
				}

				// ③ 履歴に削除情報を保存
				KakeiboHistoryDao historyDao = new KakeiboHistoryDao();
				historyDao.insertDeletion(conn, before);

				conn.commit();

			} catch (Exception e) {
				conn.rollback();
				throw new RuntimeException("家計簿削除処理中にエラーが発生しました", e);
			}

		} catch (Exception e) {
			throw new RuntimeException("家計簿削除に失敗しました", e);
		}
	}

	public void restore(int id, int userId) {

		String sql = """
				    UPDATE kakeibo
				    SET deleted = FALSE
				    WHERE id = ? AND user_id = ?
				""";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
			conn.setAutoCommit(false);

			try {
				// ① 復元前データ取得（deleted = TRUE の状態）
				Kakeibo before = findById(conn, id, userId);
				if (before == null) {
					throw new RuntimeException("復元対象が存在しません");
				}

				// ② 復元
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, id);
					ps.setInt(2, userId);
					ps.executeUpdate();
				}

				// ③ 履歴に「復元」を記録
				KakeiboHistoryDao historyDao = new KakeiboHistoryDao();
				historyDao.insertRestore(conn, before);

				conn.commit();

			} catch (Exception e) {
				conn.rollback();
				throw new RuntimeException("復元処理中にエラーが発生しました", e);
			}

		} catch (Exception e) {
			throw new RuntimeException("家計簿復元に失敗しました", e);
		}
	}

}
