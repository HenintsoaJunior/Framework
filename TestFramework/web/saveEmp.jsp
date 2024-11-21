<%@page import="etu2802.FileUpload"%>
<%@page import="modele.Emp"%>

<%
    Emp employer = (Emp) request.getAttribute("employer");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Ajout Employé</title>
</head>
<body>
    <h5>Ajout Employé</h5>
    <p>
        <strong>ID:</strong> <%= employer.getId() %> <br>
        <strong>Nom:</strong> <%= employer.getNom() %> <br>
        <strong>Âge:</strong> <%= employer.getAge() %> <br>
        <strong>Image:</strong> <%= employer.getImage() != null ? employer.getImage().getName() : "Aucune image téléchargée" %>
    </p>

    <% if (employer.getImage() != null) { %>
        <% 
            // Convert bytes to Base64 for displaying the image
            String base64Image = java.util.Base64.getEncoder().encodeToString(employer.getImage().getBytes());
        %>
        <img src="data:image/jpeg;base64,<%= base64Image %>" alt="Image de l'employé" />
    <% } %>
        
</body>
</html>