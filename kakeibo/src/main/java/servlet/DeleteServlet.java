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

	private KakeiboDao kakeiboDao = new KakeiboDao();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// セッションからログインユーザーID取得
		Integer userId = (Integer) request.getSession().getAttribute("loginUserId");
		if (userId == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		try {
			int id = Integer.parseInt(request.getParameter("id"));

			// 削除実行（履歴も同時に記録）
			kakeiboDao.delete(id, userId);

		} catch (NumberFormatException e) {
			throw new ServletException("不正なIDです", e);
		} catch (Exception e) {
			throw new ServletException("削除処理に失敗しました", e);
		}

		response.sendRedirect(request.getContextPath() + "/list");
	}
}
