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

@WebServlet("/action/subjectdelete")
public class SubjectDelete extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    Teacher teacher = (Teacher) session.getAttribute("teacher");
    if (teacher == null) {
      response.sendRedirect("../login.jsp");
      return;
    }

    String schoolCd = teacher.getSchool_cd();
    String cd = request.getParameter("cd");

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {
        try (PreparedStatement st = con.prepareStatement(
            "DELETE FROM SUBJECT WHERE school_cd = ? AND cd = ?")) {
          st.setString(1, schoolCd);
          st.setString(2, cd);
          st.executeUpdate();
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    response.sendRedirect("subjectlist");
  }
}
