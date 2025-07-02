<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.*" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<style>
.絞り込み {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  flex-wrap: wrap; /* 改行可能に */
}


.select-group {
  display: flex;
  flex-direction: column;
  width: 80px;
  margin: 0 5px;
}

/* ボタンをフォームの横幅いっぱいに広げて下に置く */
.button-group {
  flex-basis: 100%;  /* 横幅100% */
  margin-top: 10px;  /* 上の選択肢から少し余白 */
}
</style>

<div class="content">
  <h2 class="menu-title">成績登録</h2>

  <!-- 絞り込みフォーム -->
  <form class="絞り込み" action="scoreinsert" method="get" style="justify-content: center;">
    <div class="select-group">
      <label>入学年度</label>
      <select name="ent_year" required>
        <option value="">--選択--</option>
        <c:forEach var="year" items="${entYears}">
          <option value="${year}" <c:if test="${param.ent_year == year}">selected</c:if>>${year}</option>
        </c:forEach>
      </select>
    </div>
    <div class="select-group">
      <label>クラス</label>
      <select name="class_no" required>
        <option value="">--選択--</option>
        <c:forEach var="cls" items="${classNums}">
          <option value="${cls}" <c:if test="${param.class_no == cls}">selected</c:if>>${cls}</option>
        </c:forEach>
      </select>
    </div>
    <div class="select-group">
      <label>科目</label>
      <select name="subject" required>
        <option value="">--選択--</option>
        <c:forEach var="subj" items="${subjects}">
          <option value="${subj}" <c:if test="${param.subject == subj}">selected</c:if>>${subj}</option>
        </c:forEach>
      </select>
    </div>
    <div class="select-group">
      <label>回数</label>
      <select name="exam_round" required>
        <option value="">--選択--</option>
        <c:forEach var="round" items="${examRounds}">
          <option value="${round}" <c:if test="${param.exam_round == round}">selected</c:if>>第${round}回</option>
        </c:forEach>
      </select>
    </div>
    <div class="button-group">
    <button type="submit">表示</button>
  </div>
  </form>

  <!-- 件数表示 -->
  <c:if test="${not empty students}">
    <!-- 成績入力フォーム -->
    <form action="scoreinsert" method="post">
    <div style="margin-top: 10px;">
      対象学生：${students.size()} 名
    </div>
    <!-- ✅ formの中に科目・回数の表示 -->
    <c:if test="${not empty param.subject && not empty param.exam_round}">
      <p>科目：${param.subject}（第${param.exam_round}回）</p>
    </c:if>
      <!-- hiddenで条件を送る -->
      <input type="hidden" name="subject" value="${param.subject}" />
      <input type="hidden" name="exam_round" value="${param.exam_round}" />
      <input type="hidden" name="class_no" value="${param.class_no}" />
      <input type="hidden" name="ent_year" value="${param.ent_year}" />

      <table border="1" style="margin: 10px auto; border-collapse: collapse;">
        <thead>
          <tr>
            <th>学生番号</th>
            <th>氏名</th>
            <th>クラス</th>
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
              <td>${student.class_num}</td>
              <td>
              <input type="number" name="point_${student.no}" min="0" max="100"
               value="${pointMap[student.no]}" required>
              </td>

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