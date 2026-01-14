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

<style>
.copy-star {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	width: 20px;
	height: 20px;
	margin-left: 4px;
	border-radius: 50%;
	background: #ffecec;
	color: red;
	font-weight: bold;
	cursor: pointer;
}

.copy-star:hover {
	background: #ffcccc;
}

.toast {
	position: fixed;
	bottom: 30px;
	left: 50%;
	transform: translateX(-50%);
	background: rgba(0, 0, 0, 0.85);
	color: #fff;
	padding: 10px 16px;
	border-radius: 6px;
	font-size: 14px;
	opacity: 0;
	pointer-events: none;
	transition: opacity 0.3s ease;
	z-index: 9999;
}

.toast.show {
	opacity: 1;
}

.demo-banner {
	background: #fff3cd;
	border: 1px solid #ffeeba;
	color: #856404;
	padding: 10px;
	margin-bottom: 15px;
	font-weight: bold;
}
</style>

<script>
	function showToast(message) {
	  const toast = document.getElementById("toast");
	  toast.textContent = message;
	  toast.classList.add("show");

	  setTimeout(() => {
	    toast.classList.remove("show");
	  }, 1500);
	}
	/* 行コピー（Excel用・タブ区切り） */
	function copyRow(date, type, item, amount, memo) {
  const text = [date, type, item, amount, memo].join("\t");
  navigator.clipboard.writeText(text)
    .then(() => {
      showToast("行をコピーしました");
    });
}


	/* 単項目コピー */
	function copyItem(value, event) {
  event.stopPropagation();
  navigator.clipboard.writeText(value)
    .then(() => {
      showToast("項目をコピーしました");
    });
}

</script>

</head>
<body>
	<%
	Boolean isDemo = (Boolean) session.getAttribute("IS_DEMO");
	if (isDemo != null && isDemo) {
	%>
	<div class="demo-banner">この画面はデモ用です。</div>
	<%
}
%>
	<div style="margin-bottom: 10px;"></div>
	<%
	if (Boolean.TRUE.equals(session.getAttribute("IS_DEMO"))) {
	%>
	<a href="<%=request.getContextPath()%>/demo?mode=off"> 通常モードに戻る </a>
	<%
	} else {
	%>
	<a href="<%=request.getContextPath()%>/demo?mode=on"> ▶ デモモードで表示 </a>
	<%
	}
	%>
	<%
	if (Boolean.TRUE.equals(session.getAttribute("IS_DEMO"))) {
	%>
	<div align="right">
		<form action="<%=request.getContextPath()%>/demo/reset" method="post">

			<button type="submit" onclick="return confirm('デモデータを初期状態に戻しますか？');">
				🔄 デモデータをリセット</button>
		</form>
	</div>
	<%
	}
	%>
	<h2>登録者一覧</h2>
	<table border="1">
		<%
		if (users == null || users.isEmpty()) {
		%>
		<tr>
			<td colspan="2">データがありません</td>
		</tr>
		<%
		} else {
		%>
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
		<%
		}
		%>
	</table>

	<h2>家計簿一覧</h2>
	<table border="1">
		<%
		if (kakeibos == null || kakeibos.isEmpty()) {
		%>
		<tr>
			<td colspan="7">データがありません</td>
		</tr>
		<%
		} else {
		%>
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
		<%
		}
		%>
	</table>

	<h2>修正ログ一覧</h2>
	<table border="1">
		<%
		if (histories == null || histories.isEmpty()) {
		%>
		<tr>
			<td colspan="6">データがありません</td>
		</tr>
		<%
		} else {
		%>
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
			<%
			String beforeMemo = h.getBeforeMemo() == null ? "" : h.getBeforeMemo();
			String afterMemo = h.getAfterMemo() == null ? "" : h.getAfterMemo();
			%>
			<td>
				<%
				if (!h.isRestored()) {
				%> [<%=h.getBeforeDate()%> / <%=h.getBeforeType()%> / <%=h.getBeforeItem()%>
				/ 金額:<%=nf.format(h.getBeforeAmount())%> / メモ:<%=beforeMemo%>]
				<%
				} else {
				%> ― <%
				}
				%>
			</td>

			<!-- 更新後 -->

			<td <%-- 削除以外のときだけ行コピーを有効にする --%>
<%if (!h.isDeleted()) {%>
				title="クリックで行をコピー（Excel貼り付け可）"
				onclick="copyRow(
   '<%=h.getAfterDate()%>',
   '<%=h.getAfterType()%>',
   '<%=h.getAfterItem()%>',
   '<%=nf.format(h.getAfterAmount())%>',
   '<%=afterMemo%>'
 )"
				style="cursor: pointer;" <%}%>>
				<%-- ===== 表示内容の分岐 ===== --%> <%
 if (h.isDeleted()) {
 %> <strong style="color: red;">削除されました</strong> <%
 } else if (h.isRestored()) {
 %> <strong style="color: green;">復元されました</strong><br> [<%=h.getAfterDate()%>
				/ <%=h.getAfterType()%> / <%=h.getAfterItem()%> / 金額:<%=nf.format(h.getAfterAmount())%>
				/ メモ:<%=afterMemo%>] <%
 } else {
 %> [ <!-- 日付 --> <%=h.getAfterDate()%> <%
 if (!h.getBeforeDate().equals(h.getAfterDate())) {
 %> <span class="copy-star" title="この日付をコピー"
				onclick="copyItem('<%=h.getAfterDate()%>', event)">*</span> <%
 }
 %> / <!-- 区分 --> <%=h.getAfterType()%> <%
 if (!h.getBeforeType().equals(h.getAfterType())) {
 %> <span class="copy-star" title="この区分をコピー"
				onclick="copyItem('<%=h.getAfterType()%>', event)">*</span> <%
 }
 %> / <!-- 用途 --> <%=h.getAfterItem()%> <%
 if (!h.getBeforeItem().equals(h.getAfterItem())) {
 %> <span class="copy-star" title="この用途をコピー"
				onclick="copyItem('<%=h.getAfterItem()%>', event)">*</span> <%
 }
 %> / <!-- 金額 --> 金額:<%=nf.format(h.getAfterAmount())%> <%
 if (h.getBeforeAmount() != h.getAfterAmount()) {
 %> <span class="copy-star" title="この金額をコピー"
				onclick="copyItem('<%=nf.format(h.getAfterAmount())%>', event)">*</span>
				<%
				}
				%> / <!-- メモ --> メモ:<%=afterMemo%> <%
 if (!beforeMemo.equals(afterMemo)) {
 %> <span class="copy-star" title="このメモをコピー"
				onclick="copyItem('<%=afterMemo%>', event)">*</span> <%
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
		<%
}
%>
	</table>

	<div id="toast" class="toast"></div>

</body>
</html>
