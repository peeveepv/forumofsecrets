package Foorumi;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "KeskustelujaViestitServlet", urlPatterns = {"/KeskustelujaViestitServlet"})
public class KeskustelujaViestitServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
              haeKeskustelut(request, response);
    }

    protected void haeKeskustelut(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("text/html");
        PrintWriter out = null;
        List<KeskusteluOlio> lista = Keskustelu.haeKeskustelut();
        try {
            out = response.getWriter();
            for (KeskusteluOlio olio: lista) {
                out.print("<p>"+olio.getNimi() + ": " + olio.getKuvaus()+ "</p>");
            }
        } catch (IOException e) {
            out.print(e.getMessage());
    }


    }
}
