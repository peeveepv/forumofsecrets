package Foorumi;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name="Profiilisivu", urlPatterns = {"/Profiilisivu"})
public class Profiilisivu {
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        doGet(req, res);
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ResultSet rs = null;
        try {
            Connection con = ds.getConnection();
            haeTiedot(con, rs);
            try (PrintWriter out = res.getWriter()) {
                String nimi = rs.getString("kayttajanimi");
                res.setContentType("text/html");
                out.println("<html>" +
                        "<head>" +
                        "<title>Profiilisivu</title>" +
                        "</head>" +
                        "<p>" + nimi + "</p>" +
                        "<body>" +
                        "</body>" +
                        "</html>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void haeTiedot(Connection con, ResultSet rs){
    String kayttajannimi = "testi";
    String haeTiedot = "SELECT * from henkilo where 'kayttajanimi'= ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(haeTiedot);
            ps.setString(1,kayttajannimi);
            rs = ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        }
    }

