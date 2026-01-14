package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;

public class UserDao {

	private String dbName;

	private static final String DB_USER = "sa";
	private static final String DB_PASS = "";

	public UserDao(String dbName) {
		this.dbName = dbName;
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("H2 Driver not found", e);
		}
	}

	private Connection getConnection() throws SQLException {
		String url = "jdbc:h2:~/" + dbName;
		return DriverManager.getConnection(url, DB_USER, DB_PASS);
	}

	/**
	 * ログイン認証
	 * @return 認証成功なら User、失敗なら null
	 */
	public User login(String username, String password) {
		String sql = "SELECT id, username FROM users WHERE username=? AND password=?";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);
			ps.setString(2, password);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new User(rs.getInt("id"), rs.getString("username"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** ユーザー存在チェック */
	public boolean exists(String username) {
		String sql = "SELECT 1 FROM users WHERE username=?";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 新規ユーザー登録 */
	public void insert(String username, String password) {
		String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);
			ps.setString(2, password);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** ユーザーID取得 */
	public Integer findUserId(String username, String password, String dbName) {

		String url = "jdbc:h2:~/" + dbName;

		try (Connection con = DriverManager.getConnection(url, "sa", "");
				PreparedStatement ps = con.prepareStatement(
						"SELECT id FROM USERS WHERE USERNAME = ? AND PASSWORD = ?")) {

			ps.setString(1, username);
			ps.setString(2, password);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/** 全ユーザー取得 */
	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		String sql = "SELECT id, username FROM users ORDER BY id";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				User u = new User();
				u.setId(rs.getInt("id"));
				u.setUsername(rs.getString("username"));
				list.add(u);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}
