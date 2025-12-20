<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.KakeiboHistory" %>

<%
    List<KakeiboHistory> list =
        (List<KakeiboHistory>) request.getAttribute("historyList");
%>

<html>
<head>
<title>修正履歴</title>
<style>
table { border-collapse: collapse; width: 100%; }
th, td { border: 1px solid #ccc; padding: 6px; }
.changed { background: #fff4cc; font-weight: bold; }
.arrow { text-align: center; }
</style>
</head>
<body>

<h2>修正履歴</h2>

<table>
<tr>
    <th>更新日時</th>
    <th>項目</th>
    <th>変更前</th>
    <th></th>
    <th>変更後</th>
</tr>

<% for (KakeiboHistory h : list) { %>

    <%-- 日付 --%>
    <tr>
        <td rowspan="6"><%= h.getUpdatedAt() %></td>
        <td>日付</td>
        <td><%= h.getBeforeDate() %></td>
        <td class="arrow">→</td>
        <td class="<%= !h.getBeforeDate().equals(h.getAfterDate()) ? "changed" : "" %>">
            <%= h.getAfterDate() %>
        </td>
    </tr>

    <%-- 種別 --%>
    <tr>
        <td>種別</td>
        <td><%= h.getBeforeType() %></td>
        <td class="arrow">→</td>
        <td class="<%= !h.getBeforeType().equals(h.getAfterType()) ? "changed" : "" %>">
            <%= h.getAfterType() %>
        </td>
    </tr>

    <%-- 項目 --%>
    <tr>
        <td>項目</td>
        <td><%= h.getBeforeItem() %></td>
        <td class="arrow">→</td>
        <td class="<%= !h.getBeforeItem().equals(h.getAfterItem()) ? "changed" : "" %>">
            <%= h.getAfterItem() %>
        </td>
    </tr>

    <%-- 金額 --%>
    <tr>
        <td>金額</td>
        <td><%= h.getBeforeAmount() %></td>
        <td class="arrow">→</td>
        <td class="<%= h.getBeforeAmount() != h.getAfterAmount() ? "changed" : "" %>">
            <%= h.getAfterAmount() %>
        </td>
    </tr>

    <%-- メモ --%>
    <tr>
        <td>メモ</td>
        <td><%= h.getBeforeMemo() %></td>
        <td class="arrow">→</td>
        <td class="<%= !String.valueOf(h.getBeforeMemo())
                .equals(String.valueOf(h.getAfterMemo())) ? "changed" : "" %>">
            <%= h.getAfterMemo() %>
        </td>
    </tr>

    <tr><td colspan="4"></td></tr>

<% } %>

</table>

</body>
</html>
