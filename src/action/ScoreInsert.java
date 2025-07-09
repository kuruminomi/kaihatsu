package action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @Override
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
    Map<String, Integer> pointMap = new HashMap<>(); // ⭐ 追加

    String selectedYear = request.getParameter("ent_year");
    String selectedClass = request.getParameter("class_no");
    String selectedSubject = request.getParameter("subject");
    String selectedExamRound = request.getParameter("exam_round");

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

        // 回数
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
        if (maxRound == 0) {
          testRounds.add(1);
        } else {
          for (int i = 1; i <= maxRound + 1; i++) {
            testRounds.add(i);
          }
        }

     // 学生リスト
        if (selectedYear != null && selectedClass != null &&
            !selectedYear.isEmpty() && !selectedClass.isEmpty()) {

          try (PreparedStatement st = con.prepareStatement(
              // ⭐ class_numもSELECTする
              "SELECT no, name, class_num FROM STUDENT WHERE school_cd = ? AND ent_year = ? AND class_num = ? AND is_attend = true ORDER BY no")) {
            st.setString(1, schoolCd);
            st.setInt(2, Integer.parseInt(selectedYear));
            st.setString(3, selectedClass);
            try (ResultSet rs = st.executeQuery()) {
              while (rs.next()) {
                Student s = new Student();
                s.setNo(rs.getString("no"));
                s.setName(rs.getString("name"));
                s.setClass_num(rs.getString("class_num")); // ⭐ 追加
                students.add(s);

                // 既存点数
                if (selectedSubject != null && selectedExamRound != null &&
                    !selectedSubject.isEmpty() && !selectedExamRound.isEmpty()) {

                  String subjectCd = null;
                  try (PreparedStatement stSub = con.prepareStatement(
                      "SELECT cd FROM SUBJECT WHERE school_cd = ? AND name = ?")) {
                    stSub.setString(1, schoolCd);
                    stSub.setString(2, selectedSubject);
                    try (ResultSet rsSub = stSub.executeQuery()) {
                      if (rsSub.next()) {
                        subjectCd = rsSub.getString("cd");
                      }
                    }
                  }

                  if (subjectCd != null) {
                    try (PreparedStatement stPoint = con.prepareStatement(
                        "SELECT point FROM TEST WHERE student_no = ? AND subject_cd = ? AND school_cd = ? AND no = ?")) {
                      stPoint.setString(1, s.getNo());
                      stPoint.setString(2, subjectCd);
                      stPoint.setString(3, schoolCd);
                      stPoint.setInt(4, Integer.parseInt(selectedExamRound));
                      try (ResultSet rsPoint = stPoint.executeQuery()) {
                        if (rsPoint.next()) {
                          pointMap.put(s.getNo(), rsPoint.getInt("point"));
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

    } catch (Exception e) {
      throw new ServletException(e);
    }

    request.setAttribute("entYears", entYears);
    request.setAttribute("classNums", classNums);
    request.setAttribute("subjects", subjectNames);
    request.setAttribute("examRounds", testRounds);
    request.setAttribute("students", students);
    request.setAttribute("pointMap", pointMap); // ⭐ JSPで使用

    request.getRequestDispatcher("/disp/seiseki_insert.jsp").forward(request, response);
  }

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
    String subjectName = request.getParameter("subject");
    int examRound = Integer.parseInt(request.getParameter("exam_round"));
    String classNum = request.getParameter("class_no");

    String message = "";

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {

        // 科目CD取得
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

        if (subjectCd == null) {
          throw new Exception("科目が見つかりませんでした。");
        }

        // 学生ごとにINSERT or UPDATE
        int insertCount = 0;
        int updateCount = 0;

        for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
          String param = e.nextElement();
          if (param.startsWith("point_")) {
            String studentNo = param.substring(6);
            String pointStr = request.getParameter(param);
            int point = (pointStr == null || pointStr.trim().isEmpty()) ? 0 : Integer.parseInt(pointStr.trim());


            // 既存確認
            boolean exists = false;
            try (PreparedStatement st = con.prepareStatement(
                "SELECT COUNT(*) FROM TEST WHERE student_no = ? AND subject_cd = ? AND school_cd = ? AND no = ?")) {
              st.setString(1, studentNo);
              st.setString(2, subjectCd);
              st.setString(3, schoolCd);
              st.setInt(4, examRound);
              try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                  exists = true;
                }
              }
            }

            if (exists) {
              try (PreparedStatement st = con.prepareStatement(
                  "UPDATE TEST SET point = ? WHERE student_no = ? AND subject_cd = ? AND school_cd = ? AND no = ?")) {
                st.setInt(1, point);
                st.setString(2, studentNo);
                st.setString(3, subjectCd);
                st.setString(4, schoolCd);
                st.setInt(5, examRound);
                st.executeUpdate();
                updateCount++;
              }
            } else {
              try (PreparedStatement st = con.prepareStatement(
                  "INSERT INTO TEST (student_no, subject_cd, school_cd, no, point, class_num) VALUES (?, ?, ?, ?, ?, ?)")) {
                st.setString(1, studentNo);
                st.setString(2, subjectCd);
                st.setString(3, schoolCd);
                st.setInt(4, examRound);
                st.setInt(5, point);
                st.setString(6, classNum);
                st.executeUpdate();
                insertCount++;
              }
            }
          }
        }

        message = String.format("登録が完了しました。新規登録：%d件 / 更新：%d件", insertCount, updateCount);
      }

    } catch (Exception e) {
      e.printStackTrace();
      message = "登録中にエラーが発生しました。<br>" + e.getMessage();
    }

    request.setAttribute("message", message);
    request.getRequestDispatcher("/disp/seiseki_insert_result.jsp").forward(request, response);
  }
}
