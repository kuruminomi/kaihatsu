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

import bean.Subject;
import bean.Teacher;
import dao.DAO;

@WebServlet("/action/subjectlist")
public class SubjectList extends HttpServlet {
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
    List<Subject> subjects = new ArrayList<>();

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {
        try (PreparedStatement st = con.prepareStatement(
            "SELECT cd, name FROM SUBJECT WHERE school_cd = ? ORDER BY cd")) {
          st.setString(1, schoolCd);
          try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
              Subject subj = new Subject();
              subj.setCd(rs.getString("cd"));
              subj.setName(rs.getString("name"));
              subjects.add(subj);
            }
          }
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    request.setAttribute("subjects", subjects);
    request.getRequestDispatcher("/disp/subject_list.jsp").forward(request, response);
  }
}
