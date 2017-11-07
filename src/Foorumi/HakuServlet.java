package Foorumi;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.sql.*;

//@WebServlet(name = "Hakukone", urlPatterns = {"/hakukone"})
//public class HakuServlet extends HttpServlet {
public class HakuServlet {



    public static void main(String[] args) {

        Connection con = mockiMetodiConnectionille();

        String testi = vapaaHaku(con, "kolkki");

        System.out.println(testi);

    }


    public static Connection mockiMetodiConnectionille() {

        Connection con = null;

        try {

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/foorumi?useSSL=false",
                    "root", "jaavakahvi");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Yhteys onnistui");

        return con;
    }



//    ************************************************









    public static String vapaaHaku (Connection con, String haettava) {

        StringBuilder palauta = new StringBuilder();

        try {

            String sql1 = "select keskusteluid, nimi from keskustelu where nimi like ?";
            PreparedStatement kyselyLause1 = con.prepareStatement(sql1);
            kyselyLause1.setString(1, haettava);
            ResultSet kyselynTulos1 = kyselyLause1.executeQuery();

            String sql2 = "select kuvaus from keskustelu";

            String sql3 = "select kirjoittaja from viesti";
            String sql4 = "select otsikko from viesti";
            String sql5 = "select viesti from viesti";
            String sql6 = "select vastaus from viesti";

            while (kyselynTulos1.next()) {
                String testi = kyselynTulos1.getString("nimi");
                int testi2 = kyselynTulos1.getInt("keskusteluid");

                if (haettava.contains(testi)) {
                    palauta.append(testi + testi2);
                    // Lisää StringBuilderiin joku metatieto siitä miltä riviltä haettava löytyi,
                    // jotta siihen voi generoida hakulinkin
                palauta.append("<br/>");

// Tee palautus URL --> joka hakee keskustelu/keskusteluID

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.valueOf(palauta);

    }
}
