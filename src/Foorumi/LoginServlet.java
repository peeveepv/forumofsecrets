package Foorumi;

import javax.annotation.Resource;
import javax.print.attribute.standard.MediaSize;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

    // import javax.annotation.Resource;
    // import javax.sql.DataSource;
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try (PrintWriter out = res.getWriter()) {

            try (Connection con = ds.getConnection()) {

                String sql = "SELECT * FROM henkilo WHERE kayttajanimi = ? LIMIT 1;";
                PreparedStatement ps = con.prepareStatement(sql);

                String kayttajanimi = req.getParameter("username");

                ps.setString(1, kayttajanimi);

                ResultSet rs = ps.executeQuery();

//                Henkilo hlo = null;

                boolean rsempty = true;

                while (rs.next()) {

                    if (req.getParameter("password").equals(rs.getString("salasana"))) {

                        rsempty = false;

                        HttpSession session = req.getSession(true);

                        session.setAttribute("hloid", rs.getInt("hloid"));
                        session.setAttribute("kayttajanimi", rs.getString("kayttajanimi"));
                        session.setAttribute("nimimerkki", rs.getString("nimimerkki"));
                        session.setAttribute("kuvaus", rs.getString("kuvaus"));
                        session.setAttribute("rooli", rs.getString("rooli"));

                        /*
                        hlo = new Henkilo(
                                rs.getInt("henkiloid"),
                                rs.getString("kayttajanimi"),
                                rs.getString("nimimerkki"),
                                rs.getString("kuvaus"),
                                rs.getString("rooli")
                        ); */

                        res.setContentType("text/html");

                        NaviPalkki.luoNaviPalkki(req, res, "Kirjautuminen");

                        out.println("<br>");
                        out.println("<br>");
                        out.println("<br>");
                        out.println("<h3 style='position: relative; left: 8%; color: antiquewhite;'>Kirjautuminen onnistui, jatka <a href='index.jsp'>kotisivulle</a> tai <a href='/KeskustelujaViestitServlet'>keskusteluihin</a></h3>");

                        out.println("</div>");

                        out.println("</body>");
                        out.println("</html>");

                    }

                }

                if (rsempty) {

                    req.setAttribute("loginfailed", "failed");
                    tulostaKirjautuminen(req, res);

                }

            } catch (
                    SQLException e) {
                out.println(e.getMessage());
            }

        }

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        tulostaKirjautuminen(req, res);

    }

    private void tulostaKirjautuminen(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try (PrintWriter out = res.getWriter()) {

            // tulostaa navipalkin
            NaviPalkki.luoNaviPalkki(req, res, "Kirjautuminen");

            out.println("<form method='post' style='width: 400px; position: relative;" +
                    "top: 70px; left: 8%;'>" +
                    "<fieldset>");

            out.println("<legend>Kirjautuminen</legend>");

            out.println("<table>");
                out.println("<tr>");
                    out.println("<td style='width: 120px'><label for='username'>Käyttäjänimi</legend></td>");
                    out.println("<td><input type='text' name='username' focus></td>");
                out.println("</tr>");
                out.println("<tr>");
                    out.println("<td style='width: 120px'><label for='password'>Salasana</legend></td>");
                    out.println("<td><input type='password' name='password'></td>");
                out.println("</tr>");
                out.println("<tr>");
                    out.println("<td><input type='submit' value='Kirjaudu'></td>");
                out.println("</tr>");
            out.println("</table>");

            if ("failed".equals((String)req.getAttribute("loginfailed"))) {
                out.println("<span style='color: red; font-weight: bold;'>   Tunnus tai salasana oli väärin, yritä uudestaan!</span>");
            }

            out.println("</fieldset></form>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        }
    }
}
