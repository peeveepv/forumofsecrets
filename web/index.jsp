<%-- Created by we the people --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>

    <title></title>

    <style>

        #content {
            position: relative;
            left: 260px;
            width: 80%;
        }

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

    <nav>

        <span></span>
        <span style="font-size: 120%"><a href="index.jsp"><strong>Forum of Secrets</strong></a></span>
        <span></span>

        <a href="index.jsp">Pääsivu</a>
        <span></span>

        <a href="/KeskustelujaViestitServlet">Keskustelujen lista</a>
        <span></span>

        <%
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
        %>

        <span></span>

        <a href="/Hakukone">Etsi viestejä</a>
        <span></span>

    </nav>

    <div id="content">

        <h1>Index sisältöä...</h1>

        <p><a href="KeskustelujaViestitServlet">Keskusteluihin</a></p>

        <p>Testaa yhteyttä, häytä kaikki viestit: <a href="NaytaKeskustelu">Yhteys-servlet</a></p>

        <h3>Linkit:</h3>
        <ul>
            <li><a href="http://localhost:8080/hakukone">Hakukone</a></li>
        </ul>

    </div>

</div>
</body>
</html>