package Foorumi;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "KeskustelujaViestitServlet", urlPatterns = {"/KeskustelujaViestitServlet"})
public class KeskustelujaViestitServlet extends HttpServlet {

    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
              haeKeskustelut(request, response);
    }

    protected void haeKeskustelut(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("text/html");
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
            for (KeskusteluOlio olio: lista) {
               // out.print("<p>"<a href="NaytaKeskustelu.java+olio.getNimi() + ": " + olio.getKuvaus()+ "</p>");
            }
        } catch (IOException e) {
            out.print(e.getMessage());
    }


    }
}
