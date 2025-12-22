package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Kakeibo;

public class KakeiboDao {

	private String dbName;

	public KakeiboDao(String dbName) {
		this.dbName = dbName;
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:h2:~/" + dbName, "sa", "");
	}

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

		try (Connection conn = getConnection();
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
	 * 全件取得（テスト・管理用）
	 */
	public List<Kakeibo> findAll() {

		List<Kakeibo> list = new ArrayList<>();

		String sql = """
				    SELECT id, user_id, k_date, type, item, amount, memo
				    FROM kakeibo
				    ORDER BY k_date DESC, id DESC
				""";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

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

		} catch (Exception e) {
			throw new RuntimeException("家計簿全件取得に失敗しました", e);
		}

		return list;
	}

	/**
	 * 新規登録
	 */
	public void insert(Kakeibo k) {

		String sql = """
				    INSERT INTO kakeibo
				    (user_id, k_date, type, item, amount, memo)
				    VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = getConnection();
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
	 * 更新（履歴あり）
	 */
	public void update(Kakeibo k) {

		String sql = """
				    UPDATE kakeibo
				    SET k_date = ?, type = ?, item = ?, amount = ?, memo = ?
				    WHERE id = ? AND user_id = ?
				""";

		try (Connection conn = getConnection()) {

			conn.setAutoCommit(false);

			try {
				Kakeibo before = findById(conn, k.getId(), k.getUserId());
				if (before == null) {
					throw new RuntimeException("更新対象が存在しません");
				}

				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setDate(1, k.getDate());
					ps.setString(2, k.getType());
					ps.setString(3, k.getItem());
					ps.setInt(4, k.getAmount());
					ps.setString(5, k.getMemo());
					ps.setInt(6, k.getId());
					ps.setInt(7, k.getUserId());
					ps.executeUpdate();
				}

				new KakeiboHistoryDao(dbName).insert(conn, before, k);

				conn.commit();

			} catch (Exception e) {
				conn.rollback();
				throw e;
			}

		} catch (Exception e) {
			throw new RuntimeException("家計簿更新に失敗しました", e);
		}
	}

	/**
	 * Servlet 用 findById
	 */
	public Kakeibo findById(int id, int userId) {
		try (Connection conn = getConnection()) {
			return findById(conn, id, userId);
		} catch (Exception e) {
			throw new RuntimeException("家計簿取得に失敗しました", e);
		}
	}

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

	/**
	 * 削除（論理削除 + 履歴）
	 */
	public void delete(int id, int userId) {

		String sql = """
				    UPDATE kakeibo
				    SET deleted = TRUE
				    WHERE id = ? AND user_id = ?
				""";

		try (Connection conn = getConnection()) {

			conn.setAutoCommit(false);

			try {
				Kakeibo before = findById(conn, id, userId);
				if (before == null) {
					throw new RuntimeException("削除対象が存在しません");
				}

				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, id);
					ps.setInt(2, userId);
					ps.executeUpdate();
				}

				new KakeiboHistoryDao(dbName).insertDeletion(conn, before);

				conn.commit();

			} catch (Exception e) {
				conn.rollback();
				throw e;
			}

		} catch (Exception e) {
			throw new RuntimeException("家計簿削除に失敗しました", e);
		}
	}

	/**
	 * 復元
	 */
	public void restore(int id, int userId) {

		String sql = """
				    UPDATE kakeibo
				    SET deleted = FALSE
				    WHERE id = ? AND user_id = ?
				""";

		try (Connection conn = getConnection()) {

			conn.setAutoCommit(false);

			try {
				Kakeibo before = findById(conn, id, userId);
				if (before == null) {
					throw new RuntimeException("復元対象が存在しません");
				}

				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setInt(1, id);
					ps.setInt(2, userId);
					ps.executeUpdate();
				}

				new KakeiboHistoryDao(dbName).insertRestore(conn, before);

				conn.commit();

			} catch (Exception e) {
				conn.rollback();
				throw e;
			}

		} catch (Exception e) {
			throw new RuntimeException("家計簿復元に失敗しました", e);
		}
	}
}
