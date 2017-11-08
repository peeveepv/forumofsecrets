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

@WebServlet(name="Profiilisivu", urlPatterns = {"/Profiili"})
public class Profiilisivu extends HttpServlet{
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;
    String kayttajanimi ="";
    String nimimerkki = "";
    String kuvaus ="";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String apuNick = req.getParameter("nimimerkki");
        String apuKuvaus = req.getParameter("kuvaus");
        String sql = "UPDATE henkilo SET nimimerkki =?, kuvaus=? WHERE kayttajanimi=?;";
        try {
            Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, apuNick);
            ps.setString(2,apuKuvaus);
            ps.setString(3,kayttajanimi);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        doGet(req,res);
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        ResultSet rs = null;

        HttpSession istunto = req.getSession(false);
        if (istunto.getAttribute("kayttajanimi") == null){
            RequestDispatcher rd = req.getRequestDispatcher(
                    "/Login");
            rd.forward(req, res);
        }

        try (Connection con = ds.getConnection()){
            String apuNimi = "testi";
            apuNimi = (String) istunto.getAttribute("kayttajanimi");
            String haeTiedot = "SELECT * from henkilo where kayttajanimi=?";
            PreparedStatement ps = con.prepareStatement(haeTiedot);
            ps.setString(1,apuNimi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                kayttajanimi = rs.getString("kayttajanimi");
                nimimerkki = rs.getString("nimimerkki");
                kuvaus = rs.getString("kuvaus");
            }
            PrintWriter out = res.getWriter();
//            String nimi = rs.getString("kayttajanimi");
            res.setContentType("text/html");

            //Tämä tulostaa keskustelualustan pohjan
            res.setContentType("text/html");

            out.println("<html>");
            out.println("<head>");

            out.println("<title>Profiili</title>");

            out.println(
                    "<style> td {word-break: break-all; } " +
                            "#content {position: relative; left: 260px; width: 80%;} " +
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
                            "<span style='font-size: 120%'><a href='index.jsp'><strong>Forum of Secrets</strong></a></span>" +
                            "<span></span>" +
                            "<a href='/KeskustelujaViestitServlet'>Keskustelut</a>" +
                            "<span></span>"
            );

            if (istunto == null
                    || istunto.getAttribute("kayttajanimi") == null
                    || "anonymous".equals(istunto.getAttribute("kayttajanimi"))) {

                out.println("<a href='/Login'>Kirjautuminen</a>");
                out.println("<a href='/Kayttaja'>Rekisteröityminen</a>");

            } else {

                out.println("<span style='font-size: 80%'><i>Tällä hetkellä kirjautuneena:</i>");

                if (istunto.getAttribute("nimimerkki") == null) {
                    out.println(istunto.getAttribute("kayttajanimi"));
                } else {
                    out.println(istunto.getAttribute("nimimerkki"));
                }

                out.println("</span>");

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



            out.println("<html>" +
                        "<head>" +
                        "<title>Profiilisivu</title>" +
                        "</head>" +
                        "<body>" +
                        "<h2> Profiilin tiedot </h2>" +
                        "<form method=\"post\">" +
                        "<p>Käyttäjänimi: " + kayttajanimi +
                        "<br>Nimimerkki: " + "<input type=\"text\" name=\"nimimerkki\" value='" + nimimerkki + "'></input>" +
                        "<br>Kuvaus: " + "<input type=\"text\" name=\"kuvaus\" value='" + kuvaus + "'></input>" +
                        "<br><input type=\"submit\" value=\"Päivitä\"/></form>" +
                        "</div>"+
                        "</body>" +
                        "</html>");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    }

