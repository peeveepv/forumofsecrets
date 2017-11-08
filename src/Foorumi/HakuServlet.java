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

@WebServlet(name = "Hakukone", urlPatterns = {"/hakukone"})
public class HakuServlet extends HttpServlet {

    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String haettava = (String) request.getParameter("haettava");


        StringBuilder palauta = new StringBuilder();

        try {

            String sql1 = "select keskusteluid, nimi from keskustelu where nimi like ?";
            PreparedStatement kyselyLause1 = con.prepareStatement(sql1);
            kyselyLause1.setString(1, haettava);
            ResultSet kyselynTulos1 = kyselyLause1.executeQuery();

//            String sql2 = "select kuvaus from keskustelu";
//
//            String sql3 = "select kirjoittaja from viesti";
//            String sql4 = "select otsikko from viesti";
//            String sql5 = "select viesti from viesti";
//            String sql6 = "select vastaus from viesti";

            while (kyselynTulos1.next()) {
                String testi = kyselynTulos1.getString("nimi");
                int testi2 = kyselynTulos1.getInt("keskusteluid");

                if (haettava.contains(testi)) {
                    palauta.append("<p>");
// Tee palautus URL --> joka hakee keskustelu/keskusteluID
                    palauta.append("<a href='LOREM_IPSUS' target=_blank>");
                    palauta.append(testi);
                    palauta.append("</a>");
                    palauta.append("</p>");

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Haun tulos</title>");
            out.println("</head>");
            out.println("<body>");
            out.println(palauta.toString());
            out.println("</body>");
            out.println("</html>");
        }
    }




    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Foorumin hakusivu</title>");
            out.println("</head>");
            out.println("<body>");

            // Koska Form-tagista puuttuu "action"-määre, niin toiminto kutsuu tätä samaa servlettiä,
            // eli itseänsä, täältä doGet:istä --> doPost:iin
            out.println("<form method='post'>");

            out.println("Anna yksi hakusana ");
            out.println("<input type='text' name='haettava'>");
            out.println("<input type='submit' value='Lähetä'>");
            out.println("</form>");

            out.println("</body>");
            out.println("</html>");
        }
    }
}



