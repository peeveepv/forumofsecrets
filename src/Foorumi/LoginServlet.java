package Foorumi;

import javax.annotation.Resource;
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

                while (rs.next()) {

                    if (req.getParameter("password").equals(rs.getString("salasana"))) {

                        HttpSession session = req.getSession(true);

                        session.setAttribute("henkiloid", rs.getInt("hloid"));
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

                        out.println("<html>");
                        out.println("<head>");

                        out.println("<title>Kaikki viestit</title>");

                        out.println(
                                "<style> p {word-break: break-all;} " +
                                        "#content {position: relative; left: 260px; width: 80%;} " +
                                        "nav {position: fixed; top: 0; width: 240px; height: 100%; font-family: Georgia; " +
                                        "background-color: #333; float: left; clear: left; display: inline; } " +
                                        "nav a, nav span {display: block; padding: 14px 16px; color: antiquewhite; text-shadow: none; " +
                                        "text-decoration: none;} .active {background-color: dimgrey;} " +
                                        "nav a:active, nav a:visited {color: antiquewhite; text-shadow: none;} " +
                                        "nav a:hover {background-color: #111;} " +
                                        "</style>"
                        );

                        out.println("</head>");

                        out.println("<body>");


                        out.println(
                                "<nav> " +
                                        "<span></span>" +
                                        "<span style='font-size: 120%'><strong>Forum of Secrets</strong></span>" +
                                        "<span></span>" +
                                        "<a href='/KeskustelujaViestitServlet'>Keskustelujen lista</a>" +
                                        "<a href='/NaytaKeskustelu'>Yksittäisen keskustelun sivu</a>" +
                                        "<span></span>"
                        );

                        if (session == null
                                || session.getAttribute("kayttajanimi") == null
                                || "anonymous".equals(session.getAttribute("kayttajanimi"))) {

                            out.println("<a href='/Login'>Kirjautuminen</a>");
                            out.println("<a href='/Kayttaja'>Rekisteröityminen</a>");

                        } else {

                            out.println("<span><i>Tällä hetkellä kirjautuneena:</i> "
                                    + session.getAttribute("nimimerkki")
                                    + "</span>");

                            out.println("<a href='/Profiili'>Profiili</a>");
                            out.println("<a href='/Logout'>Uloskirjautuminen</a>");

                        }

                        out.println(
                                "<span></span>" +
                                        "<a href='/Hakukone'>Etsi viestejä</a>" +
                                        "<span></span>" +
                                        "</nav>" +
                                        "" +
                                        "<div id='content'>"
                        );


                        out.println("<br>");
                        out.println("<br>");
                        out.println("<br>");
                        out.println("<h3 style='text-align: center;'>Kirjautuminen onnistui, jatka <a href='index.jsp'>kotisivulle</a> tai <a href='/KeskustelujaViestitServlet'>keskusteluihin</a></h3>");

                        out.println("</div>");

                        out.println("</body>");
                        out.println("</html>");

                    } else {
                        req.setAttribute("loginfailed", "failed");
                        doGet(req, res);
                    }
                }

            } catch (
                    SQLException e) {
                out.println(e.getMessage());
            }

        }

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try (PrintWriter out = res.getWriter()) {

            HttpSession session = req.getSession(false);

            //Tämä tulostaa keskustelualustan pohjan
            res.setContentType("text/html");

            out.println("<html>");
            out.println("<head>");

            out.println("<title>Kaikki viestit</title>");

            out.println(
                    "<style> p {word-break: break-all;} " +
                            "#content {position: relative; left: 260px; width: 80%;} " +
                            "nav {position: fixed; top: 0; width: 240px; height: 100%; font-family: Georgia; " +
                            "background-color: #333; float: left; clear: left; display: inline; } " +
                            "nav a, nav span {display: block; padding: 14px 16px; color: antiquewhite; text-shadow: none; " +
                            "text-decoration: none;} .active {background-color: dimgrey;} " +
                            "nav a:active, nav a:visited {color: antiquewhite; text-shadow: none;} " +
                            "nav a:hover {background-color: #111;} " +
                            "</style>"
            );

            out.println("</head>");

            out.println("<body>");


            out.println(
                    "<nav> " +
                            "<span></span>" +
                            "<span style='font-size: 120%'><strong>Forum of Secrets</strong></span>" +
                            "<span></span>" +
                            "<a href='/KeskustelujaViestitServlet'>Keskustelujen lista</a>" +
                            "<a href='/NaytaKeskustelu'>Yksittäisen keskustelun sivu</a>" +
                            "<span></span>"
            );

            if (session == null
                    || session.getAttribute("kayttajanimi") == null
                    || "anonymous".equals(session.getAttribute("kayttajanimi"))) {

                out.println("<a href='/Login'>Kirjautuminen</a>");
                out.println("<a href='/Kayttaja'>Rekisteröityminen</a>");

            } else {

                out.println("<span style='font-size: 80%'><i>Tällä hetkellä kirjautuneena:</i> "
                        + session.getAttribute("nimimerkki")
                        + "</span>");
                out.println("<span></span>");

                out.println("<a href='/Profiili'>Profiili</a>");
                out.println("<a href='/Logout'>Uloskirjautuminen</a>");

            }

            out.println(
                    "<span></span>" +
                            "<a href='/Hakukone'>Etsi viestejä</a>" +
                            "<span></span>" +
                            "</nav>" +
                            "" +
                            "<div id='content'>"
            );

            out.println("<form method='post'><fieldset>");

            out.println("<legend>Kirjautuminen</legend>");

            out.println("<br>");
            out.println("<label for='username'>Käyttäjänimi</legend>");
            out.println("<input type='text' name='username' focus>");
            out.println("<br>");
            out.println("<label for='password'>Salasana</legend>");
            out.println("<input type='password' name='password'>");
            out.println("<br>");

            out.println("<br>");
            out.println("<input type='submit' value='Kirjaudu'>");

            if ("failed".equals((String) req.getAttribute("loginfailed"))) {
                out.println("<span style='color: red; font-weight: bold;'>   Tunnus tai salasana oli väärin, yritä uudestaan!</span>");
            }

            out.println("<br>");

            out.println("</form></fieldset>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

        }

    }
}
