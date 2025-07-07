package action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

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

    String message = "";

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {
        try (PreparedStatement st = con.prepareStatement(
            "INSERT INTO SUBJECT (school_cd, cd, name) VALUES (?, ?, ?)")) {
          st.setString(1, schoolCd);
          st.setString(2, cd);
          st.setString(3, name);
          st.executeUpdate();
        }
      }
    } catch (Exception e) {
        e.printStackTrace();
        message = "登録中にエラーが発生しました。<br>" + e.getMessage();
      }

      request.setAttribute("message", message);
      request.getRequestDispatcher("/disp/subject_result.jsp").forward(request, response);
    }
  }
