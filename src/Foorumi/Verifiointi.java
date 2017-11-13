package Foorumi;

import java.sql.*;

public class Verifiointi {

    public static boolean paasynHallinta(Connection con, String tunnus, String sala){

        boolean tulos = false;

        try {

            String sql = "select salasana from henkilo where kayttajanimi like ?";

            PreparedStatement kyselyLause = con.prepareStatement(sql);
            kyselyLause.setString(1, tunnus);
            ResultSet kyselynTulos = kyselyLause.executeQuery();

            kyselynTulos.next();

            // MIKA: Tarkistetaan täsmääkö annettu salasana tietokannassa olevaan salasanaan
            if (sala.equals(kyselynTulos.getString("salasana"))) {
                tulos = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tulos;
    }
}
