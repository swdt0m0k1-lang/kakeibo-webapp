<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ログイン</title>
</head>
<body>

	<h2>ログイン</h2>

	<%-- ログアウトメッセージ --%>
	<%
	String message = request.getParameter("message");
	if ("logout".equals(message)) {
	%>
	<p style="color: green;">ログアウトしました</p>
	<%
}
%>

	<%-- エラーメッセージ --%>
	<%
	if (request.getAttribute("error") != null) {
	%>
	<p style="color: red;"><%=request.getAttribute("error")%></p>
	<%
	}
	%>

	<form action="login" method="post">
		ユーザー名：<input type="text" name="username"><br> パスワード：<input
			type="password" name="password"><br>
		<br>
		<button type="submit" name="mode" value="normal">ログイン</button>
		<button type="submit" name="mode" value="demo">デモでログイン</button>
	</form>

	<p style="margin-top: 10px;">
		新規登録は <a href="<%=request.getContextPath()%>/register">こちら</a>
	</p>

</body>
</html>
