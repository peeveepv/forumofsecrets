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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String haettava = (String) request.getParameter("haettava");

        StringBuilder palauta1 = new StringBuilder();
        StringBuilder palauta2 = new StringBuilder();
        StringBuilder palauta3 = new StringBuilder();
        StringBuilder palauta4 = new StringBuilder();
        StringBuilder palauta5 = new StringBuilder();
        StringBuilder palauta6 = new StringBuilder();

        try {

//            ***********************************************************************************************

            String sql1 = "select keskusteluid, nimi from keskustelu where nimi like ?";
            PreparedStatement kyselyLause1 = con.prepareStatement(sql1);
StringBuilder apu1 = new StringBuilder("%" + haettava + "%");
            kyselyLause1.setString(1, apu1.toString());
            ResultSet kyselynTulos1 = kyselyLause1.executeQuery();

            palauta1.append("<p>");
            palauta1.append("<fieldset>");
            palauta1.append("<legend>Keskustelujen otsikot</legend>");

            int index1 = 1;

            while (kyselynTulos1.next()) {
                String tulosTeksti1 = kyselynTulos1.getString("nimi");
                int tulosID1 = kyselynTulos1.getInt("keskusteluid");

                    palauta1.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                    palauta1.append(tulosID1);
                    palauta1.append("'>");
                    palauta1.append(index1);
                    palauta1.append(". hakutulos");
                    palauta1.append("</a>");
                    palauta1.append("<br/>");
                    palauta1.append("</p>");
                    index1++;
            }
            palauta1.append("</fieldset>");

//            ***********************************************************************************************

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
                String tulosTeksti2 = kyselynTulos2.getString("kuvaus");
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

//            ***********************************************************************************************

            String sql3 = "select keskusteluid, kirjoittaja from viesti where kirjoittaja like ?";
            PreparedStatement kyselyLause3 = con.prepareStatement(sql3);
StringBuilder apu3 = new StringBuilder("%" + haettava + "%");
            kyselyLause3.setString(1, apu3.toString());

            ResultSet kyselynTulos3 = kyselyLause3.executeQuery();

            palauta3.append("<p>");
            palauta3.append("<fieldset>");
            palauta3.append("<legend>Kirjoittajien nimimerkit</legend>");

            int index3 = 1;

            while (kyselynTulos3.next()) {
                String tulosTeksti3 = kyselynTulos3.getString("kirjoittaja");
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

//            ***********************************************************************************************

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
                String tulosTeksti4 = kyselynTulos4.getString("otsikko");
                int tulosID4 = kyselynTulos4.getInt("keskusteluid");

                    palauta4.append("<p>");
                    palauta4.append("<a href='/NaytaKeskustelu?KeskusteluId=");
                    palauta4.append(tulosID4);
                    palauta4.append("'>");

                    palauta4.append(index4);
                    palauta4.append(". hakutulos");
                    palauta4.append("</a>");
                index4++;
                        palauta4.append("</p>");
            }
            palauta4.append("</fieldset>");

//            ***********************************************************************************************

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
                String tulosTeksti5 = kyselynTulos5.getString("viesti");
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


            //            ***********************************************************************************************


            String sql6 = "select keskusteluid, vastaus from viesti where vastaus like ?";
            PreparedStatement kyselyLause6 = con.prepareStatement(sql6);
StringBuilder apu6 = new StringBuilder("%" + haettava + "%");
            kyselyLause6.setString(1, apu6.toString());

            ResultSet kyselynTulos6 = kyselyLause5.executeQuery();

            palauta6.append("<p>");
            palauta6.append("<fieldset>");
            palauta6.append("<legend>Yksittäisen keskustelusäikeen vastauksissa</legend>");

            int index6 = 1;

            while (kyselynTulos6.next()) {
                String tulosTeksti6 = kyselynTulos6.getString("vastaus");
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

            //            ***********************************************************************************************


        } catch (SQLException e) {
            e.printStackTrace();
        }


        response.setContentType("text/html");

       try(
    PrintWriter out = response.getWriter())

    {

        out.println("<html>");
        NaviPalkki.luoNaviPalkki(request, response, "Haun tulos");
        out.println("<body>");
        out.println("<h1>Haettava esiinty seuraavilla alueilla</h1>");
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


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            NaviPalkki.luoNaviPalkki(request, response, "Foorumin hakusivu");
            out.println("<body>");

            // Koska Form-tagista puuttuu "action"-määre, niin toiminto kutsuu tätä samaa servlettiä,
            // eli itseänsä, täältä doGet:istä --> doPost:iin
            out.println("<form method='post'>");

            out.println("Anna yksi hakusana ");
            out.println("<input type='text' name='haettava'>");
            out.println("<input type='submit' value='Lähetä'>");
            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}

