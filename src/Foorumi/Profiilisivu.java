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
public class Profiilisivu extends HttpServlet {
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;
    String kayttajanimi = "";
    String nimimerkki = "";
    String kuvaus = "";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        //Haetaan uudet tiedot
        String apuNick = req.getParameter("nimimerkki");
        String apuKuvaus = req.getParameter("kuvaus");
        //Päivitetään tietokanta
        String sql = "UPDATE henkilo SET nimimerkki =?, kuvaus=? WHERE kayttajanimi=?;";
        try {
            Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, apuNick);
            ps.setString(2, apuKuvaus);
            ps.setString(3, kayttajanimi);
            ps.executeUpdate();
            HttpSession istunto = req.getSession(false);

            //Tallennetaan päivitetyt tiedot istuntoon
            istunto.setAttribute("nimimerkki", apuNick);
            istunto.setAttribute("kuvaus", apuKuvaus);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Tulostetaan sama sivu uusilla tiedoilla.
        doGet(req, res);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession istunto = req.getSession(false);
        String istuntoId;

        //Ohjataan kirjautumissivulle mikäli istuntoa ei ole.
        if (istunto.getAttribute("hloid") == null) {
            RequestDispatcher rd = req.getRequestDispatcher(
                    "/Login");
            rd.forward(req, res);
        }

        //Tarkistetaan onko tutkailtavaa profiilisivua lupa muuttaa
        String paramId = req.getParameter("hloid");
        istuntoId = istunto.getAttribute("hloid").toString();

        //Mikäli sivulle ei tulla parametrin kanssa, asetetaan katsottavaksi profiiliksi oma
        if (req.getParameter("hloid") == null) {
            paramId = istuntoId;
        }
        try (Connection con = ds.getConnection()) {
            // Hakee tiedot hloid:n perusteella
            String haeTiedot = "SELECT * from henkilo where hloid=?";
            PreparedStatement ps = con.prepareStatement(haeTiedot);
            ps.setString(1, paramId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                kayttajanimi = rs.getString("kayttajanimi");
                nimimerkki = rs.getString("nimimerkki");
                kuvaus = rs.getString("kuvaus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        NaviPalkki.luoNaviPalkki(req, res, "Profiilisivu");
            //Tulostetaan sivu sen perusteella onko oikeus muokata vai ei
            if (istuntoId.equals(paramId)) {
                tulostaLomake(res);
            } else{
                tulostaTiedot(res);
            }
        }

    private void tulostaLomake(HttpServletResponse res) {
        try (PrintWriter out = res.getWriter()) {
            out.println("<form method='post' id=1 style='width: 480px; position: relative; top: 70px; left: 8%;'><fieldset>");
            out.println("<legend>Profiilin tiedot</legend>");
            out.println("<table>" +
                    "<tr>" +
                    "<td style='width: 220px'><label for='kayttajanimi'>Käyttäjänimi: </label></td>" +
                    "<td>" + kayttajanimi + "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style='width: 220px'><label for='nimimerkki'>Nimimerkki (max 20 merkkiä): </label></td>" +
                    "<td><input type='text' name='nimimerkki' maxlength=20 focus value='" + nimimerkki + "'></td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style='width: 220px'><label for='kuvaus'>Kuvaus (max 200 merkkiä): </label></td>" +
                    "<td><textarea maxlength='200' form=1 name='kuvaus' row=10 column=30>" + kuvaus + "</textarea></td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td><input type='submit' value='Päivitä'></td>" +
                    "</tr>" +
                    "</table>" +
                    "</fieldset>" +
                    "</form>" +
                    "</div>" +
                    "</body>" +
                    "</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tulostaTiedot(HttpServletResponse res){
        try (PrintWriter out = res.getWriter()) {
            out.println(
                "<form style='width: 400px; position: relative; top: 70px; left: 8%;'>" +
                    "<fieldset>" +
                        "<legend> Profiilin tiedot </legend>" +
                            "<table>" +
                            "<tr>" +
                            "<td style='width: 120px'><label for='kayttajanimi'>Käyttäjänimi: </label></td>" +
                            "<td>" + kayttajanimi + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style='width: 120px'><label for='nimimerkki'>Nimimerkki: </label></td>" +
                            "<td>" + nimimerkki + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td style='width: 120px'>" + "<label for='kuvaus'>Kuvaus: </label></td>" +
                            "<td>" + kuvaus +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</fieldset>" +
                            "</div>" +
                            "</body>" +
                            "</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

