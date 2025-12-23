package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.KakeiboDao;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		var session = request.getSession(false);
		if (session == null || session.getAttribute("loginUserId") == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		Integer userId = (Integer) session.getAttribute("loginUserId");

		// ★ デモ判定
		boolean isDemo = Boolean.TRUE.equals(session.getAttribute("IS_DEMO"));

		// ★ 使用するDBを切り替える
		// 通常: ~/kakeibo.mv.db
		// デモ: ~/kakeibo_demo.mv.db
		String dbName = isDemo ? "kakeibo_demo" : "kakeibo";

		try {
			int id = Integer.parseInt(request.getParameter("id"));

			KakeiboDao dao = new KakeiboDao(dbName);
			dao.delete(id, userId); // ← 論理削除＋履歴

		} catch (NumberFormatException e) {
			throw new ServletException("不正なIDです", e);
		} catch (Exception e) {
			throw new ServletException("削除処理に失敗しました", e);
		}

		response.sendRedirect(request.getContextPath() + "/list");
	}
}
