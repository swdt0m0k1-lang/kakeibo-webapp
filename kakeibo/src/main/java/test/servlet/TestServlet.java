package test.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.KakeiboDao;
import dao.KakeiboHistoryDao;
import dao.UserDao;
import model.Kakeibo;
import model.KakeiboHistory;
import model.User;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    private UserDao userDao = new UserDao();
    private KakeiboDao kakeiboDao = new KakeiboDao();
    private KakeiboHistoryDao historyDao = new KakeiboHistoryDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 登録者一覧
        List<User> users = userDao.findAll();
        request.setAttribute("users", users);

        // 家計簿一覧
        List<Kakeibo> kakeibos = kakeiboDao.findAll();
        request.setAttribute("kakeibos", kakeibos);

        // 修正ログ一覧
        List<KakeiboHistory> histories = historyDao.findAll();
        request.setAttribute("histories", histories);

        request.getRequestDispatcher("/WEB-INF/test/test.jsp")
               .forward(request, response);
    }
}