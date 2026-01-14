package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.KakeiboDao;
import dao.KakeiboHistoryDao;
import model.Kakeibo;

@WebServlet("/list")
public class ListServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("loginUserId") == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		Integer userId = (Integer) session.getAttribute("loginUserId");
		String userName = (String) session.getAttribute("loginUserName");

		// ★ DB 名取得（demo / 本番）
		String dbName = (String) session.getAttribute("DB_NAME");
		if (dbName == null) {
			dbName = "kakeibo";
		}

		// DAO生成（★ここ重要）
		KakeiboDao dao = new KakeiboDao(dbName);
		KakeiboHistoryDao historyDao = new KakeiboHistoryDao(dbName);

		// ユーザー名
		request.setAttribute("userName", userName);

		// 家計簿一覧
		List<Kakeibo> list = dao.findByUserId(userId);
		request.setAttribute("list", list);

		// 変更履歴一覧（ユーザー単位）
		request.setAttribute(
				"historyList",
				historyDao.findByUserId(userId));

		// 編集対象
		String editIdStr = request.getParameter("editId");
		if (editIdStr != null && !editIdStr.isBlank()) {
			int editId = Integer.parseInt(editIdStr);

			// 編集データ
			Kakeibo edit = dao.findById(editId, userId);
			request.setAttribute("edit", edit);

			request.setAttribute(
					"rowHistoryList",
					historyDao.findByKakeiboId(editId));
		}

		request.getRequestDispatcher("/WEB-INF/jsp/list.jsp")
				.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		Integer userId = (Integer) session.getAttribute("loginUserId");

		String dbName = (String) session.getAttribute("DB_NAME");
		if (dbName == null) {
			dbName = "kakeibo";
		}

		KakeiboDao dao = new KakeiboDao(dbName);

		Kakeibo k = new Kakeibo();
		k.setUserId(userId);
		k.setDate(java.sql.Date.valueOf(request.getParameter("date")));
		k.setType(request.getParameter("type"));
		k.setItem(request.getParameter("item"));
		k.setAmount(Integer.parseInt(request.getParameter("amount")));
		k.setMemo(request.getParameter("memo"));

		String id = request.getParameter("id");

		if (id == null || id.isEmpty()) {
			// 新規登録
			dao.insert(k);
		} else {
			// 更新（履歴保存は Dao 側）
			k.setId(Integer.parseInt(id));
			dao.update(k);
		}

		// PRGパターン
		response.sendRedirect(request.getContextPath() + "/list");
	}
}
