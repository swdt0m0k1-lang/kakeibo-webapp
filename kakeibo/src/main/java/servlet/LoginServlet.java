package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.UserDao;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	// ログイン画面表示
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
				.forward(request, response);
	}

	// ログイン処理
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String mode = request.getParameter("mode"); // ★ 追加

		// 入力チェック
		if (username == null || username.isBlank()
				|| password == null || password.isBlank()) {

			request.setAttribute("error", "ユーザー名とパスワードを入力してください");
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
					.forward(request, response);
			return;
		}

		// ★ 使用するDBを決定
		String dbName;
		if ("demo".equals(mode)) {
			dbName = "kakeibo_demo";
		} else {
			dbName = "kakeibo";
		}

		// ★ DAO生成（セッションを使う前提）
		UserDao dao = new UserDao(dbName);
		Integer userId = dao.findUserId(username, password, dbName);

		if (userId != null) {

			// 既存セッション破棄 → 新規作成
			HttpSession oldSession = request.getSession(false);
			if (oldSession != null) {
				oldSession.invalidate();
			}
			HttpSession session = request.getSession(true);

			// ★ セッションに保存
			session.setAttribute("loginUserId", userId);
			session.setAttribute("loginUserName", username);
			session.setAttribute("DB_NAME", dbName);

			// ★ デモ判定フラグを追加
			boolean isDemo = "kakeibo_demo".equals(dbName);
			session.setAttribute("IS_DEMO", isDemo);

			response.sendRedirect(request.getContextPath() + "/list");

		} else {
			request.setAttribute("error", "ユーザー名またはパスワードが違います");
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
					.forward(request, response);
		}
	}
}
