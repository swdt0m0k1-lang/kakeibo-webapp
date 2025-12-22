package test.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/demo")
public class DemoSwitchServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(true);

		String mode = request.getParameter("mode");

		if ("on".equals(mode)) {
			// ===== デモモードON =====
			session.setAttribute("IS_DEMO", true);
			session.setAttribute("DB_NAME", "kakeibo_demo");

		} else if ("off".equals(mode)) {
			// ===== 通常モードへ戻す =====
			session.removeAttribute("IS_DEMO");
			session.setAttribute("DB_NAME", "kakeibo");
		}

		response.sendRedirect(request.getContextPath() + "/test");
	}
}
