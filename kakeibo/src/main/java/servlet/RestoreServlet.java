package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.KakeiboDao;

@WebServlet("/restore")
public class RestoreServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("loginUserId") == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		Integer userId = (Integer) session.getAttribute("loginUserId");

		// ★ デモ判定
		boolean isDemo = Boolean.TRUE.equals(session.getAttribute("IS_DEMO"));
		String dbName = isDemo ? "kakeibo_demo" : "kakeibo";

		try {
			int id = Integer.parseInt(request.getParameter("id"));

			KakeiboDao dao = new KakeiboDao(dbName);
			dao.restore(id, userId);

			response.sendRedirect(request.getContextPath() + "/list");

		} catch (NumberFormatException e) {
			request.setAttribute("error", "不正なIDです");
			request.getRequestDispatcher("/list").forward(request, response);

		} catch (Exception e) {
			request.setAttribute("error", "復元処理に失敗しました");
			request.getRequestDispatcher("/list").forward(request, response);
		}
	}
}
