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

// Servlet, joka hoitaa kirjautumisen lomakkeen ja autorisoinnin, myös väärän tunnuksen/salasanan raportointi
@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

    // avataan käyttöön perus-datasource
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    // doGet, näyttää kirjautumisen lomakkeen, jos get-pyyntö
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // tulostaa lomakkeen html-sivun
        tulostaKirjautuminen(req, res);

    }

    // hakee kannasta annetun käyttäjänimen, vertaa salasanaa ja joko raportoi onnistumisen tai
    // tulostaa uudestaan kirjautumislomakkeen epäonnistumisen varoituksella
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try (PrintWriter out = res.getWriter()) {

            try (Connection con = ds.getConnection()) {

                // haetaan tietokannasta käyttäjät, jotka vastaavat annettua käyttäjänimeä

                String sql = "SELECT * FROM henkilo WHERE kayttajanimi = ? LIMIT 1;";
                PreparedStatement ps = con.prepareStatement(sql);

                String kayttajanimi = req.getParameter("username");

                ps.setString(1, kayttajanimi);

                ResultSet rs = ps.executeQuery();

                // pohjustettu Henkilo-olion mahdollista käyttöä
                // Henkilo hlo = null;

                // alustetaan epäonnistumisen tai käyttäjän puuttumisen mahdollisuus
                boolean rsempty = true;

                while (rs.next()) {

                    // tarkistus, onko annetulla käyttäjällä etsityssä tulosjoukossa sama salasana kuin annettu
                    // salasanaa ei välitallenneta mihinkään
                    if (req.getParameter("password").equals(rs.getString("salasana"))) {

                        // kerrotaan, että sopiva käyttäjä on löytynyt eikä tulosjoukko ole tyhjä
                        // (ettei tulostu varoitusta epäonnistumisesta)
                        rsempty = false;

                        // luodaan sessio, koska käyttäjä on todennettu
                        HttpSession session = req.getSession(true);

                        // tallennetaan sessioon käyttäjän muut tietokentät kuin salasana
                        session.setAttribute("hloid", rs.getInt("hloid"));
                        session.setAttribute("kayttajanimi", rs.getString("kayttajanimi"));
                        session.setAttribute("nimimerkki", rs.getString("nimimerkki"));
                        session.setAttribute("kuvaus", rs.getString("kuvaus"));
                        session.setAttribute("rooli", rs.getString("rooli"));

                        /* pohjustettu Henkilo-olion käyttöä sessioattribuuttien sijaan
                        hlo = new Henkilo(
                                rs.getInt("henkiloid"),
                                rs.getString("kayttajanimi"),
                                rs.getString("nimimerkki"),
                                rs.getString("kuvaus"),
                                rs.getString("rooli")
                        ); */

                        // tulosta kirjautumisen onnistumisen sivu (navipalkki ja ilmoitus)
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

                // jos salasana ei tästmää tai ei kyseistä käyttäjää kannassa, raportoi epäonnistuminen ja kirjaudu uudestaan
                if (rsempty) {

                    // asettaa requestiin, että kirjautuminen epäonnistui
                    req.setAttribute("loginfailed", "failed");

                    // tulostaa lomakkeen html-sivun uusintayritystä varten
                    tulostaKirjautuminen(req, res);

                }

            } catch (
                    SQLException e) {
                out.println(e.getMessage());
            }

        }

    }

    // tulostaa kirjautumisen lomakkeen, näyttää epäonnistumisen viestin, jos requestissä "loginfailed" == "failed"
    private void tulostaKirjautuminen(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try (PrintWriter out = res.getWriter()) {

            // tulostaa navipalkin, <html>, <body> ja <div> -tagit valmiiksi
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

            // jos requestin "loginfailed" == "failed", tulostaa epäonnistumisen varoituksen
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
