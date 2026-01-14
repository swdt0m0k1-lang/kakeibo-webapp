package test.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import test.util.DemoDataInitializer;

@WebServlet("/demo/reset")
public class DemoResetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // セッションなし or デモモードでない場合は拒否
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("IS_DEMO"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "デモ環境ではありません");
            return;
        }

        try {
            // デモデータ初期化
            DemoDataInitializer initializer = new DemoDataInitializer();
            initializer.reset();

        } catch (Exception e) {
            throw new ServletException("デモデータのリセットに失敗しました", e);
        }

        // デモトップ or 一覧へ戻す
        response.sendRedirect(request.getContextPath() + "/test");
    }
}
