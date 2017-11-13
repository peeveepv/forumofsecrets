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

@WebServlet(name = "Rekisterointi", urlPatterns = {"/Rekisterointi"})
public class Rekisterointi extends HttpServlet{
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        try (PrintWriter out = response.getWriter()) {
            try (Connection con = ds.getConnection()) {
                NaviPalkki.luoNaviPalkki(request, response, "Rekisteröityminen");
                tulostaLomake(out);
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

            tunnus = request.getParameter("tunnus"); //Hakee lomakkeelta tunnuksen
            salasana = request.getParameter("salasana"); // Hakee lomakkeelta salasanan
            String onnistuiko = "Tunnus varattu!"; // Vaihtuu mikäli rekisteröinti onnistuu.

            if (luoKäyttäjä(con, tunnus, salasana)) {
                onnistuiko = "Rekisteröityminen onnistui!";
            }
            NaviPalkki.luoNaviPalkki(request, response, "Rekisteröityminen");

            // Tulostetaan miten kävi
            tulostaOnnistuiko(out,onnistuiko);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ÄLÄ ANNA TYHJIÄ ARVOJA!!, tässä niitä ei ainakaan vielä tarkisteta
    private boolean luoKäyttäjä(Connection con, String tunnus, String salasana){
        //Tsekataan onko tunnus varattu
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
            // Luodaan uusi jollei tunnusta löydy.
            if (onkoVarattu == 0){
                    String lisäys = "INSERT INTO henkilo (kayttajanimi, salasana, rooli) values (?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(lisäys);
                    ps.setString(1, tunnus);
                    ps.setString(2, salasana);
                    ps.setString(3, "2"); //lisätään rooliksi 'rekisteroitynyt'
                    ps.executeUpdate();
                    return true;
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } return false;
    }

    private void tulostaOnnistuiko(PrintWriter out, String onnistuiko){
            out.println("<form method='post' style='width: 400px; position: relative;" +
                    "top: 70px; left: 8%;'>" +
                    "<fieldset>");
            out.println("<legend>Rekisteröityminen</legend>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td colspan='3'><h3>" + onnistuiko + "</h3></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td colspan='3'>siirry</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td><a href='index.jsp'>kotisivulle</a>, </td>");
            out.println("<td><a href='/KeskustelujaViestitServlet'>keskusteluihin</a></td>");
            out.println("<td> tai <a href='/Profiili'>profiiliin</a></td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</fieldset></form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
    }

    private void tulostaLomake(PrintWriter out){
        // Tulostaa hienon rekisteröitymislomakkeen
        out.println("<form method='post' style='width: 400px; position: relative;" +
                "top: 70px; left: 8%;'>" +
                "<fieldset>");
        out.println("<legend>Rekisteröityminen</legend>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<td style='width: 120px'><label for='tunnus'>Käyttäjänimi</legend></td>");
        out.println("<td><input type='text' maxlength=16  name='tunnus' focus required pattern='.*\\S+.*' title='Ei whitespace-merkkejä.'></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td style='width: 120px'><label for='salasana'>Salasana</legend></td>");
        out.println("<td><input type='password' maxlength=16 name='salasana' required pattern='.*\\S+.*' title='Ei whitespace-merkkejä.'></td>");
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

    }
}
