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

    // 登録画面表示
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/jsp/register.jsp")
               .forward(request, response);
    }

    // 登録処理
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDao dao = new UserDao();

        if (dao.exists(username)) {
            request.setAttribute("error", "すでに存在するユーザー名です");
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp")
                   .forward(request, response);
            return;
        }

        dao.insert(username, password);

        // 登録後はログイン画面へ
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
