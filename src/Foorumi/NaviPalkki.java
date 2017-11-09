package Foorumi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class NaviPalkki {

    // tulostaa kutsuvalle Servletille html-koodin, joka sisältää
        // response.setContentType("text/html");
        // <head> -osion tyylittelyineen taustaa varten
        // navipalkin tulostettavan sivun vasempaan reunaan
        // avaavat <html>, <body> sekä <div> -tagit
    //HUOM!! Metodin lisäksi tulee luoda 1 </div> tägi ennen kutsuvan Servletin sivun sulkemista!

    public static void luoNaviPalkki(HttpServletRequest request, HttpServletResponse response, String title) throws IOException {

        PrintWriter out = response.getWriter();

        // contentType
        response.setContentType("text/html");

        // haetaan mahdollinen sessio käyttäjän nimen/nimimerkin raportointia varten navipalkissa
        HttpSession session = request.getSession(false);

        out.println("<html>");
        out.println("<head>");

        // tulostetaan otsikoksi metodille annettu sivun title
        out.println("<title>"+title+"</title>");

        // varmistaa, että navipalkin vasemmalle puolelle ei jää 3px marginia (oletus)
        out.println("<style>" +
                "body {margin-left: 0px;}");

        // tumma "secret" background oletuksena ja form background; käyttöön muille kuin määritellyille sivuille (titleille)
        if ("Viestit".equals(title) || "Keskustelut".equals(title) || "Haun tulos".equals(title)){
        } else {
            out.println(
                    "form {background-color: white;} " +
                    "body {background: url('img/candle_secret.jpg');" +
                            "background-position: center;" +
                            "background-repeat: no-repeat;" +
                            "background-size: contain;" +
                            "background-color: #333;}"
            );
        }

        // navipalkki ja content-div positio + tyylittely
        out.println(
                    "td {word-break: break-all; }" +
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

        // navipalkin tulostaminen
        out.println(
                "<nav> " +
                        "<span></span>" +
                        "<span style='font-size: 120%'><a href='index.jsp'><strong>Forum of Secrets</strong></a></span>" +
                        "<span></span>" +
                        "<a href='/KeskustelujaViestitServlet'>Keskustelut</a>" +
                        "<span></span>"
        );

        // jos ei sessiota, tulostetaan kirjautumisen ja rekisteröitymisen linkit
        if (session == null
                || session.getAttribute("kayttajanimi") == null
                || "anonymous".equals(session.getAttribute("kayttajanimi"))) {

            out.println("<a href='/Login'>Kirjautuminen</a>");
            out.println("<a href='/Kayttaja'>Rekisteröityminen</a>");

        // jos sessio, tulostetaan nimimerkki/nimi käyttäjältä sekä linkit profiilisivustolle ja uloskirjautumiseen
        } else {

            out.println("<span style='font-size: 80%'><i>Tällä hetkellä kirjautuneena:</i>");

            if (session.getAttribute("nimimerkki") == null) {
                out.println(session.getAttribute("kayttajanimi"));
            } else {
                out.println(session.getAttribute("nimimerkki"));
            }

            out.println("</span>");

            out.println("<a href='/Profiili'>Profiili</a>");
            out.println("<a href='/Logout'>Uloskirjautuminen</a>");

        }

        // hakukone-linkki navipalkkiin, navin sulkeminen ja content-divin avaaminen
        out.println(
                "<span></span>" +
                        "<a href='/Hakukone'>Etsi viestejä</a>" +
                        "<span></span>" +
                        "</nav>" +
                        "" +
                        "<div id='content'>"
        );

    }
}
