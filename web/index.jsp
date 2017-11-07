<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>

    <title></title>

    <style>

        #content {
            display: inline;
            width: 80%;
        }

        nav {
            position: fixed;
            top: 0;
            width: 15%;
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

        <span style="font-size: 120%"><strong>KISSAFOORUMI</strong></span>
        <span></span>

        <a href="/KeskustelujaViestitServlet">Keskustelujen lista</a>
        <a href="/NaytaKeskustelu">Yksittäisen keskustelun sivu</a>
        <span></span>

        <a href="/Login">Kirjautuminen</a>
        <a href="Kayttaja.jsp">Rekisteröityminen</a>
        <a href="/Profiili">Profiili</a>
        <a href="/Logout">Uloskirjautuminen</a>
        <span></span>

        <a href="/Etsi">Etsi viestejä</a>
        <span></span>

    </nav>

    <div id="content">

        <p><a href="KeskustelujaViestitServlet">Keskusteluihin</a></p>

        <p>Testaa yhteyttä, häytä kaikki viestit: <a href="/NaytaKeskustelu">Yhteys-servlet</a></p>

        <h3>Linkit:</h3>
        <ul>
            <li><a href="/KeskustelujaViestitServlet">Keskustelujen lista</a></li>
            <li><a href="/NaytaKeskustelu">Yksittäisen keskustelun sivu</a></li>
            <li></li>
            <li><a href="/Login">Kirjautuminen</a></li>
            <li><a href="Kayttaja.jsp">Rekisteröityminen</a></li>
            <li><a href="/Profiili">Profiili</a></li>
            <li><a href="/Logout">Uloskirjautuminen</a></li>
            <li></li>
            <li><a href="/Etsi">Etsi</a></li>
        </ul>

    </div>

</div>
</body>
</html>