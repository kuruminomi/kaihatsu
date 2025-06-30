package action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Student;
import bean.Teacher;
import dao.DAO;

@WebServlet("/action/scoreinsert")
public class ScoreInsert extends HttpServlet {

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");
    HttpSession session = request.getSession();
    Teacher teacher = (Teacher) session.getAttribute("teacher");
    if (teacher == null) {
      response.sendRedirect("../login.jsp");
      return;
    }

    String schoolCd = teacher.getSchool_cd();
    List<Integer> entYears = new ArrayList<>();
    List<String> classNums = new ArrayList<>();
    List<String> subjectNames = new ArrayList<>();
    List<Integer> testRounds = new ArrayList<>();
    List<Student> students = new ArrayList<>();

    String selectedYear = request.getParameter("ent_year");
    String selectedClass = request.getParameter("class_no");
    String selectedSubject = request.getParameter("subject");

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {

        // 入学年度
        try (PreparedStatement st = con.prepareStatement(
            "SELECT DISTINCT ent_year FROM STUDENT WHERE school_cd = ? ORDER BY ent_year")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              entYears.add(rs.getInt("ent_year"));
            }
          }
        }

        // クラス
        try (PreparedStatement st = con.prepareStatement(
            "SELECT DISTINCT class_num FROM STUDENT WHERE school_cd = ? ORDER BY class_num")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              classNums.add(rs.getString("class_num"));
            }
          }
        }

        // 科目
        try (PreparedStatement st = con.prepareStatement(
            "SELECT DISTINCT name FROM SUBJECT WHERE school_cd = ? ORDER BY name")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              subjectNames.add(rs.getString("name"));
            }
          }
        }

        // 回数（最大＋1まで）
        int maxRound = 0;
        try (PreparedStatement st = con.prepareStatement(
            "SELECT MAX(no) FROM TEST WHERE school_cd = ?")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
              maxRound = rs.getInt(1);
            }
          }
        }
        if (maxRound == 0) testRounds.add(1);
        else for (int i = 1; i <= maxRound + 1; i++) testRounds.add(i);

        // 学生リスト
        if (selectedYear != null && selectedClass != null &&
            !selectedYear.isEmpty() && !selectedClass.isEmpty()) {

          try (PreparedStatement st = con.prepareStatement(
              "SELECT no, name FROM STUDENT WHERE school_cd = ? AND ent_year = ? AND class_num = ? AND is_attend = true ORDER BY no")) {
            st.setString(1, schoolCd);
            st.setInt(2, Integer.parseInt(selectedYear));
            st.setString(3, selectedClass);
            try (ResultSet rs = st.executeQuery()) {
              while (rs.next()) {
                Student s = new Student();
                s.setNo(rs.getString("no"));
                s.setName(rs.getString("name"));
                students.add(s);
              }
            }
          }
        }

      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    // フォワード先へ渡す
    request.setAttribute("entYears", entYears);
    request.setAttribute("classNums", classNums);
    request.setAttribute("subjects", subjectNames);
    request.setAttribute("examRounds", testRounds);
    request.setAttribute("students", students);

    request.getRequestDispatcher("/disp/seiseki_insert.jsp").forward(request, response);
  }

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
    String subjectName = request.getParameter("subject");
    int examRound = Integer.parseInt(request.getParameter("exam_round"));
    String classNum = request.getParameter("class_no");

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {

        // 科目名 → CD取得
        String subjectCd = null;
        try (PreparedStatement st = con.prepareStatement(
            "SELECT cd FROM SUBJECT WHERE school_cd = ? AND name = ?")) {
          st.setString(1, schoolCd);
          st.setString(2, subjectName);
          try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
              subjectCd = rs.getString("cd");
            }
          }
        }

        // 学生ごとに点数登録
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
          String param = paramNames.nextElement();
          if (param.startsWith("point_")) {
            String studentNo = param.substring(6); // 例：point_000 → 000
            int point = Integer.parseInt(request.getParameter(param));

            // INSERT処理
            try (PreparedStatement st = con.prepareStatement(
                "INSERT INTO TEST (student_no, subject_cd, school_cd, no, point, class_num) VALUES (?, ?, ?, ?, ?, ?)")) {
              st.setString(1, studentNo);
              st.setString(2, subjectCd);
              st.setString(3, schoolCd);
              st.setInt(4, examRound);
              st.setInt(5, point);
              st.setString(6, classNum);
              st.executeUpdate();
            }
          }
        }

      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    // 登録後はGETにリダイレクト
    response.sendRedirect("scoreinsert?ent_year=" + request.getParameter("ent_year")
        + "&class_no=" + classNum
        + "&subject=" + subjectName
        + "&exam_round=" + examRound);
  }
}
