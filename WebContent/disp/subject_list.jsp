<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<div class="content">
  <h2 class="menu-title">科目管理</h2>

  <!-- 新規登録ボタン -->
  <div style="margin-bottom: 15px;">
    <a href="${pageContext.request.contextPath}/disp/subject_insert.jsp">新規科目登録</a>
  </div>

  <!-- 科目一覧 -->
  <c:if test="${not empty subjects}">
    <table border="1" style="border-collapse: collapse; width: 100%;">
      <thead>
        <tr>
          <th>科目コード</th>
          <th>科目名</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="subj" items="${subjects}">
          <tr>
            <td>${subj.cd}</td>
            <td>${subj.name}</td>
            <td>
              <a href="${pageContext.request.contextPath}/disp/subjectupdateform?cd=${subj.cd}">変更</a>
              |
              <a href="${pageContext.request.contextPath}/action/subjectdelete?cd=${subj.cd}"
                 onclick="return confirm('本当に削除しますか？');">削除</a>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </c:if>

  <c:if test="${empty subjects}">
    <p style="color:red;">登録されている科目がありません。</p>
  </c:if>
</div>

<%@include file="../footer.html" %>
