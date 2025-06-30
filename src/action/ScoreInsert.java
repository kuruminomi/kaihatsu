package action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Teacher;
import dao.DAO;

@WebServlet("/action/scoreinsert")
public class ScoreInsert extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");

    // ✅ ログイン中の教師を取得
    HttpSession session = request.getSession();
    Teacher teacher = (Teacher) session.getAttribute("teacher");

    if (teacher == null) {
      response.sendRedirect("../login.jsp");
      return;
    }

    String schoolCd = teacher.getSchool_cd(); // ログイン者の学校コード

    // ✅ 各種選択肢リスト
    List<Integer> entYears = new ArrayList<>();
    List<String> classNums = new ArrayList<>();
    List<String> subjectNames = new ArrayList<>();
    List<Integer> testRounds = new ArrayList<>();

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {

        // ✅ 入学年度（STUDENT）
        try (PreparedStatement st = con.prepareStatement(
            "SELECT DISTINCT ent_year FROM STUDENT WHERE school_cd = ? ORDER BY ent_year")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              entYears.add(rs.getInt("ent_year"));
            }
          }
        }

        // ✅ クラス番号（STUDENT）
        try (PreparedStatement st = con.prepareStatement(
            "SELECT DISTINCT class_num FROM STUDENT WHERE school_cd = ? ORDER BY class_num")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              classNums.add(rs.getString("class_num"));
            }
          }
        }

        // ✅ 科目名（SUBJECT）
        try (PreparedStatement st = con.prepareStatement(
            "SELECT DISTINCT name FROM SUBJECT WHERE school_cd = ? ORDER BY name")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              subjectNames.add(rs.getString("name"));
            }
          }
        }

        // ✅ 回数（TEST.NO）
        try (PreparedStatement st = con.prepareStatement(
            "SELECT DISTINCT no FROM TEST WHERE school_cd = ? ORDER BY no")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              testRounds.add(rs.getInt("no"));
            }
          }
        }

      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    // ✅ JSP にデータを渡す
    request.setAttribute("entYears", entYears);
    request.setAttribute("classNums", classNums);
    request.setAttribute("subjects", subjectNames);
    request.setAttribute("examRounds", testRounds);

    request.getRequestDispatcher("/disp/seiseki_insert.jsp").forward(request, response);
  }
}
