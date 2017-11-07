package Foorumi;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet(name="Profiilisivu", urlPatterns = {"Profiilisivu"})
public class Profiilisivu {
    @Resource(name = "jdbc/Foorumi")
    DataSource ds;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        doGet(req, res);
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }
}
