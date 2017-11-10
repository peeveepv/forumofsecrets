package Foorumi;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

// Servlet rakentaa sivun, jossa näytetään yksittäisen keskustelun tiedot ja siihen liittyvät viestit
@WebServlet(name = "NaytaKeskustelu", urlPatterns = {"/NaytaKeskustelu"})
public class NaytaKeskustelu extends HttpServlet {

    // datasource tietojen lukemiseen ja päivittämiseen
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    // datasource viesti/keskustelu-rivien poistamiseen
    @Resource(name = "jdbc/FoorumiDELETE")
    DataSource dsAdmin;

    // poistaa tai lisää viestin / vastauksen doGetin rakentaman sivun pyynnöstä
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        res.setContentType("text/html");

        // alustetaan PrintWriter ja Connection
        PrintWriter out = null;
        Connection con = null;

        // haetaan parametrit pyynnöstä
        Enumeration nimet = req.getParameterNames();
        List<String> lista = new ArrayList<>();

        // läpikäydään parametrit
        while (nimet.hasMoreElements()) {
            lista.add((String) nimet.nextElement());
        }

        boolean moi = false;

        //Jos parametri on tyhjä String, viestiä ei lisätä
        while(moi==false) {
            if ("".equals(req.getParameter("vastaus")) || "".equals(req.getParameter("viesti")) || "".equals(req.getParameter("otsikko"))) {
                moi = true;
                break;
            }

            // jos parametrinä oli "vastaus", lisätään pyydetty viesti uutena vastauksena
            if (lista.contains("vastaus")) {
                String sql = "INSERT INTO viesti (otsikko, viesti, kirjoittaja ,keskusteluid, vastaus) VALUES (vastaus ,?, ?, ?, ?)";

                String viesti = req.getParameter("vastaus");
                int kirjoittaja = Integer.parseInt(req.getParameter("kirjoittaja"));
                int keskusteluid = Integer.parseInt(req.getParameter("keskusteluid"));
                int vastaus = Integer.parseInt(req.getParameter("viestiID"));

                try {

                    con = ds.getConnection();
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, viesti);
                    stmt.setInt(2, kirjoittaja);
                    stmt.setInt(3, keskusteluid);
                    stmt.setInt(4, vastaus);
                    stmt.executeUpdate();
                    moi = true;

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


            // jos parametrinä oli "poista", poistetaan pyydetty viesti
            else if (lista.contains("poista")) {

                String sql = "DELETE from viesti where id = ?";
                int id = Integer.parseInt(req.getParameter("poista")); //Palauttaa poistettavan viestin ID:n

                try {
                    con = dsAdmin.getConnection();
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    moi = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // jos ei poistamisen tai vastauksen parametriä, kyseessä on viestin lisääminen, jolloin lisätään pyydetty uusi viesti
            else {

                String sql = "INSERT INTO viesti (otsikko, viesti, kirjoittaja ,keskusteluid) VALUES (?, ?, ?, ?)";
                String keskustelunNimi = req.getParameter("otsikko");
                String keskustelukuvaus = req.getParameter("viesti");
                int kirjoittaja = Integer.parseInt(req.getParameter("kirjoittaja"));
                int keskusteluid = Integer.parseInt(req.getParameter("keskusteluid"));

                try {

                    con = ds.getConnection();
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, keskustelunNimi);
                    stmt.setString(2, keskustelukuvaus);
                    stmt.setInt(3, kirjoittaja);
                    stmt.setInt(4, keskusteluid);
                    stmt.executeUpdate();
                    moi = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        //Jos update onnistuu, niin palataan takaisin doGet-metodiin, siellä näkyy lisätty viesti / vastaus
        if(moi)
            doGet(req, res);
        else
            out.println("<h2>Hups.. tapahtui virhe! Käänny Jaken puoleen</h2>");
    }


    // näyttää taulukon olemassaolevista viesteistä ja vastauksista keskustelussa
    // näyttää myös lomakkeen uuden viestin lisäämiseksi ja kentät uusien vastausten lisäämiseksi
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // tarkistetaan onko sessiota
        HttpSession session = req.getSession(false);

        ResultSet rsviestit = null;
        ResultSet rsvastaukset = null;
        ResultSet rskeskustelu = null;
        ResultSet rshenkilot = null;

        String keskustelunimi = "", keskustelukuvaus = "";

        try (PrintWriter out = res.getWriter()) {

            try (Connection con = ds.getConnection()) {

                int keskusteluid;

                // jos sivulle tullaan antamalla KeskusteluID-parametri, näytetään kyseinen keskustelu
                // muuten näytetään ensimmäinen "Yleinen" keskustelu
                if (req.getParameter("KeskusteluId") != null) {
                    keskusteluid = Integer.parseInt(req.getParameter("KeskusteluId"));
                } else {
                    keskusteluid = 1;
                }

                // haetaan viestit, jotka liittyvät kyseiseen keskusteluun
                String sql = "SELECT * FROM viesti WHERE keskusteluid = " + keskusteluid + " ORDER BY kirjoitettu ASC;";
                PreparedStatement ps = con.prepareStatement(sql);

                //haetaan tulosjoukko rsviestit
                rsviestit = ps.executeQuery();

                sql = "SELECT * FROM keskustelu WHERE keskusteluid = " + keskusteluid + ";";
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

                // pohjustetaan oletuskirjoittajaksi "anonymous"-henkilö
                int kirjoittajaID = 2;

                // jos istunnossa on henkilöid (kirjautunut), vaihdetaan kirjoittajaksi käyttäjä
                if(session.getAttribute("hloid")!= null)
                    kirjoittajaID = (Integer)(session.getAttribute("hloid"));

                // tulostetaan HTML-sivun alkuosa (<html>, <head> tyyleineen, <body> sekä avaava content-<div>
                NaviPalkki.luoNaviPalkki(req, res, "Keskustelu");

                // tulostetaan kyseisen keskustelun nimi ja kuvaus
                out.println("<h2><br>Keskustelu:<br> " + keskustelunimi + "</h2>");
                out.println("<h3>Kuvaus: <i>" + keskustelukuvaus + "</i></h3>");

                // alustetaan lista Viestit-olioita varten
                List<Viestit> lista = new ArrayList<>();

                // luodaan jokaisesta keskustelun viestistä Viestit-olio ja lisätään listaan
                while(rsviestit.next()) {
                    Viestit olio = new Viestit(rsviestit.getInt("id"),
                            rsviestit.getInt("kirjoittaja"),
                            rsviestit.getInt("keskusteluid"),
                            rsviestit.getString("otsikko"),
                            rsviestit.getString("viesti"),
                            rsviestit.getInt("vastaus"),
                            rsviestit.getDate("kirjoitettu"));
                    lista.add(olio);
                }

                // avataan taulukko viestien ja vastausten esittämiseen
                out.println("<table border: 1px solid black>");

                // tulostetaan listalta jokainen viesti ja siihen liittyvät vastaukset
                for (int i = 0; i <lista.size() ; i++) {

                    int vastausid = lista.get(i).getVastaus();

                    // jos viesti ei ole vastaus (vastausid = default(0)), niin tulostetaan viestinä
                    if (vastausid == 0) {

                        out.println("<tr><td colspan='4'><hr></td><td></td><td></td><td></td></tr>");
                        out.println("<tr>");
                        out.println("<td style='width: 200px'> Otsikko: <br>" + lista.get(i).getOtsikko() +
                                "</td><td style='width: 120px'> Kirjoittaja: <br>"
                                + "<a href='/Profiili?hloid=" +  lista.get(i).getKirjoittaja() + "'>"
                                + kirjoittajat.get(lista.get(i).getKirjoittaja()) + "</a></td>" +
                                "<td style='width: 80px'>Kirjoitettu: <br>" + lista.get(i).getFormatoituKirjoitettu() + "</td>" +
                                "<td style='width: 400px';>" + lista.get(i).getViesti() + "</td>" +
                                "<td>");

                                // tulostetaan lomake vastauksen lähettämiseen
                                // (ja sen kirjoittajaksi nykyinen käyttäjä tai anonymous)
                                out.println("<form method='post' id=2>");
                                out.println("<input type=submit  value='Vastaa'>");
                                out.println("<input type=text maxlength='254' name='vastaus' placeholder='vastaus'><br>");
                                out.println("<input type=hidden name='kirjoittaja' value=" + kirjoittajaID + ">");
                                out.println("<input type=hidden name='keskusteluid' value=" + keskusteluid + ">");
                                out.println("<input type=hidden name='viestiID' value=" + lista.get(i).getId() + ">");
                                out.println("</form>");

                                // näytetään poistamisen painike viestille, jos käyttäjän rooli on "admin" (istunnosta)
                                if("admin".equals((String)session.getAttribute("rooli"))){
                                    out.println("<br><form method=post><input type=submit value=poista>" +
                                            "<input type=hidden name=poista value= "+lista.get(i).getId() +"></form>");
                                }

                        out.println("</td>" +
                                "</tr>");

                        // käydään läpi kyseisen viestin mahdolliset vastaukset listalta ja tulostetaan ne
                        for (int j = 0; j < lista.size(); j++) {
                            if (lista.get(j).getVastaus() == lista.get(i).getId()) {
                                out.println("<tr><td><br></td></tr><tr><td></td><td>" +
                                        "<a href='/Profiili?hloid=" +  lista.get(j).getKirjoittaja() + "'>"
                                        + kirjoittajat.get(lista.get(j).getKirjoittaja()) + "</a></td>" +
                                        "<td>" + lista.get(j).getFormatoituKirjoitettu() + "</td>" +
                                        "<td>" + lista.get(j).getViesti() + "</td><td></td></tr>");
                            }
                        }
                    }
                }

                out.println("</table>");

                // Viestien listaamisen ja viestin lisäämisen lomakkeen välinen horizontal ruler <hr>
                out.println("<br>");
                out.println("<hr>");
                out.println("<br>");

                //String kirjoittajaI = (String) session.getAttribute("hloid");
                //out.print(kirjoittajaID);
                ///out.print(kirjoittajaI);
                //Tässä on lomake uuden viestin luomiseen
//                out.println("<form method='post' id=1>");
//                out.println("<input type=submit  value='Lisää uusi viesti'> <p></p>");
//                out.println("<input type=text name='otsikko' value='otsikko'><br>");
//                out.println("<input type=hidden name='kirjoittaja' value=" +kirjoittajaID + ">");
//                out.println("<input type=hidden name='keskusteluid' value=" + keskusteluid + ">");
//                out.println("</form>");
//                out.println("<textarea form=1 name='viesti' value='viesti' row=5 column=10></textarea>");

                // tulostetaan lomake viestin lisäämiseen
                out.println("<form method='post' id=1 style='width: 400px;" +
                        "top: 70px; left: 8%;'>" +
                        "<fieldset>");

                    out.println("<legend>Lisää viesti</legend>");

                    out.println("<table>");

                        out.println("<tr><td><input type=submit  value='Lisää uusi viesti'> </td></tr>");
                        out.println("<tr>");
                            out.println("<td style='width: 120px'><label for='nimi'>Aihe</legend></td>");
                            out.println("<td><input type=text maxlength='50' name='otsikko' placeholder='otsikko'></td>");
                        out.println("</tr>");
                        out.println("<tr>");
                            out.println("<td style='width: 120px'><label for='kuvaus'>Viesti</legend></td>");
                            out.println("<td><textarea maxlength='400' form=1 name='viesti' placeholder='viesti' row=5 column=10></textarea></td>");
                        out.println("</tr>");

                        out.println("<input type=hidden name='kirjoittaja' value=" +kirjoittajaID + ">");
                        out.println("<input type=hidden name='keskusteluid' value=" + keskusteluid + ">");

                    out.println("</table>");

                out.println("</fieldset></form>");

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

            } catch (
                    SQLException e)
            {
                out.println(e.getMessage());
            }
        }
    }
}