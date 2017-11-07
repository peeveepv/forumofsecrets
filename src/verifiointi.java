import java.sql.*;

public class verifiointi {


    public static void main(String[] args) {
        Connection con = mockiMetodiConnectionille();
        boolean totuustesti = paasynHallinta(con, "jukka", "sukka");

        System.out.println(totuustesti);
    }


    public static Connection mockiMetodiConnectionille() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/foorumi?useSSL=false",
                    "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Yhteys onnistui");
        return con;
    }



//    ************************************************


    public static boolean paasynHallinta(Connection con, String tunnus, String sala){

        boolean tulos = false;

        try {

            String sql = "select salasana from henkilo where kayttajanimi like ?";

            PreparedStatement kyselyLause = con.prepareStatement(sql);
            kyselyLause.setString(1, tunnus);
            ResultSet kyselynTulos = kyselyLause.executeQuery();

            kyselynTulos.getString("salasana");

            if (sala.equals(kyselynTulos.getString("salasana"))) {
                tulos = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tulos;
    }
}


