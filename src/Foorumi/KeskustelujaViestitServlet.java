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

//Tämä tulostaa keskustelualustan pohjan
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Keskustelut</title>");
        out.println(
                "<style> td {word-break: break-all; } " +
                        "#content {position: relative; left: 260px; width: 80%;} " +
                        "#content {position: relative; left: 260px; width: 80%;} " +
                        "nav {position: fixed; top: 0; width: 240px; height: 100%; font-family: Georgia; " +
                        "background-color: #333; float: left; clear: left; display: inline; } " +
                        "nav a, nav span {display: block; padding: 14px 16px; color: antiquewhite; text-shadow: none; " +
                        "text-decoration: none;} .active {background-color: dimgrey;} " +
                        "nav a:active, nav a:visited {color: antiquewhite; text-shadow: none;} " +
                        "nav a:hover {background-color: #111;} " +
                        "</style>"
        );
        out.println("</head>");

        out.println("<body>");
        out.println(
                "<nav> " +
                        "<span></span>" +
                        "<span style='font-size: 120%'><a href='index.jsp'><strong>Forum of Secrets</strong></a></span>" +
                        "<span></span>" +
                        "<a href='/KeskustelujaViestitServlet'>Keskustelut</a>" +
                        "<span></span>"
        );

        if (session == null
                || session.getAttribute("kayttajanimi") == null
                || "anonymous".equals(session.getAttribute("kayttajanimi"))) {

            out.println("<a href='/Login'>Kirjautuminen</a>");
            out.println("<a href='/Kayttaja'>Rekisteröityminen</a>");

        } else {

            out.println("<span><i>Tällä hetkellä kirjautuneena:</i><span>");
            out.println("<span>" + session.getAttribute("kayttajanimi") + "</span>");
            out.println("<span></span>");

            out.println("<a href='/Profiili'>Profiili</a>");
            out.println("<a href='/Logout'>Uloskirjautuminen</a>");

        }

        out.println(
                "<span></span>" +
                        "<a href='/Hakukone'>Etsi viestejä</a>" +
                        "<span></span>" +
                        "</nav>" +
                        "" +
                        "<div id='content'>"
        );

        haeKeskustelut(request, response);
            out.println("<form method='post' >");
            out.println("<input type=text name=nimi value=nimi> </br>");
            out.println("<input type=text name=kuvaus value=kuvaus> </br>");
            out.println("<input type=submit  value='Aloita uusi keskustelu'>");
            out.println("</form>");

            out.println("</div>");
    }


    protected void haeKeskustelut(HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = null;
        Connection con = null;
        try {
           con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<KeskusteluOlio> lista = Keskustelu.haeKaikkiKeskustelut(con);

        try {
            out = response.getWriter();

            out.print("<h2>Keskustelut</h2>");
            out.print("<ul>");

            for (KeskusteluOlio olio: lista) {
                out.print("<li> <a href=NaytaKeskustelu?KeskusteluId="+olio.getKeskusteluId()+ ">"
                        +olio.getNimi() +" </a><br> Keskustelualueen kuvaus: "  + olio.getKuvaus()+ "</li>");
            }
            out.print("</ul>");
        } catch (IOException e) {
            out.print(e.getMessage());
    }
    }
}
