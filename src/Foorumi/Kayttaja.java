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


                NaviPalkki.luoNaviPalkki(request, response, "Rekisteröityminen");

                out.println("<form method='post' style='width: 400px; position: relative;" +
                        "top: 70px; left: 8%;'>" +
                        "<fieldset>");

                out.println("<legend>Rekisteröityminen</legend>");

                out.println("<table>");
                    out.println("<tr>");
                       out.println("<td style='width: 120px'><label for='tunnus'>Käyttäjänimi</legend></td>");
                       out.println("<td><input type='text' name='tunnus' focus></td>");
                    out.println("</tr>");
                    out.println("<tr>");
                       out.println("<td style='width: 120px'><label for='salasana'>Salasana</legend></td>");
                       out.println("<td><input type='password' name='salasana'></td>");
                    out.println("</tr>");
                    out.println("<tr>");
                       out.println("<td><input type='submit' value='Rekisteröi'></td>");
                    out.println("</tr>");
                out.println("</table>");

                out.println("</fieldset>"+
                        "</form>" +
                        "</div>" +
                        "</body>" +
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

            NaviPalkki.luoNaviPalkki(request, response, "Rekisteröityminen");

            out.println("<br>");
            out.println("<br>");
            out.println("<br>");
            out.println("<h3 style='text-align: center; color: antiquewhite;'>"+ onnistuiko + ", jatka <a href='index.jsp'>kotisivulle</a> tai <a href='/KeskustelujaViestitServlet'>keskusteluihin</a></h3>");

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");
//            out.println("<p>" +onnistuiko+ "</p>");
//            out.println("<p>Back to the <a href='index.jsp'>index</a></p>");
//
//            out.println("</div>");
//            out.println("</body>");
//            out.println("</html>");

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
                    String lisäys = "INSERT INTO henkilo (kayttajanimi, salasana, rooli) values (?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(lisäys);
                    ps.setString(1, tunnus);
                    ps.setString(2, salasana);
                    ps.setString(3, "2");
                    ps.executeUpdate();
                    return true;
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } return false;
    }
}
