package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.KakeiboHistoryDao;
import model.KakeiboHistory;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		// ログインチェック
		if (session == null || session.getAttribute("loginUserId") == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		int userId = (int) session.getAttribute("loginUserId");

		// ★ 使用DB名を取得（デモ / 通常）
		String dbName = (String) session.getAttribute("DB_NAME");
		if (dbName == null) {
			dbName = "kakeibo"; // 念のための保険
		}

		// ★ DAO生成（ここで切替）
		KakeiboHistoryDao historyDao = new KakeiboHistoryDao(dbName);

		List<KakeiboHistory> historyList = historyDao.findByUserId(userId);

		request.setAttribute("historyList", historyList);

		request.getRequestDispatcher("/WEB-INF/jsp/history.jsp")
				.forward(request, response);
	}
}
