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

@WebServlet("/action/subjectupdate")
public class SubjectUpdate extends HttpServlet {
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

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {
        try (PreparedStatement st = con.prepareStatement(
            "UPDATE SUBJECT SET name = ? WHERE school_cd = ? AND cd = ?")) {
          st.setString(1, name);
          st.setString(2, schoolCd);
          st.setString(3, cd);
          st.executeUpdate();
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    response.sendRedirect("subjectlist");
  }
}
