package Foorumi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Haku {

    public static String vapaaHaku (Connection con, String haettava) {

        String sql = "select salasana from henkilo where kayttajanimi like ?";

        PreparedStatement kyselyLause = con.prepareStatement(sql);
        kyselyLause.setString(1, haettava);
        ResultSet kyselynTulos = kyselyLause.executeQuery();

        return "";
    }
}
