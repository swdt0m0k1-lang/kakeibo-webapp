package test.servlet;

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
import dao.UserDao;
import model.Kakeibo;
import model.KakeiboHistory;
import model.User;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // セッション取得（ログインしていなくてもOK）
        HttpSession session = request.getSession(false);

        // ★ 使用DB名を決定
        String dbName = "kakeibo";
        if (session != null && session.getAttribute("DB_NAME") != null) {
            dbName = (String) session.getAttribute("DB_NAME");
        }

        // ★ DAO生成（DB切替対応）
        UserDao userDao = new UserDao(dbName);
        KakeiboDao kakeiboDao = new KakeiboDao(dbName);
        KakeiboHistoryDao historyDao = new KakeiboHistoryDao(dbName);

        // 登録者一覧
        List<User> users = userDao.findAll();
        request.setAttribute("users", users);

        // 家計簿一覧
        List<Kakeibo> kakeibos = kakeiboDao.findAll();
        request.setAttribute("kakeibos", kakeibos);

        // 修正ログ一覧
        List<KakeiboHistory> histories = historyDao.findAll();
        request.setAttribute("histories", histories);

        // デモ判定（表示用）
        boolean isDemo = session != null && Boolean.TRUE.equals(session.getAttribute("IS_DEMO"));
        request.setAttribute("isDemo", isDemo);

        request.getRequestDispatcher("/WEB-INF/test/test.jsp")
                .forward(request, response);
    }
}
