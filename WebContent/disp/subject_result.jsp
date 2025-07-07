<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<div class="content">
  <h2 class="menu-title">科目管理</h2>

  <p style="color: green;">
    ${message}
  </p>

  <div style="margin-top: 20px;">
    <a href="${pageContext.request.contextPath}/action/subjectlist">科目一覧へ戻る</a>
  </div>
</div>

<%@include file="../footer.html" %>
