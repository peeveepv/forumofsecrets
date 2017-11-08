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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name="Profiilisivu", urlPatterns = {"/Profiili"})
public class Profiilisivu extends HttpServlet{
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;
    String kayttajanimi ="";
    String nimimerkki = "";
    String kuvaus ="";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    }
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        ResultSet rs = null;

        try (Connection con = ds.getConnection()){
            String apuNimi = "testi";
            String haeTiedot = "SELECT * from henkilo where kayttajanimi=?";
            PreparedStatement ps = con.prepareStatement(haeTiedot);
            ps.setString(1,apuNimi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                kayttajanimi = rs.getString("kayttajanimi");
                nimimerkki = rs.getString("nimimerkki");
                kuvaus = rs.getString("kuvaus");
            }
            PrintWriter out = res.getWriter();
//            String nimi = rs.getString("kayttajanimi");
            res.setContentType("text/html");
            out.println("<html>" +
                        "<head>" +
                        "<title>Profiilisivu</title>" +
                        "</head>" +
                        "<h2> Profiilin tiedot </h2>" +
                        "<form method=\"post\">" +
                        "<p>Käyttäjänimi: " + kayttajanimi +
                        "<br>Nimimerkki: " + "<input type=\"text\" name=\"nimimerkki\" value=" + nimimerkki + "></input>" +
                        "<br>Kuvaus: " + "<input type=\"text\" name=\"kuvaus\" value=" + kuvaus + "></input>" +
                        "<br><input type=\"submit\" value=\"Päivitä\"/>" +
                        "<body>" +
                        "</body>" +
                        "</html>");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
//    private void haeTiedot(Connection con, ResultSet rs) throws SQLException {
//
//        }
    }

