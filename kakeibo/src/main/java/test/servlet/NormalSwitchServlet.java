package test.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/normal")
public class NormalSwitchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            // ★ デモ情報を完全に解除
            session.removeAttribute("IS_DEMO");
            session.removeAttribute("DB_NAME");
        }

        // 通常の test へ
        response.sendRedirect(request.getContextPath() + "/test");
    }
}
