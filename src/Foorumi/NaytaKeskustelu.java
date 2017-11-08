package Foorumi;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "NaytaKeskustelu", urlPatterns = {"/NaytaKeskustelu"})
public class NaytaKeskustelu extends HttpServlet {

    // import javax.annotation.Resource;
    // import javax.sql.DataSource;
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = null;
        Connection con = null;
        String sql = "INSERT INTO viesti (otsikko, viesti, kirjoittaja ,keskusteluid) VALUES (?, ?, ?, ?)";
        String keskustelunNimi = req.getParameter("otsikko");
        String keskustelukuvaus = req.getParameter("viesti");
        int kirjoittaja = Integer.parseInt(req.getParameter("kirjoittaja"));
        int keskusteluid = Integer.parseInt(req.getParameter("keskusteluid"));

        boolean moi = false;
        try {
            con = ds.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1,keskustelunNimi);
            stmt.setString(2,keskustelukuvaus);
            stmt.setInt(3,kirjoittaja);
            stmt.setInt(4,keskusteluid);
            stmt.executeUpdate();
            moi = true;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        //Jos update onnistuu, niin palataan takaisin doGet-metodiin, siellä näkyy lisätty viesti
        if(moi)
            doGet(req, res);
        else
            out.println("<h2>update ei toiminut</h2>");
    }



    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        ResultSet rsviestit = null;
        ResultSet rskeskustelu = null;
        ResultSet rshenkilot = null;

        String keskustelunimi = "", keskustelukuvaus = "";

        Map<Integer, String> kirjoittajat = new HashMap<>();

        try (PrintWriter out = res.getWriter()) {
            try (Connection con = ds.getConnection()) {

                int keskusteluid;

                if (req.getParameter("KeskusteluId") != null) {
                    keskusteluid = Integer.parseInt(req.getParameter("KeskusteluId"));
                } else {
                    keskusteluid = 1;
                }

                String sql = "SELECT * FROM viesti WHERE keskusteluid = " + keskusteluid + " ORDER BY kirjoitettu ASC;";
                PreparedStatement ps = con.prepareStatement(sql);

                rsviestit = ps.executeQuery();

                sql = "SELECT * FROM keskustelu WHERE keskusteluid = " + keskusteluid + ";";

                ps = con.prepareStatement(sql);

                rskeskustelu = ps.executeQuery();

                while (rskeskustelu.next()) {
                    keskustelunimi = rskeskustelu.getString("nimi");
                    keskustelukuvaus = rskeskustelu.getString("kuvaus");
                }

                sql = "SELECT * FROM henkilo;";

                ps = con.prepareStatement(sql);

                rshenkilot = ps.executeQuery();
                //Tämä laittaa kirjoittajat hashmappiin henkiloid:n mukaan
                while (rshenkilot.next()) {
                    kirjoittajat.put(
                            rshenkilot.getInt("hloid"),
                            rshenkilot.getString("nimimerkki")
                    );
                }

                NaviPalkki.luoNaviPalkki(req, res, "Viestit");

                out.println("<h1>Keskustelu:<br> " + keskustelunimi + "</h1>");
                out.println("<h3>Kuvaus: <i>" + keskustelukuvaus + "</i></h3>");

                out.println("<table style='border: 1px solid black'>");

                //Tämä tulostaa viestit tulosjoukosta rsviestit
                while (rsviestit.next()) {
                    out.println("<tr>");
                    out.println("<td style='width: 200px'>"+ kirjoittajat.get(rsviestit.getInt("kirjoittaja")) +
                            "</td><td style='rowspan: 2'; >"+ rsviestit.getString("viesti") + "</td>");
                    out.println("</tr>");
                    out.println("<tr>");
                    out.println("<td>"+ rsviestit.getString("otsikko") + "</td><td></td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                out.println("<br>");
                out.println("<hr>");
                out.println("<br>");
                int kirjoittajaID = 2;
                if(session.getAttribute("hloid")!= null)
                kirjoittajaID = (Integer)(session.getAttribute("hloid"));
                //String kirjoittajaI = (String) session.getAttribute("hloid");
                //out.print(kirjoittajaID);
                ///out.print(kirjoittajaI);
                //Tässä on lomake uuden viestin luomiseen
                out.println("<form method='post' id=1>");
                out.println("<input type=submit  value='Lisää uusi viesti'> <p></p>");
                out.println("<input type=text name='otsikko' value='otsikko'><br>");
                out.println("<input type=hidden name='kirjoittaja' value=" +kirjoittajaID + ">");
                out.println("<input type=hidden name='keskusteluid' value=" + keskusteluid + ">");
                out.println("</form>");
                out.println("<textarea form=1 name='viesti' value='viesti' row=5 column=10></textarea>");

                out.println("<p>Back to the <a href='index.jsp'>index</a></p>");

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

            } catch (
                    SQLException e)
            {
                out.println(e.getMessage());
            }
        }
    }
}