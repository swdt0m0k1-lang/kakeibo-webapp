<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.User"%>
<%@ page import="model.Kakeibo"%>
<%@ page import="model.KakeiboHistory"%>
<%@ page import="java.text.NumberFormat"%>
<%
NumberFormat nf = NumberFormat.getNumberInstance();
%>


<%
List<User> users = (List<User>) request.getAttribute("users");
List<Kakeibo> kakeibos = (List<Kakeibo>) request.getAttribute("kakeibos");
List<KakeiboHistory> histories = (List<KakeiboHistory>) request.getAttribute("histories");
%>

<html>
<head>
<title>テストページ</title>

<script>
function copyForExcel(date, type, item, amount, memo) {
    const text = [
        date,
        type,
        item,
        amount,
        memo
    ].join("\t"); // タブ区切り → Excelで列に分かれる

    navigator.clipboard.writeText(text)
        .then(() => {
            alert("Excel用にコピーしました");
        })
        .catch(err => {
            alert("コピーに失敗しました");
            console.error(err);
        });
}
</script>

</head>
<body>

	<h2>登録者一覧</h2>
	<table border="1">
		<tr>
			<th>ID</th>
			<th>ユーザー名</th>
		</tr>
		<%
		for (User u : users) {
		%>
		<tr>
			<td><%=u.getId()%></td>
			<td><%=u.getUsername()%></td>
		</tr>
		<%
		}
		%>
	</table>

	<h2>家計簿一覧</h2>
	<table border="1">
		<tr>
			<th>ID</th>
			<th>ユーザーID</th>
			<th>日付</th>
			<th>区分</th>
			<th>用途</th>
			<th>金額</th>
			<th>メモ</th>
		</tr>
		<%
		for (Kakeibo k : kakeibos) {
		%>
		<tr>
			<td><%=k.getId()%></td>
			<td><%=k.getUserId()%></td>
			<td><%=k.getDate()%></td>
			<td><%=k.getType()%></td>
			<td><%=k.getItem()%></td>
			<td style="text-align: right;"><%=nf.format(k.getAmount())%></td>
			<td><%=k.getMemo()%></td>
		</tr>
		<%
		}
		%>
	</table>

	<h2>修正ログ一覧</h2>
	<table border="1">
		<tr>
			<th>ID</th>
			<th>ユーザーID</th>
			<th>家計簿ID</th>
			<th>変更前</th>
			<th>更新後</th>
			<th>更新日時</th>
		</tr>

		<%
		for (KakeiboHistory h : histories) {
		%>
		<tr>
			<td><%=h.getId()%></td>
			<td><%=h.getUserId()%></td>
			<td><%=h.getKakeiboId()%></td>

			<!-- 変更前 -->
			<td>
				<%
				if (!h.isRestored()) {
				%> [<%=h.getBeforeDate()%> / <%=h.getBeforeType()%> / <%=h.getBeforeItem()%>
				/ 金額:<%=nf.format(h.getBeforeAmount())%> / メモ:<%=h.getBeforeMemo()%>]
				<%
				} else {
				%> ― <%
				}
				%>
			</td>

			<!-- 更新後 -->
			<td>
				<%
				if (h.isDeleted()) {
				%> <strong style="color: red;">削除されました</strong> <%
} else if (h.isRestored()) {
%> <strong style="color: green;">復元されました</strong><br> [<%=h.getAfterDate()%>
				/ <%=h.getAfterType()%> / <%=h.getAfterItem()%> / 金額:<%=nf.format(h.getAfterAmount())%>
				/ メモ:<%=h.getAfterMemo() == null ? "" : h.getAfterMemo()%>] <%
 } else {
 String beforeMemo = h.getBeforeMemo() == null ? "" : h.getBeforeMemo();
 String afterMemo = h.getAfterMemo() == null ? "" : h.getAfterMemo();
 %> [ <!-- 日付 --> <%=h.getAfterDate()%> <%
 if (!h.getBeforeDate().equals(h.getAfterDate())) {
 %>
				<span style="color: red; cursor: pointer; font-weight: bold;"
				onclick="copyForExcel(
     '<%=h.getAfterDate()%>',
     '<%=h.getAfterType()%>',
     '<%=h.getAfterItem()%>',
     '<%=nf.format(h.getAfterAmount())%>',
     '<%=afterMemo%>'
 )"
				title="Excel用にコピー">*</span> <%
 }
 %> / <!-- 区分 --> <%=h.getAfterType()%>
				<%
				if (!h.getBeforeType().equals(h.getAfterType())) {
				%> <span
				style="color: red; cursor: pointer; font-weight: bold;"
				onclick="copyForExcel(
     '<%=h.getAfterDate()%>',
     '<%=h.getAfterType()%>',
     '<%=h.getAfterItem()%>',
     '<%=nf.format(h.getAfterAmount())%>',
     '<%=afterMemo%>'
 )">*</span>
				<%
				}
				%> / <!-- 用途 --> <%=h.getAfterItem()%> <%
 if (!h.getBeforeItem().equals(h.getAfterItem())) {
 %>
				<span style="color: red; cursor: pointer; font-weight: bold;"
				onclick="copyForExcel(
     '<%=h.getAfterDate()%>',
     '<%=h.getAfterType()%>',
     '<%=h.getAfterItem()%>',
     '<%=nf.format(h.getAfterAmount())%>',
     '<%=afterMemo%>'
 )">*</span>
				<%
				}
				%> / <!-- 金額 --> 金額:<%=nf.format(h.getAfterAmount())%> <%
 if (h.getBeforeAmount() != h.getAfterAmount()) {
 %>
				<span style="color: red; cursor: pointer; font-weight: bold;"
				onclick="copyForExcel(
     '<%=h.getAfterDate()%>',
     '<%=h.getAfterType()%>',
     '<%=h.getAfterItem()%>',
     '<%=nf.format(h.getAfterAmount())%>',
     '<%=afterMemo%>'
 )">*</span>
				<%
				}
				%> / <!-- メモ --> メモ:<%=afterMemo%> <%
 if (!beforeMemo.equals(afterMemo)) {
 %>
				<span style="color: red; cursor: pointer; font-weight: bold;"
				onclick="copyForExcel(
     '<%=h.getAfterDate()%>',
     '<%=h.getAfterType()%>',
     '<%=h.getAfterItem()%>',
     '<%=nf.format(h.getAfterAmount())%>',
     '<%=afterMemo%>'
 )">*</span>
				<%
				}
				%> ] <%
}
%>
			</td>

			<td><%=h.getUpdatedAt()%></td>
		</tr>
		<%
}
%>
	</table>



</body>
</html>
