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

@WebServlet(name = "Kayttaja", urlPatterns = {"/Kayttaja"})
public class Kayttaja extends HttpServlet{
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        try (PrintWriter out = response.getWriter()) {

            try (Connection con = ds.getConnection()) {
                response.setContentType("text/html");
                out.println("<html>\n" +
                        "<head>\n" +
                        "<title>Rekisteröityminen</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<form method=\"post\">\n" +
                        "<input type=\"text\" name=\"tunnus\"> Käyttäjätunnus</input><br>\n" +
                        " <input type=\"text\" name=\"salasana\"> Salasana</input><br>\n" +
                        " <input type=\"submit\" value=\"OK\"/>\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>");
            } catch (SQLException e){
                out.println(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        try (PrintWriter out = response.getWriter()){
            Connection con = ds.getConnection();
            String tunnus = "";
            String salasana = "";

            tunnus = request.getParameter("tunnus");
            salasana = request.getParameter("salasana");
            String onnistuiko = "Tunnus varattu!"; // Vaihtuu mikäli rekisteröinti onnistuu.

            if (luoKäyttäjä(con, tunnus, salasana)) {
                onnistuiko = "Rekisteröityminen onnistui!";
            }
            //Tulostetaan sivulle miten kävi.
            response.setContentType("text/html");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Rekisteröityminen</title>");
            out.println("</head>");
            out.println("<body><p>" +onnistuiko+ "</p>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ÄLÄ ANNA TYHJIÄ ARVOJA!!, tässä niitä ei ainakaan vielä tarkisteta
    public static boolean luoKäyttäjä(Connection con, String tunnus, String salasana){
        try {
            String haku = "SELECT kayttajanimi FROM henkilo WHERE kayttajanimi=?";
            PreparedStatement pees = con.prepareStatement(haku);
            pees.setString(1, tunnus);
            ResultSet rs = pees.executeQuery();
            int onkoVarattu = 0;
            while (rs.next()) {
                String valiaikainenNimi = rs.getString("kayttajanimi");
                if (tunnus.equals(valiaikainenNimi)) {
                    onkoVarattu++;
                    break;
                }
            }
            if (onkoVarattu == 0){
                    String lisäys = "INSERT INTO henkilo (kayttajanimi, salasana) values (?, ?)";
                    PreparedStatement ps = con.prepareStatement(lisäys);
                    ps.setString(1, tunnus);
                    ps.setString(2, salasana);
                    ps.executeUpdate();
                    return true;
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } return false;
    }
}
