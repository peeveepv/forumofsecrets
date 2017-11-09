package Foorumi;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "Hakukone", urlPatterns = {"/Hakukone"})
public class HakuServlet extends HttpServlet {

    @Resource(name = "jdbc/Foorumi")
    DataSource ds;


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            NaviPalkki.luoNaviPalkki(request, response, "Foorumin hakusivu");

            // Koska Form-tagista puuttuu "action"-määre, niin toiminto kutsuu tätä samaa servlettiä,
            // eli itseänsä, täältä doGet:istä --> doPost:iin
            out.println("<form method='post' style='width: 400px; position: relative;" +
                    "top: 70px; left: 8%;'>" +
                    "<fieldset>");

            // Luodaan hakulomake
            out.println("<legend>Viestien hakeminen</legend>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td style='width: 140px'><label for='haettava'>Kirjoita etsittävä</legend></td>");
            out.println("<td><input type='text' name='haettava' autofocus></td>");
            out.println("<td style='width: 10px'></td>");
            out.println("<td><span> </span><input type='submit' value='Hae'></td>");
            out.println("</tr>");
            out.println("</table>");

            out.println("</fieldset></form>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");
        }
    }




    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Tallennetaan muuttujaan (String-olioon) käyttäjän hakema merkkijono
        String haettava = (String) request.getParameter("haettava");

        // Luodaan apuväline HTML-sivun ohjausmerkkien (parametrien ja niiden sisältämien tagien) luomiseen
        // ja hakutuloksen URL muodostamiseen
        StringBuilder palauta1 = new StringBuilder();
        StringBuilder palauta2 = new StringBuilder();
        StringBuilder palauta3 = new StringBuilder();
        StringBuilder palauta4 = new StringBuilder();
        StringBuilder palauta5 = new StringBuilder();
        StringBuilder palauta6 = new StringBuilder();

        try {

            // Valmistellaan tietokantaan suoritettavaa kyselylausetta ja ehkäistään
            // "SQL-injektiota" käyttäjän antamien parametrien (haettava merkkijono)
            // syöttämisen rajoittamisella / "kierrättämisellä" kiepautuksen "like ?" kautta
            // (muuttujan tyypillä PreparedStatement).
            // Hakulauseen "case sensitive" ulkoistetaan tietokannan hoidettavaksi samalla "like ?" tavalla.
            // Pyritään jo hakulausekkeessa rajaamaan mahdollisimman paljon suodatusta / suorittamaan
            // tietovirran "pyörittelyä", jotta voidaan ulkoistaa mahdollisimman paljon datan valmistelua / käsittelyä
            // muualle hoidettavaksi eikä sitä tarvitse ohjelmallisesti alkaa miettimään, ratkomaan ja toteuttamaan
            // omassa ohjelmakoodissa.
            String sql1 = "select keskusteluid, nimi from keskustelu where nimi like ?";
            PreparedStatement kyselyLause1 = con.prepareStatement(sql1);

            // Lisätään käyttäjän antamaan hakuehtoon ns. vartalonkatkaisu
            StringBuilder apu1 = new StringBuilder("%" + haettava + "%");

            // Muodostetaan varsinainen tietokantaan lähetettävä SQL-kyselylause
            kyselyLause1.setString(1, apu1.toString());
            // Suoritetaan kysely
            ResultSet kyselynTulos1 = kyselyLause1.executeQuery();

            // Hyödynnetään luotua apuvälinettä käyttäjälle näytettävään hakutulokseen
            palauta1.append("<p>");
            palauta1.append("<fieldset>");
            palauta1.append("<legend>Keskustelujen otsikot</legend>");

            // Luodaan löytyneiden hakutuloksien kappalemäärien seuranta
            int index1 = 1;

            // Tutkitaan tietokannan palauttamaa vastausta niin kauan kuin vastauksessa on rivejä
            while (kyselynTulos1.next()) {

                // String tulosTeksti1 = kyselynTulos1.getString("nimi");
                // Otetaan löytyneeltä tietokantariviltä halutun sarakkeen arvo talteen jatkohyödyntämistä varten
                int tulosID1 = kyselynTulos1.getInt("keskusteluid");

                // Hyödynnetään luotua apuvälinettä käyttäjälle näytettävään hakutulokseen
                // eli luodaan linkki, jonka avulla käyttäjä voi siirtyä alueelle,
                // jossa haettava merkkijon esiintyy
                palauta1.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                palauta1.append(tulosID1);
                palauta1.append("'>");
                palauta1.append(index1);
                palauta1.append(". hakutulos");
                palauta1.append("</a>");
                palauta1.append("<br/>");
                palauta1.append("</p>");

                // Kasvatetaan löytyneiden hakutuloksien kappalemäärää
                index1++;
            }
            palauta1.append("</fieldset>");

            // Suoritetaan ylläkuvattu toiminto erillisessä metodissa sillä poikkeuksella,
            // että jokaisessa metodissa on eri kohde tietokannassa siltä osin mistä haetaan
            // (esim. keskustelun kuvaus tai viestin sisätö tms.)
            palauta2 = suoritaKysely2(con, haettava);
            palauta3 = suoritaKysely3(con, haettava);
            palauta4 = suoritaKysely4(con, haettava);
            palauta5 = suoritaKysely5(con, haettava);
            palauta6 = suoritaKysely6(con, haettava);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        response.setContentType("text/html");

        try (
                PrintWriter out = response.getWriter())

        {

            // Muodostetaan käyttäjälle näytettävä hakutulossivu hyödyntämällä
            // aiemmin luotuja apuvälineitä, jotka sisältävät jo itsessään suoraan HTML-ohjausmerkkejä
            NaviPalkki.luoNaviPalkki(request, response, "Haun tulos");
            out.println("<h1>Haettava esiintyy seuraavilla alueilla</h1>");
            out.println(palauta1.toString());
            out.println("<br/>");
            out.println(palauta2.toString());
            out.println("<br/>");
            out.println(palauta3.toString());
            out.println("<br/>");
            out.println(palauta4.toString());
            out.println("<br/>");
            out.println(palauta5.toString());
            out.println("<br/>");
            out.println(palauta6.toString());
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }



    // ***********************************************************************************************


    private StringBuilder suoritaKysely2(Connection con, String haettava) {
        StringBuilder palauta2 = new StringBuilder();

        try {
            String sql2 = "select keskusteluid, kuvaus from keskustelu where kuvaus like ?";
            PreparedStatement kyselyLause2 = con.prepareStatement(sql2);
            StringBuilder apu2 = new StringBuilder("%" + haettava + "%");
            kyselyLause2.setString(1, apu2.toString());

            ResultSet kyselynTulos2 = kyselyLause2.executeQuery();

            palauta2.append("<p>");
            palauta2.append("<fieldset>");
            palauta2.append("<legend>Keskustelujen kuvaukset</legend>");

            int index2 = 1;

            while (kyselynTulos2.next()) {
                int tulosID2 = kyselynTulos2.getInt("keskusteluid");

                palauta2.append("<p>");
                palauta2.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                palauta2.append(tulosID2);
                palauta2.append("'>");

                palauta2.append(index2);
                palauta2.append(". hakutulos");
                palauta2.append("</p>");
                palauta2.append("</a>");
                index2++;
            }
            palauta2.append("</fieldset>");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return palauta2;
    }


    // ***********************************************************************************************


    private StringBuilder suoritaKysely3(Connection con, String haettava) {
        StringBuilder palauta3 = new StringBuilder();

        try {
            String sql3 = "select keskusteluid, kirjoittaja, nimimerkki from viesti, henkilo where nimimerkki like ?";
            PreparedStatement kyselyLause3 = con.prepareStatement(sql3);
            StringBuilder apu3 = new StringBuilder("%" + haettava + "%");
            kyselyLause3.setString(1, apu3.toString());

            ResultSet kyselynTulos3 = kyselyLause3.executeQuery();

            palauta3.append("<p>");
            palauta3.append("<fieldset>");
            palauta3.append("<legend>Kirjoittajien nimimerkit</legend>");

            int index3 = 1;

            while (kyselynTulos3.next()) {
                int tulosID3 = kyselynTulos3.getInt("keskusteluid");

                palauta3.append("<p>");

                palauta3.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                palauta3.append(tulosID3);
                palauta3.append("'>");

                palauta3.append(index3);
                palauta3.append(". hakutulos");
                palauta3.append("</a>");
                palauta3.append("</p>");
                index3++;
            }
            palauta3.append("</fieldset>");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return palauta3;
    }


    // ***********************************************************************************************

    private StringBuilder suoritaKysely4(Connection con, String haettava) {
        StringBuilder palauta4 = new StringBuilder();

        try {

            String sql4 = "select keskusteluid, otsikko from viesti where otsikko like ?";
            PreparedStatement kyselyLause4 = con.prepareStatement(sql4);
            StringBuilder apu4 = new StringBuilder("%" + haettava + "%");
            kyselyLause4.setString(1, apu4.toString());

            ResultSet kyselynTulos4 = kyselyLause4.executeQuery();

            palauta4.append("<p>");
            palauta4.append("<fieldset>");
            palauta4.append("<legend>Yksittäisten viestien otsikot</legend>");

            int index4 = 1;

            while (kyselynTulos4.next()) {
                int tulosID4 = kyselynTulos4.getInt("keskusteluid");

                palauta4.append("<p>");
                palauta4.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                palauta4.append(tulosID4);
                palauta4.append("'>");

                palauta4.append(index4);
                palauta4.append(". hakutulos");
                palauta4.append("</a>");
                index4++;
            }
            palauta4.append("</fieldset>");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return palauta4;
    }


    // ***********************************************************************************************


    private StringBuilder suoritaKysely5(Connection con, String haettava) {
        StringBuilder palauta5 = new StringBuilder();

        try {

            String sql5 = "select keskusteluid, viesti from viesti where viesti like ?";
            PreparedStatement kyselyLause5 = con.prepareStatement(sql5);
            StringBuilder apu5 = new StringBuilder("%" + haettava + "%");
            kyselyLause5.setString(1, apu5.toString());

            ResultSet kyselynTulos5 = kyselyLause5.executeQuery();

            palauta5.append("<p>");
            palauta5.append("<fieldset>");
            palauta5.append("<legend>Yksittäisten viestien leipäteksteissä</legend>");

            int index5 = 1;

            while (kyselynTulos5.next()) {
                int tulosID5 = kyselynTulos5.getInt("keskusteluid");

                palauta5.append("<p>");
                palauta5.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                palauta5.append(tulosID5);
                palauta5.append("'>");

                palauta5.append(index5);
                palauta5.append(". hakutulos");
                palauta5.append("</a>");
                palauta5.append("</p>");
                index5++;
            }
            palauta5.append("</fieldset>");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return palauta5;
    }


    // ***********************************************************************************************


    private StringBuilder suoritaKysely6(Connection con, String haettava) {
        StringBuilder palauta6 = new StringBuilder();

        try {

            String sql6 = "select keskusteluid, vastaus from viesti where vastaus like ?";
            PreparedStatement kyselyLause6 = con.prepareStatement(sql6);
            StringBuilder apu6 = new StringBuilder("%" + haettava + "%");
            kyselyLause6.setString(1, apu6.toString());

            ResultSet kyselynTulos6 = kyselyLause6.executeQuery();

            palauta6.append("<p>");
            palauta6.append("<fieldset>");
            palauta6.append("<legend>Yksittäisen keskustelusäikeen vastauksissa</legend>");

            int index6 = 1;

            while (kyselynTulos6.next()) {
                int tulosID6 = kyselynTulos6.getInt("keskusteluid");

                palauta6.append("<p>");
                palauta6.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                palauta6.append(tulosID6);
                palauta6.append("'>");

                palauta6.append(index6);
                palauta6.append(". hakutulos");
                palauta6.append("</a>");
                palauta6.append("</p>");
                index6++;
            }
            palauta6.append("</fieldset>");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return palauta6;
    }

}
