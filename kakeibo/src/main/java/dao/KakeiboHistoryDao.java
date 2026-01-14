package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Kakeibo;
import model.KakeiboHistory;

public class KakeiboHistoryDao {

	private String dbName;

	public KakeiboHistoryDao(String dbName) {
		this.dbName = dbName;
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				"jdbc:h2:~/" + dbName, "sa", "");
	}

	/**
	 * 修正履歴一覧（user_id 指定）
	 */
	public List<KakeiboHistory> findByUserId(int userId) {

		List<KakeiboHistory> list = new ArrayList<>();

		String sql = """
				    SELECT
				        id,
				        kakeibo_id,
				        user_id,
				        before_date, after_date,
				        before_type, after_type,
				        before_item, after_item,
				        before_amount, after_amount,
				        before_memo, after_memo,
				        updated_at
				    FROM kakeibo_history
				    WHERE user_id = ?
				    ORDER BY updated_at DESC
				""";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					KakeiboHistory h = new KakeiboHistory();
					h.setId(rs.getInt("id"));
					h.setKakeiboId(rs.getInt("kakeibo_id"));
					h.setUserId(rs.getInt("user_id"));
					h.setBeforeDate(rs.getDate("before_date"));
					h.setAfterDate(rs.getDate("after_date"));
					h.setBeforeType(rs.getString("before_type"));
					h.setAfterType(rs.getString("after_type"));
					h.setBeforeItem(rs.getString("before_item"));
					h.setAfterItem(rs.getString("after_item"));
					h.setBeforeAmount(rs.getInt("before_amount"));
					h.setAfterAmount(rs.getInt("after_amount"));
					h.setBeforeMemo(rs.getString("before_memo"));
					h.setAfterMemo(rs.getString("after_memo"));
					h.setUpdatedAt(rs.getTimestamp("updated_at"));
					list.add(h);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("履歴一覧取得に失敗しました", e);
		}

		return list;
	}

	/**
	 * 家計簿ID単位の履歴取得
	 */
	public List<KakeiboHistory> findByKakeiboId(int kakeiboId) {

		List<KakeiboHistory> list = new ArrayList<>();

		String sql = """
				    SELECT *
				    FROM kakeibo_history
				    WHERE kakeibo_id = ?
				    ORDER BY updated_at DESC
				""";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, kakeiboId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					KakeiboHistory h = new KakeiboHistory();
					h.setId(rs.getInt("id"));
					h.setKakeiboId(rs.getInt("kakeibo_id"));
					h.setUserId(rs.getInt("user_id"));
					h.setBeforeDate(rs.getDate("before_date"));
					h.setAfterDate(rs.getDate("after_date"));
					h.setBeforeType(rs.getString("before_type"));
					h.setAfterType(rs.getString("after_type"));
					h.setBeforeItem(rs.getString("before_item"));
					h.setAfterItem(rs.getString("after_item"));
					h.setBeforeAmount(rs.getInt("before_amount"));
					h.setAfterAmount(rs.getInt("after_amount"));
					h.setBeforeMemo(rs.getString("before_memo"));
					h.setAfterMemo(rs.getString("after_memo"));
					h.setUpdatedAt(rs.getTimestamp("updated_at"));
					list.add(h);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("履歴取得に失敗しました", e);
		}

		return list;
	}

	/**
	 * 全件取得（テスト・管理用）
	 */
	public List<KakeiboHistory> findAll() {

		List<KakeiboHistory> list = new ArrayList<>();

		String sql = """
				    SELECT
				        id,
				        kakeibo_id,
				        user_id,
				        before_date, after_date,
				        before_type, after_type,
				        before_item, after_item,
				        before_amount, after_amount,
				        before_memo, after_memo,
				        updated_at
				    FROM kakeibo_history
				    ORDER BY updated_at DESC
				""";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				KakeiboHistory h = new KakeiboHistory();
				h.setId(rs.getInt("id"));
				h.setKakeiboId(rs.getInt("kakeibo_id"));
				h.setUserId(rs.getInt("user_id"));
				h.setBeforeDate(rs.getDate("before_date"));
				h.setAfterDate(rs.getDate("after_date"));
				h.setBeforeType(rs.getString("before_type"));
				h.setAfterType(rs.getString("after_type"));
				h.setBeforeItem(rs.getString("before_item"));
				h.setAfterItem(rs.getString("after_item"));
				h.setBeforeAmount(rs.getInt("before_amount"));
				h.setAfterAmount(rs.getInt("after_amount"));
				h.setBeforeMemo(rs.getString("before_memo"));
				h.setAfterMemo(rs.getString("after_memo"));
				h.setUpdatedAt(rs.getTimestamp("updated_at"));
				list.add(h);
			}

		} catch (Exception e) {
			throw new RuntimeException("履歴全件取得に失敗しました", e);
		}

		return list;
	}

	/**
	 * 更新履歴保存（update 用）
	 */
	public void insert(Connection conn, Kakeibo before, Kakeibo after) {

		String sql = """
				    INSERT INTO kakeibo_history (
				        kakeibo_id,
				        user_id,
				        before_date, after_date,
				        before_type, after_type,
				        before_item, after_item,
				        before_amount, after_amount,
				        before_memo, after_memo
				    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, before.getId());
			ps.setInt(2, before.getUserId());
			ps.setDate(3, before.getDate());
			ps.setDate(4, after.getDate());
			ps.setString(5, before.getType());
			ps.setString(6, after.getType());
			ps.setString(7, before.getItem());
			ps.setString(8, after.getItem());
			ps.setInt(9, before.getAmount());
			ps.setInt(10, after.getAmount());
			ps.setString(11, before.getMemo());
			ps.setString(12, after.getMemo());

			ps.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("履歴保存に失敗しました", e);
		}
	}

	/**
	 * 削除履歴保存
	 */
	public void insertDeletion(Connection conn, Kakeibo before) {

		String sql = """
				    INSERT INTO kakeibo_history (
				        kakeibo_id,
				        user_id,
				        before_date,
				        before_type,
				        before_item,
				        before_amount,
				        before_memo,
				        after_date,
				        after_type,
				        after_item,
				        after_amount,
				        after_memo,
				        updated_at
				    ) VALUES (?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, before.getId());
			ps.setInt(2, before.getUserId());
			ps.setDate(3, before.getDate());
			ps.setString(4, before.getType());
			ps.setString(5, before.getItem());
			ps.setInt(6, before.getAmount());
			ps.setString(7, before.getMemo());

			ps.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("削除履歴の保存に失敗しました", e);
		}
	}

	/**
	 * 復元履歴保存
	 */
	public void insertRestore(Connection conn, Kakeibo before) {

		String sql = """
				    INSERT INTO kakeibo_history (
				        kakeibo_id,
				        user_id,
				        before_date,
				        before_type,
				        before_item,
				        before_amount,
				        before_memo,
				        after_date,
				        after_type,
				        after_item,
				        after_amount,
				        after_memo,
				        updated_at
				    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, before.getId());
			ps.setInt(2, before.getUserId());

			ps.setDate(3, before.getDate());
			ps.setString(4, before.getType());
			ps.setString(5, before.getItem());
			ps.setInt(6, before.getAmount());
			ps.setString(7, before.getMemo());

			ps.setDate(8, before.getDate());
			ps.setString(9, before.getType());
			ps.setString(10, before.getItem());
			ps.setInt(11, before.getAmount());
			ps.setString(12, before.getMemo());

			ps.executeUpdate();

		} catch (Exception e) {
			throw new RuntimeException("復元履歴の保存に失敗しました", e);
		}
	}
}
