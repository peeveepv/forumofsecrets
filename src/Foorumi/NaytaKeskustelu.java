package Foorumi;

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

        doGet(req, res);

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ResultSet rsviestit = null;
        ResultSet rskeskustelu = null;
        ResultSet rshenkilot = null;

        String keskustelunimi = "", keskustelukuvaus = "";

        Map<Integer, String> kirjoittajat = new HashMap<>();

        try (PrintWriter out = res.getWriter()) {

            try (Connection con = ds.getConnection()) {

                haeTiedot(con, rskeskustelu, rsviestit, rshenkilot, kirjoittajat, keskustelunimi, keskustelukuvaus);


                res.setContentType("text/html");

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Kaikki viestit</title>");
                out.println("</head>");

                out.println("<body>");

                tulostaTiedot(out, rskeskustelu, rsviestit, rshenkilot, kirjoittajat, keskustelunimi, keskustelukuvaus);

                out.println("</body>");
                out.println("</html>");

// ...


            } catch (
                    SQLException e)

            {
                out.println(e.getMessage());
            }
        }
    }

    private void haeTiedot(Connection con, ResultSet rskeskustelu, ResultSet rsviestit, ResultSet rshenkilot, Map<Integer, String> kirjoittajat, String keskustelunimi, String keskustelukuvaus) throws SQLException {

//                int keskusteluid = Integer.parseInt(req.getParameter("keskusteluid"));
        int keskusteluid = 1;

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

        while (rshenkilot.next()) {
            kirjoittajat.put(
                    rshenkilot.getInt("hloid"),
                    rshenkilot.getString("nimimerkki")
            );
        }

    }

    private void tulostaTiedot(ResultSet rskeskustelu, ResultSet rsviestit, ResultSet rshenkilot, Map<Integer, String> kirjoittajat, String keskustelunimi, String keskustelukuvaus) {

        out.println("<h1>Keskustelu: " + keskustelunimi + "</h1>");
        out.println("<h3><i>" + keskustelukuvaus + "</i></h3>");

        while (rsviestit.next()) {
            out.println("<p>"
                    + rsviestit.getString("otsikko")
                    + " \n"
                    + "Kirjoittaja: " + kirjoittajat.get(rsviestit.getInt("kirjoittaja"))
                    + "\n"
                    + rsviestit.getString("viesti")
                    + "</p>");
        }


        out.println("<p>Back to the <a href='index.jsp'>index</a></p>");

    }
}

            }

