<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<!-- 入力フォームや検索条件はここにあると仮定 -->

<c:choose>
  <c:when test="${not empty studentList}">
    <!-- ここに成績入力表を表示 -->
    <form action="../action/ScoreRegisterAction" method="post">
      <table border="1">
        <thead>
          <tr>
            <th>入学年度</th>
            <th>クラス</th>
            <th>学生番号</th>
            <th>氏名</th>
            <th>点数</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="student" items="${studentList}">
            <tr>
              <td>${student.entYear}</td>
              <td>${student.classNum}</td>
              <td>${student.studentNo}</td>
              <td>${student.studentName}</td>
              <td>
                <input type="text" name="score_${student.studentNo}" value="${student.score}" size="5" />
                <input type="hidden" name="studentNos" value="${student.studentNo}" />
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

      <br/>
      <button type="submit">登録して終了</button>
    </form>
  </c:when>

  <c:otherwise>
    <p style="color: red;">該当する学生がいません。</p>
  </c:otherwise>
</c:choose>
