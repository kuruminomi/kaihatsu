package action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Teacher;
import dao.DAO;

@WebServlet("/action/seisekisearchresult")
public class SeisekisearchResult extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");

    HttpSession session = request.getSession();
    Teacher teacher = (Teacher) session.getAttribute("teacher");
    if (teacher == null) {
      response.sendRedirect("../login.jsp");
      return;
    }

    String schoolCd = teacher.getSchool_cd();

    // 検索条件
    String entYear = request.getParameter("ent_year");
    String classNo = request.getParameter("class_no");
    String subjectName = request.getParameter("subject");
    String studentNo = request.getParameter("student_no");

    List<Map<String, Object>> results = new ArrayList<>();

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append(" s.NO AS student_no, ")
           .append(" s.NAME AS student_name, ")
           .append(" sub.NAME AS subject_name, ")
           .append(" t.NO AS exam_round, ")
           .append(" t.POINT ")
           .append("FROM TEST t ")
           .append("JOIN STUDENT s ON t.STUDENT_NO = s.NO AND t.SCHOOL_CD = s.SCHOOL_CD ")
           .append("JOIN SUBJECT sub ON t.SUBJECT_CD = sub.CD AND t.SCHOOL_CD = sub.SCHOOL_CD ")
           .append("WHERE t.SCHOOL_CD = ? ");

        List<Object> params = new ArrayList<>();
        params.add(schoolCd);

        if (studentNo != null && !studentNo.isEmpty()) {
          sql.append(" AND s.NO = ? ");
          params.add(studentNo);
        } else {
          if (entYear != null && !entYear.isEmpty()) {
            sql.append(" AND s.ENT_YEAR = ? ");
            params.add(Integer.parseInt(entYear));
          }
          if (classNo != null && !classNo.isEmpty()) {
            sql.append(" AND s.CLASS_NUM = ? ");
            params.add(classNo);
          }
          if (subjectName != null && !subjectName.isEmpty()) {
            sql.append(" AND sub.NAME = ? ");
            params.add(subjectName);
          }
        }

        sql.append(" ORDER BY t.NO ASC, s.NO ASC, sub.CD ASC ");


        try (PreparedStatement st = con.prepareStatement(sql.toString())) {
          for (int i = 0; i < params.size(); i++) {
            st.setObject(i + 1, params.get(i));
          }

          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              Map<String, Object> row = new HashMap<>();
              row.put("studentNo", rs.getString("student_no"));
              row.put("studentName", rs.getString("student_name"));
              row.put("subject", rs.getString("subject_name"));
              row.put("examRound", rs.getInt("exam_round"));
              row.put("point", rs.getInt("point"));
              results.add(row);
            }
          }
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    request.setAttribute("results", results);
    request.getRequestDispatcher("/disp/seiseki_result.jsp").forward(request, response);
  }
}
