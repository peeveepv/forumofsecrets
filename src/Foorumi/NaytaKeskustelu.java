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
import java.util.*;

@WebServlet(name = "NaytaKeskustelu", urlPatterns = {"/NaytaKeskustelu"})
public class NaytaKeskustelu extends HttpServlet {

    // import javax.annotation.Resource;
    // import javax.sql.DataSource;
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;
    @Resource(name = "jdbc/FoorumiDELETE")
    DataSource dsAdmin;
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = null;
        Connection con = null;
        Enumeration nimet = req.getParameterNames();
        List<String> lista = new ArrayList<>();
        while(nimet.hasMoreElements()){
            lista.add((String)nimet.nextElement());
        }

        boolean moi = false;

        if(lista.contains("vastaus")){
            String sql = "INSERT INTO viesti (otsikko, viesti, kirjoittaja ,keskusteluid, vastaus) VALUES (vastaus ,?, ?, ?, ?)";

            String viesti = req.getParameter("vastaus");
            int kirjoittaja = Integer.parseInt(req.getParameter("kirjoittaja"));
            int keskusteluid = Integer.parseInt(req.getParameter("keskusteluid"));
            int vastaus = Integer.parseInt(req.getParameter("viestiID"));
            try {
                con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, viesti);
                stmt.setInt(2, kirjoittaja);
                stmt.setInt(3, keskusteluid);
                stmt.setInt(4, vastaus);
                stmt.executeUpdate();
                moi = true;
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }else if(lista.contains("poista")){
            String sql = "DELETE from viesti where id = ?";
            int id = Integer.parseInt(req.getParameter("poista")); //Palauttaa poistettavan viestin ID:n

            try {
                con = dsAdmin.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                moi = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            String sql = "INSERT INTO viesti (otsikko, viesti, kirjoittaja ,keskusteluid) VALUES (?, ?, ?, ?)";
            String keskustelunNimi = req.getParameter("otsikko");
            String keskustelukuvaus = req.getParameter("viesti");
            int kirjoittaja = Integer.parseInt(req.getParameter("kirjoittaja"));
            int keskusteluid = Integer.parseInt(req.getParameter("keskusteluid"));

            try {
                con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, keskustelunNimi);
                stmt.setString(2, keskustelukuvaus);
                stmt.setInt(3, kirjoittaja);
                stmt.setInt(4, keskusteluid);
                stmt.executeUpdate();
                moi = true;
            } catch (SQLException e) {
                e.printStackTrace();

            }
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
        ResultSet rsvastaukset = null;
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

                //haetaan kaksi identtistä
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
                int kirjoittajaID = 2;
                if(session.getAttribute("hloid")!= null)
                    kirjoittajaID = (Integer)(session.getAttribute("hloid"));


                NaviPalkki.luoNaviPalkki(req, res, "Viestit");


                out.println("<h1>Keskustelu:<br> " + keskustelunimi + "</h1>");
                out.println("<h3>Kuvaus: <i>" + keskustelukuvaus + "</i></h3>");
                //Tämä tulostaa viestit tulosjoukosta rsviestit

                List<Viestit> lista = new ArrayList<>();
                while(rsviestit.next()) {
                    Viestit olio = new Viestit(rsviestit.getInt("id"),
                            rsviestit.getInt("kirjoittaja"),
                            rsviestit.getInt("keskusteluid"),
                            rsviestit.getString("otsikko"),
                            rsviestit.getString("viesti"),
                            rsviestit.getInt("vastaus"),
                            rsviestit.getDate("kirjoitettu"));
                    lista.add(olio);
                }
                out.println("<table border: 1px solid black>");


                for (int i = 0; i <lista.size() ; i++) {
                    int vastausid = lista.get(i).getVastaus();
                    if (vastausid == 0) {
                        out.println("<tr>");
                        out.println("<td style='width: 200px'> Kirjoittaja: <br>" + kirjoittajat.get(lista.get(i).getKirjoittaja()) +
                                "</td><td style='width: 200px'> Otsikko: <br>" + lista.get(i).getOtsikko() +
                                "</td><td style='width: 400px';>" + lista.get(i).getViesti() + "</td><td>");

                        out.println("<form method='post' id=2>");
                        out.println("<input type=submit  value='Vastaa'>");
                        out.println("<input type=text name='vastaus' value='vastaus'><br>");
                        out.println("<input type=hidden name='kirjoittaja' value=" + kirjoittajaID + ">");
                        out.println("<input type=hidden name='keskusteluid' value=" + keskusteluid + ">");
                        out.println("<input type=hidden name='viestiID' value=" + lista.get(i).getId() + ">");
                        out.println("</form>");
                        if("admin".equals((String)session.getAttribute("rooli"))){
                            out.println("<br><form method=post><input type=submit value=poista>" +
                                    "<input type=hidden name=poista value= "+lista.get(i).getId() +"></form>");
                        }
                        out.println("</td></tr>");
                        for (int j = 0; j < lista.size(); j++) {
                            if (lista.get(j).getVastaus() == lista.get(i).getId()) {
                                out.println("<tr><td></td><td>Vastauksia</td><td><i style='color:grey'>" + lista.get(i).getViesti() + "</i><br>" + lista.get(j).getViesti() + "</td><td></td></tr>");
                            }
                        }
                    }
                }


                out.println("</table>");

                out.println("<br>");
                out.println("<hr>");
                out.println("<br>");
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