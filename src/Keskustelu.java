import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Keskustelu {

    public static List<KeskusteluOlio> haeKeskustelut(){
        List<KeskusteluOlio> lista = new ArrayList<KeskusteluOlio>();
        Connection con = mockiMetodiConnectionille();
        lista = haeKaikkiKeskustelut(con);
        for (KeskusteluOlio olio: lista) {
            System.out.println(olio);
        }
        return lista;
    }

    public static Connection mockiMetodiConnectionille() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/foorumi?useSSL=false",
                    "root", "adminadmin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Yhteys onnistui");
        return con;
    }

    public static List<KeskusteluOlio> haeKaikkiKeskustelut(Connection con) {
        List<KeskusteluOlio> lista = new ArrayList<KeskusteluOlio>();
        String sql = "Select * from keskustelu";
        ResultSet rs = null;
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                KeskusteluOlio olio = new KeskusteluOlio(rs.getInt("keskusteluId"),
                        rs.getString("nimi"), rs.getString("kuvaus"));
                lista.add(olio);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}

class KeskusteluOlio{
    int keskusteluId;
    String nimi;
    String kuvaus;

    public KeskusteluOlio(int id, String nimi, String kuvaus){
        this.keskusteluId = id;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
    }

    public int getKeskusteluId() {
        return keskusteluId;
    }

    public void setKeskusteluId(int keskusteluId) {
        this.keskusteluId = keskusteluId;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String toString() {
        return "KeskusteluOlio{" +
                "keskusteluId=" + keskusteluId +
                ", nimi='" + nimi + '\'' +
                ", kuvaus='" + kuvaus + '\'' +
                '}';
    }
}