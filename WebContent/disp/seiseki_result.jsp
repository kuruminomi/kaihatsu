<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<div class="content">
  <h2 class="menu-title">検索結果</h2>

  <c:if test="${not empty results}">
    <table border="1" style="border-collapse: collapse; width: 100%; margin-top: 10px;">
      <thead>
        <tr>
          <th>学生番号</th>
          <th>氏名</th>
          <th>科目</th>
          <th>回数</th>
          <th>点数</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="r" items="${results}">
          <tr>
            <td>${r.studentNo}</td>
            <td>${r.studentName}</td>
            <td>${r.subject}</td>
            <td>第${r.examRound}回</td>
            <td>${r.point}</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </c:if>

  <c:if test="${empty results}">
    <p style="color: red;">該当する成績がありません。</p>
  </c:if>

  <div style="margin-top: 20px;">
    <a href="${pageContext.request.contextPath}/action/seisekisearch">検索画面に戻る</a>
  </div>
</div>

<%@include file="../footer.html" %>
