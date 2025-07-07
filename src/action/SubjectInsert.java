package action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Teacher;
import dao.DAO;

@WebServlet("/action/subjectinsert")
public class SubjectInsert extends HttpServlet {
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
    String cd = request.getParameter("cd");
    String name = request.getParameter("name");

    String errorMessage = null;

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {
        // ✅ 科目コードの重複チェック
        try (PreparedStatement st = con.prepareStatement(
            "SELECT COUNT(*) FROM SUBJECT WHERE school_cd = ? AND cd = ?")) {
          st.setString(1, schoolCd);
          st.setString(2, cd);
          try (ResultSet rs = st.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
              errorMessage = "科目コードが重複しています。";
            }
          }
        }

        if (errorMessage == null) {
          // 登録処理
          try (PreparedStatement st = con.prepareStatement(
              "INSERT INTO SUBJECT (school_cd, cd, name) VALUES (?, ?, ?)")) {
            st.setString(1, schoolCd);
            st.setString(2, cd);
            st.setString(3, name);
            st.executeUpdate();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      errorMessage = "登録中にエラーが発生しました。<br>" + e.getMessage();
    }

    if (errorMessage != null) {
      // エラーがあれば入力画面に戻る
      request.setAttribute("error", errorMessage);
      request.setAttribute("cd", cd);
      request.setAttribute("name", name);
      request.getRequestDispatcher("/disp/subject_insert.jsp").forward(request, response);
    } else {
      // 成功した場合
      request.setAttribute("message", "科目を登録しました。");
      request.getRequestDispatcher("/disp/subject_result.jsp").forward(request, response);
    }
  }
}
