package servlet;

import java.io.IOException;
import java.sql.SQLException;

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

	private KakeiboDao dao = new KakeiboDao();
	private KakeiboHistoryDao historyDao = new KakeiboHistoryDao();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("loginUserId") == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		int userId = (int) session.getAttribute("loginUserId");
		String userName = (String) session.getAttribute("loginUserName");

		//ユーザー名
		request.setAttribute("userName", userName);
		// 家計簿一覧
		request.setAttribute("list", dao.findByUserId(userId));

		// ★ 変更履歴一覧（全体）← 常に表示したい
		request.setAttribute(
				"historyList",
				historyDao.findByUserId(userId));

		// 編集対象
		String editIdStr = request.getParameter("editId");
		if (editIdStr != null) {
			int editId = Integer.parseInt(editIdStr);

			// 編集データ
			Kakeibo edit = dao.findById(editId, userId);
			request.setAttribute("edit", edit);

			// ★ その行だけの履歴（別名！）
			try {
				request.setAttribute(
						"rowHistoryList",
						historyDao.findByKakeiboId(editId));
			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		request.getRequestDispatcher("/WEB-INF/jsp/list.jsp")
				.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		int userId = (int) session.getAttribute("loginUserId");

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
			// 更新（履歴保存は Dao 側で実施）
			k.setId(Integer.parseInt(id));
			dao.update(k);
		}

		// PRGパターン（二重送信防止）
		response.sendRedirect(request.getContextPath() + "/list");
	}
}
