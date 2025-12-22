package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String contextPath = request.getContextPath();
		String uri = request.getRequestURI();

		HttpSession session = request.getSession(false);
		boolean loggedIn = (session != null && session.getAttribute("loginUserId") != null);

		// ログイン不要パス
		boolean loginRequest = uri.equals(contextPath + "/login")
				|| uri.equals(contextPath + "/login.jsp")
				|| uri.equals(contextPath + "/register")
				|| uri.equals(contextPath + "/test")
				|| uri.equals(contextPath + "/demo");
		

		boolean staticResource = uri.startsWith(contextPath + "/css/")
				|| uri.startsWith(contextPath + "/js/")
				|| uri.startsWith(contextPath + "/images/");

		if (loggedIn || loginRequest || staticResource) {
			chain.doFilter(req, res);
		} else {
			response.sendRedirect(contextPath + "/login");
		}
	}
}
