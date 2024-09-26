<%@ page import="java.util.List" %>
<%@ page import="modele.Emp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Data List</title>
</head>
<body>
    <% 
        List<Emp> users = (List<Emp>) session.getAttribute("users");
        if (users != null && !users.isEmpty()) { 
    %>
        <h3>Liste des employés :</h3>
        <ul>
        <% for (Emp user : users) { %>
            <li><%= user.getNom() %> - <%= user.getAge() %> ans</li>
        <% } %>
        </ul>

        <p>Contenu de la page...</p>

        <form action="logout" method="post">
            <input type="submit" value="Déconnexion">
        </form>
    <% } else { %>
        <h2>Session non trouvée</h2>
        <p>Veuillez vous connecter pour accéder à cette page.</p>
    <% } %>
</body>
</html>
