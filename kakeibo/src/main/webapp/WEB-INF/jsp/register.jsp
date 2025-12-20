<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新規登録</title>

<style>
body {
    font-family: sans-serif;
    background-color: #f5f5f5;
}
.container {
    width: 350px;
    margin: 100px auto;
    background: white;
    padding: 25px;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(0,0,0,0.15);
}
h2 {
    text-align: center;
}
input {
    width: 100%;
    padding: 8px;
    margin: 10px 0;
}
button {
    width: 100%;
    padding: 10px;
}
.error {
    color: red;
    text-align: center;
}
</style>

</head>
<body>

<div class="container">
<h2>新規登録</h2>

<% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= request.getAttribute("error") %></div>
<% } %>

<form action="<%= request.getContextPath() %>/register" method="post">
    <input type="text" name="username" placeholder="ユーザー名" required>
    <input type="password" name="password" placeholder="パスワード" required>
    <button type="submit">登録</button>
</form>

<p style="text-align:center;">
    <a href="<%= request.getContextPath() %>/login">ログインへ</a>
</p>

</div>

</body>
</html>
