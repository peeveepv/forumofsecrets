package Foorumi;

import javax.annotation.Resource;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NaytaViesti", urlPatterns = {"/NaytaViesti"})
public class NaytaViesti extends HttpServlet {

    // datasource tietojen lukemiseen ja päivittämiseen
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // tarkistetaan onko sessiota
        HttpSession session = req.getSession(false);

        ResultSet rsviesti = null;
        ResultSet rsvastaukset = null;
        ResultSet rskeskustelu = null;
        ResultSet rshenkilot = null;

        String keskustelunimi = "", keskustelukuvaus = "";
        int keskusteluId = 1, viestiId = 1;

        Viestit viesti = null;

        try (PrintWriter out = res.getWriter()) {

            try (Connection con = ds.getConnection()) {

                if (req.getParameter("id") != null) {
                    viestiId = Integer.parseInt(req.getParameter("id"));
                }

                // haetaan viesti, jonka id on annettu parametrina
                String sql = "SELECT * FROM viesti WHERE id = " + viestiId + ",";
                PreparedStatement ps = con.prepareStatement(sql);

                //haetaan tulosjoukko rsviesti
                rsviesti = ps.executeQuery();

                while (rsviesti.next()) {
                    keskusteluId = rsviesti.getInt("kirjoittaja");
                    viesti = new Viestit(
                            rsviesti.getInt("id"),
                            rsviesti.getInt("kirjoittaja"),
                            rsviesti.getInt("keskusteluId"),
                            rsviesti.getString("otsikko"),
                            rsviesti.getString("viesti"),
                            rsviesti.getInt("vastaus"),
                            rsviesti.getDate("kirjoitettu")
                    );
                }

                sql = "SELECT * FROM keskustelu WHERE keskusteluId = " + keskusteluId + ";";
                ps = con.prepareStatement(sql);

                // haetaan kyseisen keskustelun tiedot tulosjoukkona rskeskustelu
                rskeskustelu = ps.executeQuery();

                // haetaan tulosjoukosta keskustelun nimi ja kuvaus tulostusta varten
                while (rskeskustelu.next()) {
                    keskustelunimi = rskeskustelu.getString("nimi");
                    keskustelukuvaus = rskeskustelu.getString("kuvaus");
                }

                sql = "SELECT * FROM henkilo;";
                ps = con.prepareStatement(sql);

                // haetaan kaikki mahdolliset viestien kirjoittajat tulosjoukkoon rshenkilot
                rshenkilot = ps.executeQuery();

                // valmistellaan Map viestin mahdollisista kirjoittajista hloid perusteella per viesti -tulostusta varten
                Map<Integer, String> kirjoittajat = new HashMap<>();

                // laitetaan kirjoittajat hashmappiin henkiloid:n mukaan
                while (rshenkilot.next()) {
                    kirjoittajat.put(
                            rshenkilot.getInt("hloid"),
                            rshenkilot.getString("nimimerkki")
                    );
                }

                // tulostetaan HTML-sivun alkuosa (<html>, <head> tyyleineen, <body> sekä avaava content-<div>
                NaviPalkki.luoNaviPalkki(req, res, "Viesti");

                // tulostetaan kyseisen keskustelun nimi ja kuvaus
                out.println("<h2><br>Viesti:<br> " + viesti.getOtsikko() + "</h2>");
                out.println("<h3>Viestiteksti: <i>" + viesti.getViesti() + "</i></h3>");

                // tulostetaan viestin isäntäviesti ja keskustelu linkkeinä, jos kyseessä on vastaus
                if (viesti.getVastaus() != 0) {

                    out.println(
                            "<br><h3>Viesti on vastaus toiseen " +
                                    "<a href='/NaytaViesti?id=" + viesti.getVastaus() + "'>viestiin</a></h3>" +
                                    "<br>keskustelussa <a href='/NaytaKeskustelu?KeskusteluId=" + viesti.getKeskusteluid() +
                                    "'>" + keskustelunimi + "</a>.");

                    // muuten tulostetaan kyseisen viestin vastaukset
                } else {

                    // alustetaan lista vastauksien Viestit-olioita varten
                    List<Viestit> vastaukset = new ArrayList<>();

                    // luodaan jokaisesta kyseisen viestin vastausviestistä Viestit-olio ja lisätään vastausten listaan
                    while (rsvastaukset.next()) {
                        if (rsvastaukset.getInt("vastaus") == viesti.getId()) {

                            Viestit olio = new Viestit(
                                    rsvastaukset.getInt("id"),
                                    rsvastaukset.getInt("kirjoittaja"),
                                    rsvastaukset.getInt("keskusteluId"),
                                    rsvastaukset.getString("otsikko"),
                                    rsvastaukset.getString("viesti"),
                                    rsvastaukset.getInt("vastaus"),
                                    rsvastaukset.getDate("kirjoitettu")
                            );

                            vastaukset.add(olio);
                        }
                    }

                    // avataan taulukko vastausten esittämiseen
                    out.println("<table border: 1px solid black>");

                    // tulostetaan listalta jokainen vastaus
                    for (int i = 0; i < vastaukset.size(); i++) {

                        out.println(
                                "<tr>" +
                                        "<td colspan='4'><hr></td><td></td><td></td><td></td>" +
                                        "</tr>" +
                                        "<tr>" +
                                        "<td style='width: 200px'> Otsikko: <br>" + vastaukset.get(i).getOtsikko() + "</td>" +
                                        "<td style='width: 200px'> Kirjoittaja: <br>"
                                        + "<a href='/Profiili?hloid=" + vastaukset.get(i).getKirjoittaja() + "'>"
                                        + kirjoittajat.get(vastaukset.get(i).getKirjoittaja()) + "</a></td>" +
                                        "<td style='width: 400px';>" + vastaukset.get(i).getViesti() + "</td>" +
                                        "<td></td>" +
                                        "</tr>"
                        );
                    }

                    out.println("</table>");

                    out.println("</div>");

                    out.println("</body>");
                    out.println("</html>");

                }
            } catch (SQLException e) {
                out.println(e.getMessage());
            }
        }
    }

}
