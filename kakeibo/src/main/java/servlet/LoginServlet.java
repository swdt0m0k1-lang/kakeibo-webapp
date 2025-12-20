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

        // 入力チェック
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {

            request.setAttribute("error", "ユーザー名とパスワードを入力してください");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
                   .forward(request, response);
            return;
        }

        UserDao dao = new UserDao();
        Integer userId = dao.findUserId(username, password);

        if (userId != null) {

            // 既存セッション破棄 → 新規作成
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);

            session.setAttribute("loginUserId", userId);
            session.setAttribute("loginUserName", username);

            response.sendRedirect(request.getContextPath() + "/list");

        } else {
            request.setAttribute("error", "ユーザー名またはパスワードが違います");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
                   .forward(request, response);
        }
    }
}
