package Foorumi;

import javax.annotation.Resource;
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
import java.util.List;

@WebServlet(name = "KeskustelujaViestitServlet", urlPatterns = {"/KeskustelujaViestitServlet"})
public class KeskustelujaViestitServlet extends HttpServlet {

    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    @Resource(name = "jdbc/FoorumiDELETE")
    DataSource dsAdmin;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setContentType("text/html");

        PrintWriter out = null;
        Connection con = null;
        String sql = "INSERT INTO keskustelu (nimi, kuvaus) VALUES (?, ?)";
        String keskustelunNimi = request.getParameter("nimi");
        String keskustelukuvaus = request.getParameter("kuvaus");
        boolean moi = false;
        try {
            con = ds.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1,keskustelunNimi);
            stmt.setString(2,keskustelukuvaus);
            stmt.executeUpdate();
            moi = true;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        if(moi)
        doGet(request, response);
        else out.println("<h2>update ei toiminut</h2>");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        HttpSession session = request.getSession(false);
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        NaviPalkki.luoNaviPalkki(request, response, "Keskustelut");
        haeKeskustelut(request, response);
            out.println("<form method='post' >");
            out.println("<input type=text name=nimi value=nimi> </br>");
            out.println("<input type=text name=kuvaus value=kuvaus> </br>");
            out.println("<input type=submit  value='Aloita uusi keskustelu'>");
            out.println("</form>");

            out.println("</div>"); //Tämä sulkee haeNaviPalkkimetodin
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

            out.print("<h2>Keskustelut</h2>");
            out.print("<table>");
            out.print("<tr><td>Keskustelu</td><td>Keskustelualueen kuvaus: </td></tr>");

            for (KeskusteluOlio olio: lista) {
                out.print("<tr><td> <a href=NaytaKeskustelu?KeskusteluId="+olio.getKeskusteluId()+ ">"
                        +olio.getNimi() +"</a> </td><td>"  + olio.getKuvaus()+ "</td>");
                if("admin".equals((String)session.getAttribute("rooli"))){
                    out.println("<td><form method=post><input type=submit value=poista>" +
                            "<input type=hidden name=poista value= poista></form></td>");
                }
                out.print("</tr>");
            }
            out.print("</ul>");
        } catch (IOException e) {
            out.print(e.getMessage());
        }
    }

}
