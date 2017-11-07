package Foorumi;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "Hakukone", urlPatterns = {"/hakukone"})
public class HakuServlet extends HttpServlet {

    public static String vapaaHaku (Connection con, String haettava) {

        String sql = "select otsikko from viesti";

        PreparedStatement kyselyLause = con.prepareStatement(sql);
//        kyselyLause.setString(1, haettava);
        ResultSet kyselynTulos = kyselyLause.executeQuery();

        while(kyselynTulos.next()) {

        }

        return "";
    }
}
