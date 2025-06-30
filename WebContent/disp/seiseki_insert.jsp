<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.*" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<style>
form {
  display: flex;
  align-items: flex-start; /* 上揃え */
  gap: 20px; /* セット間の余白 */
}

.select-group {
  display: flex;
  flex-direction: column; /* labelを上にselectを下に */
}


</style>

<div class="content">
  <h2 class="menu-title">成績登録</h2>

  <!-- 成績登録条件の選択フォーム -->
  <form action="scoreinsert" method="get">
  <div class="select-group">
    <label>入学年度：</label>
    <select name="ent_year">
      <option value="">--選択--</option>
      <c:forEach var="year" items="${entYears}">
        <option value="${year}" <c:if test="${param.ent_year == year}">selected</c:if>>${year}</option>
      </c:forEach>
    </select>
    </div>

    <div class="select-group">
    <label>クラス：</label>
    <select name="class_no">
      <option value="">--選択--</option>
      <c:forEach var="cls" items="${classNums}">
        <option value="${cls}" <c:if test="${param.class_no == cls}">selected</c:if>>${cls}</option>
      </c:forEach>
    </select>
    </div>

    <div class="select-group">
    <label>科目：</label>
    <select name="subject">
      <option value="">--選択--</option>
      <c:forEach var="subj" items="${subjects}">
        <option value="${subj}" <c:if test="${param.subject == subj}">selected</c:if>>${subj}</option>
      </c:forEach>
    </select>
    </div>

    <div class="select-group">
    <label>回数：</label>
    <select name="exam_round">
      <option value="">--選択--</option>
      <c:forEach var="round" items="${examRounds}">
        <option value="${round}" <c:if test="${param.exam_round == round}">selected</c:if>>第${round}回</option>
      </c:forEach>
    </select>
    </div>

    <button type="submit">表示</button>
  </form>

  <!-- 件数表示（学生リストが取得されている場合） -->
  <c:if test="${not empty students}">
    <div style="margin-top: 10px;">
      対象学生：${students.size()} 名
    </div>

    <!-- 成績入力テーブル（この部分は後でPOST処理に拡張可） -->
    <form action="scoreinsert" method="post">
      <table border="1" style="margin: 10px auto; border-collapse: collapse;">
        <thead>
          <tr>
            <th>学生番号</th>
            <th>氏名</th>
            <th>点数</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="student" items="${students}">
            <tr>
              <td>
                ${student.no}
                <input type="hidden" name="student_no" value="${student.no}" />
              </td>
              <td>${student.name}</td>
              <td><input type="number" name="point_${student.no}" min="0" max="100" required></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <button type="submit">登録</button>
    </form>
  </c:if>

  <c:if test="${empty students && param.ent_year != null}">
    <div style="margin-top: 10px; color: red;">該当する学生がいません。</div>
  </c:if>
</div>


<%@include file="../footer.html" %>
