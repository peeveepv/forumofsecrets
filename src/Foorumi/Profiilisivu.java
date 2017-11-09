package Foorumi;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
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

        String apuNick = req.getParameter("nimimerkki");
        String apuKuvaus = req.getParameter("kuvaus");
        String sql = "UPDATE henkilo SET nimimerkki =?, kuvaus=? WHERE kayttajanimi=?;";
        try {
            Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, apuNick);
            ps.setString(2,apuKuvaus);
            ps.setString(3,kayttajanimi);
            ps.executeUpdate();
            HttpSession istunto = req.getSession(false);
            istunto.setAttribute("nimimerkki", apuNick);
            istunto.setAttribute("kuvaus", apuKuvaus);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        doGet(req,res);
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession istunto = req.getSession(false);
        String istuntoId;

        if (istunto.getAttribute("hloid") == null){
            RequestDispatcher rd = req.getRequestDispatcher(
                    "/Login");
            rd.forward(req, res);
        }

        String paramId = req.getParameter("hloid");
        istuntoId = istunto.getAttribute("hloid").toString();

        if (req.getParameter("hloid") == null){
            paramId = istuntoId;
        }


        try (Connection con = ds.getConnection()){

            String haeTiedot = "SELECT * from henkilo where hloid=?";
            PreparedStatement ps = con.prepareStatement(haeTiedot);
            ps.setString(1,paramId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                kayttajanimi = rs.getString("kayttajanimi");
                nimimerkki = rs.getString("nimimerkki");
                kuvaus = rs.getString("kuvaus");
            }
            NaviPalkki.luoNaviPalkki(req,res,"Profiilisivu");

            try (PrintWriter out = res.getWriter()) {
                if (istuntoId.equals(paramId)) {
                    out.println("<form method='post' style='width: 400px; position: relative; top: 70px; left: 8%;'><fieldset>");
                    out.println("<legend>Profiilin tiedot</legend>");
                    out.println("<table>" +
                            "<tr><td style='width: 120px'>" +
                            "<label for='kayttajanimi'>Käyttäjänimi: </label></td><td>" + kayttajanimi + "</td></tr>" +
                            "<tr><td style='width: 120px'>" +
                            "<label for='nimimerkki'>Nimimerkki: </label></td>"+
                            "<td><input type='text' name='nimimerkki' focus value='" + nimimerkki + "'></td></tr>"+
                            "<tr><td style='width: 120px'>" +
                            "<label for='kuvaus'>Kuvaus: </label></td>"+
                            "<td><input type='text' name='kuvaus' value='" + kuvaus + "'></td></tr>" +
                            "<tr><td><input type='submit' value='Päivitä'></td></tr></table></fieldset></form>"+
                            "</div>" +
                            "</body>" +
                            "</html>");
                } else {
                    out.println(
                            "<form style='width: 400px; position: relative; top: 70px; left: 8%;'>" +
                                    "<fieldset><legend> Profiilin tiedot </legend><table>" +
                                    "<tr><td style='width: 120px'>" +
                                    "<label for='kayttajanimi'>Käyttäjänimi: </label></td><td>" + kayttajanimi + "</td></tr>" +
                                    "<tr><td style='width: 120px'>" +
                                    "<label for='nimimerkki'>Nimimerkki: </label></td><td>" + nimimerkki + "</td></tr>" +
                                    "<tr><td style='width: 120px'>" +
                                    "<label for='kuvaus'>Kuvaus: </label></td><td>" + kuvaus + "</td></tr></table>" +
                            "</fieldset></div>" +
                            "</body>" +
                            "</html>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

