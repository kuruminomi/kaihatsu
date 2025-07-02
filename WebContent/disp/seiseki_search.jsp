<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.*" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../header.html" %>
<%@include file="menu.jsp" %>

<div class="content">
  <h2 class="menu-title">成績参照検索</h2>

  <p>科目情報や学生情報から成績を表示することができます。</p>

  <!-- 科目情報検索 -->

  <form method="post" action="<%= request.getContextPath() %>/action/seisekisearchresult">
  <h3>科目情報</h3>


  <label>入学年度</label>
  <select name="ent_year">
  <option value="">--選択--</option>
  <c:forEach var="year" items="${entYears}">
    <option value="${year}" <c:if test="${param.ent_year == year}">selected</c:if>>${year}</option>
  </c:forEach>
  </select>
  <br><br>


  <label>クラス</label>
  <select name="class_no">
  <option value="">--選択--</option>
  <c:forEach var="cls" items="${classNums}">
    <option value="${cls}" <c:if test="${param.class_no == cls}">selected</c:if>>${cls}</option>
  </c:forEach>
  </select>
  <br><br>

  <label>科目</label>
  <select name="subject">
  <option value="">--選択--</option>
  <c:forEach var="subj" items="${subjects}">
    <option value="${subj}" <c:if test="${param.subject == subj}">selected</c:if>>${subj}</option>
  </c:forEach>
  </select>
  <br><br>

    <button type="submit">検索</button>
  </form>

  <hr/>

  <!-- 学生情報検索 -->
  <form method="post" action="<%= request.getContextPath() %>/action/seisekisearchresult">
  <h3>学生情報</h3>
    <label>学生番号</label>
    <input type="text" name="student_no" maxlength="10" placeholder="学生番号を入力してください" required/>
    <br>
    <br>
    <button type="submit">検索</button>
  </form>
</div>

<%@include file="../footer.html" %>
