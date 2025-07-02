<%@page contentType="text/html; charset=UTF-8" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<div class="content">
  <h2 class="menu-title">成績登録結果</h2>

  <p>${message}</p>

  <a href="<%= request.getContextPath() %>/action/scoreinsert">成績登録に戻る</a>
</div>


<%@include file="../footer.html" %>
