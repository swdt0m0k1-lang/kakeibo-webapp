<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.Kakeibo"%>
<%@ page import="model.KakeiboHistory"%>
<%@ page import="java.text.NumberFormat"%>
<%
NumberFormat nf = NumberFormat.getNumberInstance();
%>


<%
List<Kakeibo> list = (List<Kakeibo>) request.getAttribute("list");
Kakeibo edit = (Kakeibo) request.getAttribute("edit");
List<KakeiboHistory> historyList = (List<KakeiboHistory>) request.getAttribute("historyList");
List<KakeiboHistory> rowHistoryList = (List<KakeiboHistory>) request.getAttribute("rowHistoryList");
String userName = (String) request.getAttribute("userName");
%>

<html>
<head>
<style>
.highlight {
	background-color: #fff3cd;
	transition: background-color 0.3s;
}
</style>
<script>
function toggleAllHistory() {
    const area = document.getElementById("allHistoryArea");
    area.style.display = (area.style.display === "none") ? "block" : "none";
}

function highlightRow(kakeiboId) {
    document.querySelectorAll("tr.highlight").forEach(tr => tr.classList.remove("highlight"));
    const row = document.getElementById("row-" + kakeiboId);
    if (row) {
        row.classList.add("highlight");
        row.scrollIntoView({ behavior: "smooth", block: "center" });
    }
}

// 編集中なら自動ハイライト
window.onload = function () {
    <%if (edit != null) {%>
        highlightRow(<%=edit.getId()%>);
    <%}%>
};
</script>
<title>家計簿</title>
</head>

<body>
	<h2
		style="display: flex; justify-content: space-between; align-items: center;">
		<span>家計簿 <%=(edit == null) ? "登録" : "編集"%></span> <span
			style="font-size: 90%; color: #666;"> <a
			href="<%=request.getContextPath()%>/logout">ログアウト</a> / ログイン中：<strong><%=userName%>さん</strong>
		</span>
	</h2>

	<!-- 登録 / 編集フォーム -->
	<form action="list" method="post">
		<input type="hidden" name="id"
			value="<%=(edit != null) ? edit.getId() : ""%>"> 日付: <input
			type="date" name="date" required
			value="<%=(edit != null) ? edit.getDate() : ""%>"> 区分: <select
			name="type">
			<option value="出金"
				<%=(edit != null && "出金".equals(edit.getType())) ? "selected" : ""%>>出金</option>
			<option value="入金"
				<%=(edit != null && "入金".equals(edit.getType())) ? "selected" : ""%>>入金</option>
		</select> 用途: <input type="text" name="item" required
			value="<%=(edit != null) ? edit.getItem() : ""%>"> 金額: <input
			type="number" name="amount" required
			value="<%=(edit != null) ? edit.getAmount() : ""%>"> メモ: <input
			type="text" name="memo"
			value="<%=(edit != null) ? edit.getMemo() : ""%>">
		<button type="submit"><%=(edit == null) ? "登録" : "更新"%></button>
		<%
		if (edit != null) {
		%>
		<a href="list">キャンセル</a>
		<%
		}
		%>
	</form>

	<hr>

	<div style="display: flex; gap: 20px; align-items: flex-start;">

		<!-- 左：一覧 -->
		<div style="flex: 3;">
			<h2>一覧</h2>
			<table border="1" width="100%">
				<tr>
					<th>日付</th>
					<th>区分</th>
					<th>用途</th>
					<th>金額</th>
					<th>メモ</th>
					<th>操作</th>
				</tr>
				<%
				if (list != null) {
					for (Kakeibo k : list) {
				%>
				<tr id="row-<%=k.getId()%>">
					<td><%=k.getDate()%></td>
					<td><%=k.getType()%></td>
					<td><%=k.getItem()%></td>
					<td style="text-align: right;"><%=nf.format(k.getAmount())%></td>

					<td><%=k.getMemo()%></td>
					<td><a href="list?editId=<%=k.getId()%>">編集</a>
						<form action="<%=request.getContextPath()%>/delete" method="post"
							style="display: inline;">
							<input type="hidden" name="id" value="<%=k.getId()%>">
							<button type="submit" onclick="return confirm('本当に削除しますか？')">削除</button>
						</form></td>
				</tr>
				<%
				}
				}
				%>
			</table>
		</div>

		<!-- 右：変更履歴 -->
		<div style="flex: 2; border-left: 2px solid #ccc; padding-left: 15px;">
			<h3>
				変更履歴
				<button type="button" onclick="toggleAllHistory()"
					style="margin-left: 10px;">表示 / 非表示</button>
			</h3>

			<div id="allHistoryArea"
				style="display: none; max-height: 350px; overflow-y: auto;">

				<%
				if (historyList != null && !historyList.isEmpty()) {
					for (KakeiboHistory h : historyList) {
				%>

				<div onclick="highlightRow(<%=h.getKakeiboId()%>)"
					style="border: 1px solid #ccc; padding: 10px; margin-bottom: 12px; background: #f9f9f9; cursor: pointer;">

					<!-- 更新日時 -->
					<div style="font-size: 90%; color: #666; margin-bottom: 4px;">
						<%=h.getUpdatedAt()%>
					</div>

					<%
					if (h.isDeleted()) {
					%>

					<!-- 削除履歴 -->
					<div class="deleted" style="color: red; font-weight: bold;">
						削除されました</div>

					<div style="font-size: 90%; color: #555;">
						[
						<%=h.getBeforeDate()%>
						/
						<%=h.getBeforeType()%>
						/
						<%=h.getBeforeItem()%>
						/ 金額:<%=nf.format(h.getBeforeAmount())%>
						/ メモ:<%=h.getBeforeMemo() == null ? "" : h.getBeforeMemo()%>
						]
					</div>

					<form action="<%=request.getContextPath()%>/restore" method="post">
						<input type="hidden" name="id" value="<%=h.getKakeiboId()%>">
						<button type="submit" onclick="return confirm('このデータを復元しますか？')">
							復元</button>
					</form>

					<%
					} else if (h.isRestored()) {
					%>

					<!-- 復元履歴 -->
					<div style="color: green; font-weight: bold; margin-bottom: 6px;">
						復元されました</div>

					<div style="font-size: 90%; color: #555;">
						[
						<%=h.getAfterDate()%>
						/
						<%=h.getAfterType()%>
						/
						<%=h.getAfterItem()%>
						/ 金額:<%=nf.format(h.getAfterAmount())%>
						/ メモ:<%=h.getAfterMemo() == null ? "" : h.getAfterMemo()%>
						]
					</div>

					<%
					} else {
					%>

					<!-- 通常更新のヘッダ（表示順を整理） -->
					<div style="font-weight: bold; margin-bottom: 6px;">
						[
						<%=h.getAfterDate()%>
						/
						<%=h.getAfterType()%>
						/
						<%=h.getAfterItem()%>
						/ 金額:<%=nf.format(h.getAfterAmount())%>
						/ メモ:<%=(h.getAfterMemo() == null ? "" : h.getAfterMemo())%>
						]
					</div>


					<ul style="margin: 0 0 0 16px;">
						<%
						if (h.getBeforeDate() != null && !h.getBeforeDate().equals(h.getAfterDate())) {
						%>
						<li>日付: <%=h.getBeforeDate()%> → <strong>* <%=h.getAfterDate()%></strong></li>
						<%
						}
						%>

						<%
						if (h.getBeforeType() != null && !h.getBeforeType().equals(h.getAfterType())) {
						%>
						<li>区分: <%=h.getBeforeType()%> → <strong>* <%=h.getAfterType()%></strong></li>
						<%
						}
						%>

						<%
						if (h.getBeforeItem() != null && !h.getBeforeItem().equals(h.getAfterItem())) {
						%>
						<li>用途: <%=h.getBeforeItem()%> → <strong>* <%=h.getAfterItem()%></strong></li>
						<%
						}
						%>

						<%
						if (h.getBeforeAmount() != h.getAfterAmount()) {
						%>
						<li>金額: <%=nf.format(h.getBeforeAmount())%> → <strong>*
								<%=nf.format(h.getAfterAmount())%></strong></li>
						<%
						}
						%>

						<%
						if (h.getBeforeMemo() != null && !h.getBeforeMemo().equals(h.getAfterMemo())) {
						%>
						<li>メモ: <%=h.getBeforeMemo()%> → <strong>* <%=h.getAfterMemo()%></strong></li>
						<%
						}
						%>
					</ul>

					<%
					}
					%>
				</div>

				<%
				}
				} else {
				%>
				<p>履歴はありません</p>
				<%
}
%>
			</div>


			<!-- 編集中の行履歴 -->
			<%
			if (edit != null) {
			%>
			<hr>
			<h3>この行の変更履歴</h3>
			<%
			if (rowHistoryList != null && !rowHistoryList.isEmpty()) {
				for (KakeiboHistory h : rowHistoryList) {
			%>
			<div
				style="border: 1px solid #ccc; padding: 10px; margin-bottom: 10px; cursor: pointer;">
				<div style="font-size: 90%; color: #666;"><%=h.getUpdatedAt()%></div>
				<ul>
					<%
					if (h.isDeleted()) {
					%>
					<li><strong>削除されました</strong></li>
					<%
					} else {
					%>
					<%
					if (!h.getBeforeItem().equals(h.getAfterItem())) {
					%>
					<li>用途: <%=h.getBeforeItem()%> → <strong>* <%=h.getAfterItem()%></strong></li>
					<%
					}
					%>
					<%
					if (h.getBeforeAmount() != h.getAfterAmount()) {
					%>
					<li>金額: <%=nf.format(h.getBeforeAmount())%> → <strong>*
							<%=nf.format(h.getAfterAmount())%></strong></li>
					<%
					}
					%>
					<%
					if (!h.getBeforeMemo().equals(h.getAfterMemo())) {
					%>
					<li>メモ: <%=h.getBeforeMemo()%> → <strong>* <%=h.getAfterMemo()%></strong></li>
					<%
					}
					%>
					<%
					}
					%>
				</ul>
			</div>
			<%
			}
			} else {
			%>
			<p>この行の変更履歴はありません</p>
			<%
			}
			%>
			<%
			}
			%>

		</div>

	</div>
</body>
</html>
