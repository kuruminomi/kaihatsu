<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<div class="content">
  <h2 class="menu-title">新規科目登録</h2>
  <form id="subjectForm" action="${pageContext.request.contextPath}/action/subjectinsert" method="post">
    <table>
      <tr>
        <td><label for="cd">科目コード：</label></td>
        <td><input type="text" name="cd" id="cd" required maxlength="3" value="${cd}"></td>
      </tr>
      <div id="cdWarning" style="color: red; display: none;">科目コードは3文字で入力してください。</div>
      <div style="color: red; margin-bottom: 10px;">${error}</div>
      <tr>
        <td><label for="name">科目名：</label></td>
        <td><input type="text" name="name" id="name" required maxlength="20" value="${name}" /></td>
      </tr>
    </table>
    <div class="button-row">
      <a href="${pageContext.request.contextPath}/action/subjectlist">
        <button type="button" class="back">戻る</button>
      </a>
      <button type="submit">登録</button>
    </div>
  </form>
</div>

<script>
document.getElementById("subjectForm").addEventListener("submit", function(e) {
  const cd = document.getElementById("cd").value.trim();
  if (cd.length !== 3) {
    e.preventDefault();
    document.getElementById("cdWarning").style.display = "block";
  } else {
    document.getElementById("cdWarning").style.display = "none";
  }
});
</script>

<%@include file="../footer.html" %>
