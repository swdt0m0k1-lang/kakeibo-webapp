package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.UserDao;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/WEB-INF/jsp/register.jsp")
				.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// ★ セッションからDB名取得
		String dbName = "kakeibo";
		if (request.getSession(false) != null &&
				request.getSession(false).getAttribute("DB_NAME") != null) {
			dbName = (String) request.getSession(false).getAttribute("DB_NAME");
		}

		UserDao dao = new UserDao(dbName);

		if (dao.exists(username)) {
			request.setAttribute("error", "すでに存在するユーザー名です");
			request.getRequestDispatcher("/WEB-INF/jsp/register.jsp")
					.forward(request, response);
			return;
		}

		dao.insert(username, password);

		response.sendRedirect(request.getContextPath() + "/login");
	}
}
