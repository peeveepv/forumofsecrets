package Foorumi;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.sql.*;

@WebServlet(name = "Hakukone", urlPatterns = {"/hakukone"})
public class HakuServlet extends HttpServlet {



    public static void main(String[] args) {

        Connection con = mockiMetodiConnectionille();

        String testi = vapaaHaku(con, "kukkaruukku");

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

        try {

            String sql1 = "select nimi from keskustelu";
            String sql2 = "select kuvaus from keskustelu";

            String sql3 = "select kirjoittaja from viesti";
            String sql4 = "select otsikko from viesti";
            String sql5 = "select viesti from viesti";
            String sql6 = "select vastaus from viesti";

            PreparedStatement kyselyLause1 = con.prepareStatement(sql1);
            ResultSet kyselynTulos1 = kyselyLause1.executeQuery();


            while (kyselynTulos1.next()) {
                String testi = kyselynTulos1.getString("name");
                System.out.println(testi);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "joo";

    }
}
