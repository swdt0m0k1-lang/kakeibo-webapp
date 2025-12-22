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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // セッション取得
        var session = request.getSession(false);
        if (session == null || session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // ★ デモモード判定
        //if (Boolean.TRUE.equals(session.getAttribute("IS_DEMO"))) {
        //    request.setAttribute("error", "デモモードでは削除できません");
        //    request.getRequestDispatcher("/list").forward(request, response);
        //    return;
        //}

        Integer userId = (Integer) session.getAttribute("loginUserId");

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            KakeiboDao kakeiboDao = new KakeiboDao("kakeibo");
            kakeiboDao.delete(id, userId);

        } catch (NumberFormatException e) {
            throw new ServletException("不正なIDです", e);
        } catch (Exception e) {
            throw new ServletException("削除処理に失敗しました", e);
        }

        response.sendRedirect(request.getContextPath() + "/list");
    }
}