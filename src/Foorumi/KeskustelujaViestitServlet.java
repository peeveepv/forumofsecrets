package Foorumi;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@WebServlet(name = "KeskustelujaViestitServlet", urlPatterns = {"/KeskustelujaViestitServlet"})
public class KeskustelujaViestitServlet extends HttpServlet {

    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    @Resource(name = "jdbc/FoorumiDELETE")
    DataSource dsAdmin;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("text/html");
        boolean toimiiko = false;
        PrintWriter out = null;

        //ottaa formin lähettämäät nimet talteen listaan
        Enumeration nimet = request.getParameterNames();
        List<String> lista = new ArrayList<>();
        while(nimet.hasMoreElements()){
            lista.add((String)nimet.nextElement());
        }


        //Poistaa keskustelun
        if(lista.contains("poista")){
            String sql = "DELETE from viesti where keskusteluid = ?";
            int id = Integer.parseInt(request.getParameter("poista")); //Palauttaa poistettavan viestin ID:n
            try {
                Connection con = dsAdmin.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                String sql1 = "DELETE from keskustelu where keskusteluid = ?";
                PreparedStatement stmt2 = con.prepareStatement(sql1);
                stmt2.setInt(1, id);
                stmt2.executeUpdate();
                toimiiko = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //Jos nimi on tyhjä "", ohjaa takaisin alkuun eikä luo uutta
        if("".equals(request.getParameter("nimi"))){
            toimiiko = true;
        }
        //Lisää keskustelun

        else {
            Connection con = null;
            String sql = "INSERT INTO keskustelu (nimi, kuvaus) VALUES (?, ?)";
            String keskustelunNimi = request.getParameter("nimi");
            String keskustelukuvaus = request.getParameter("kuvaus");
            if("".equals(keskustelunNimi))doGet(request, response);
            try {
                con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, keskustelunNimi);
                stmt.setString(2, keskustelukuvaus);
                stmt.executeUpdate();
                toimiiko = true;
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        if(toimiiko)
        doGet(request, response);
        else out.println("<h2>update ei toiminut</h2>");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        HttpSession session = request.getSession(false);
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        NaviPalkki.luoNaviPalkki(request, response, "Keskustelut");

        out.print("<h2><br>Keskustelut</h2>");

        out.println("<form method='post' style='width: 400px;" +
                "top: 70px; left: 8%;'>" +
                "<fieldset>");

        out.println("<legend>Luo uusi keskustelu</legend>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<td style='width: 120px'><label for='nimi'>Aihe</legend></td>");
        out.println("<td><input type=text name=nimi placeholder=nimi focus></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td style='width: 120px'><label for='kuvaus'>Aiheen kuvaus</legend></td>");
        out.println("<td><input type=text name=kuvaus placeholder=kuvaus></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><input type='submit' value='Uusi keskustelu'></td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</fieldset></form>");
//
//        out.println("<form method='post' >");
//        out.println("Aihe<input type=text name=nimi placeholder=nimi> </br>");
//        out.println("Aiheen kuvaus <input type=text name=kuvaus placeholder=kuvaus> </br>");
//        out.println("<input type=submit  value='Aloita uusi keskustelu'>");
//        out.println("</form>");


        //Välituunausta
        out.println("<br>");
        out.println("<hr>");
        out.println("<br>");

        haeKeskustelut(request, response);

        out.println("</div>"); //Tämä sulkee haeNaviPalkkimetodin
        out.println("</body>");
        out.println("</html>");


    }


    protected void haeKeskustelut(HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = null;
        Connection con = null;
        try {
           con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        HttpSession session = request.getSession(false);
        List<KeskusteluOlio> lista = Keskustelu.haeKaikkiKeskustelut(con);

        try {
            out = response.getWriter();

            out.print("<table>");
            out.print("<tr><td>Keskustelu</td><td>Keskustelualueen kuvaus: </td></tr>");
            out.print("<tr></tr>");

            for (KeskusteluOlio olio: lista) {
                out.print("<tr><td> <a href=NaytaKeskustelu?KeskusteluId="+olio.getKeskusteluId()+ ">"
                        +olio.getNimi() +"</a> </td><td>"  + olio.getKuvaus()+ "</td>");
                if("admin".equals((String)session.getAttribute("rooli"))){
                    out.println("<td><form method=post><input type=submit value=poista>" +
                            "<input type=hidden name=poista value="+olio.getKeskusteluId()+"></form></td>");
                }
                out.print("</tr>");
            }
            out.print("</table>");
        } catch (IOException e) {
            out.print(e.getMessage());
        }
    }

}
