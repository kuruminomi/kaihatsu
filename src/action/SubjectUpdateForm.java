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

import bean.Subject;
import bean.Teacher;
import dao.DAO;

@WebServlet("/disp/subjectupdateform")
public class SubjectUpdateForm extends HttpServlet {
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
    String cd = request.getParameter("cd");
    Subject subj = new Subject();

    try {
      DAO dao = new DAO();
      try (Connection con = dao.getConnection()) {
        try (PreparedStatement st = con.prepareStatement(
            "SELECT name FROM SUBJECT WHERE school_cd = ? AND cd = ?")) {
          st.setString(1, schoolCd);
          st.setString(2, cd);
          try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
              subj.setCd(cd);
              subj.setName(rs.getString("name"));
            } else {
              throw new Exception("該当する科目が見つかりません。");
            }
          }
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    request.setAttribute("subject", subj);
    request.getRequestDispatcher("/disp/subject_updateform.jsp").forward(request, response);
  }
}
