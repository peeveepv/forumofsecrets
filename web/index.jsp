<%-- Created by we the people --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>

    <title>Forum of Secrets - Home</title>

    <style>

        /* taustan ja content-divin tyylittelyt */

        body {
            margin-left: 0px;
            background: url("img/candle_secret.jpg");
            background-position: center;
            background-repeat: no-repeat;
            background-size: contain;
            background-color: #333;
        }

        #content {
            position: relative;
            left: 260px;
            width: 80%;
        }

        /* navipalkin tyylittelyt */

        nav {
            position: fixed;
            top: 0;
            width: 240px;
            height: 100%;
            font-family: Georgia;
            background-color: #333;
            float: left;
            clear: left;
            display: inline;
        }

        nav a, nav span {
            display: block;
            padding: 14px 16px;
            color: antiquewhite;
            text-shadow: none;
            text-decoration: none;
        }

        .active {
            background-color: dimgrey;
        }

        nav a:active, nav a:visited {
            color: antiquewhite;
            text-shadow: none;
        }

        nav a:hover {
            background-color: #111;
        }

    </style>

</head>

<body>
<div>

    <!-- navipalkki -->

    <nav>

        <span></span>
        <span style="font-size: 120%"><a href="index.jsp"><strong>Forum of Secrets</strong></a></span>
        <span></span>

        <a href="/KeskustelujaViestitServlet">Keskustelut</a>
        <span></span>

        <!-- jos ei ole sessiota, tulostetaan anonyymi käyttäjänimi sekä linkit kirjautumiseen ja rekisteröitymiseen -->
        <!-- jos on sessio, tulostetaan nimimerkki/nimi sekä linkit profiilin tarkasteluun ja uloskirjautumiseen -->

        <%
            if (session == null
                    || session.getAttribute("kayttajanimi") == null
                    || "anonymous".equals(session.getAttribute("kayttajanimi"))) {

                out.println("<a href='/Login'>Kirjautuminen</a>");
                out.println("<a href='/Rekisterointi'>Rekisteröityminen</a>");

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
        %>

        <span></span>

        <a href="/Hakukone">Etsi viestejä</a>
        <span></span>

    </nav>

    <div id="content">

        <!-- mahdollista index-sisältöä -->

    </div>

</div>
</body>
</html>