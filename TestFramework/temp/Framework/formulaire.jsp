<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="etu2802.validation.FormErrorHandler" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Index</title>
    <style>
    /* Conteneur principal */
    body {
        font-family: Arial, sans-serif;
        background-color: #f8f9fa;
        margin: 0;
        padding: 20px;
    }

    .form-container {
        max-width: 600px;
        margin: 0 auto;
        background: #ffffff;
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    .form-container h3 {
        text-align: center;
        color: #333;
        margin-bottom: 20px;
    }

    /* Groupes de formulaire */
    .form-group {
        margin-bottom: 15px;
    }

    .form-group label {
        display: block;
        font-weight: bold;
        margin-bottom: 5px;
        color: #555;
    }

    .form-group input[type="text"],
    .form-group input[type="number"],
    .form-group input[type="file"] {
        width: 100%;
        padding: 10px;
        font-size: 14px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
        transition: border-color 0.3s;
    }

    .form-group input[type="text"]:focus,
    .form-group input[type="number"]:focus,
    .form-group input[type="file"]:focus {
        border-color: #007bff;
        outline: none;
    }

    /* Gestion des erreurs */
    .form-group.has-error input {
        border-color: #dc3545;
        background-color: #f8d7da;
    }

    .error-message {
        display: block;
        font-size: 12px;
        color: #dc3545;
        margin-top: 5px;
    }

    /* Bouton de soumission */
    .submit-button {
        display: block;
        width: 100%;
        background-color: #007bff;
        color: white;
        font-size: 16px;
        font-weight: bold;
        padding: 10px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.3s;
    }

    .submit-button:hover {
        background-color: #0056b3;
    }

    /* Lien de navigation */
    .nav-link {
        display: block;
        text-align: center;
        margin-top: 15px;
        color: #007bff;
        text-decoration: none;
        font-weight: bold;
        transition: color 0.3s;
    }

    .nav-link:hover {
        color: #0056b3;
    }

    </style>
</head>
<body>
    <div class="form-container">
    <h3>Ajout Employé</h3>

    <%
        Map<String, List<String>> errors = (Map<String, List<String>>) session.getAttribute("errors");
        Map<String, String> validFormData = (Map<String, String>) session.getAttribute("validFormData");

        if (errors == null) errors = new HashMap<>();
        if (validFormData == null) validFormData = new HashMap<>();
    %>
    
    <form action="save_employer" method="post" enctype="multipart/form-data">
        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "id") %>">
        <label for="id">Id :</label>
            <input type="text" id="id" name="employer.id" 
                value="<%= FormErrorHandler.getValueOrDefault(validFormData, "employer.id", "") %>">
            <%= FormErrorHandler.renderErrors(errors, "id") %>
        </div>

        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "nom") %>">
            <label for="nom">Nom :</label>
            <input type="text" id="nom" name="employer.nom" 
                value="<%= FormErrorHandler.getValueOrDefault(validFormData, "employer.nom", "") %>">
            <%= FormErrorHandler.renderErrors(errors, "nom") %>
        </div>

        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "age") %>">
            <label for="age">Age :</label>
            <input type="text" id="age" name="employer.age" 
                value="<%= FormErrorHandler.getValueOrDefault(validFormData, "employer.age", "") %>">
            <%= FormErrorHandler.renderErrors(errors, "age") %>
        </div>

        <div class="form-group <%= FormErrorHandler.getErrorClass(errors, "image") %>">
            <label for="image">Image :</label>
            <input type="file" id="image" name="employer.image" accept="image/*">
            <%= FormErrorHandler.renderErrors(errors, "image") %>
        </div>

        <input type="submit" value="Valider" class="submit-button">
    </form>

    <a href="emp" class="nav-link">Liste Employés</a>
</div>
</body>
</html>
<% 
    // Nettoyer les erreurs et les valeurs valides après affichage
    session.removeAttribute("errors");
    session.removeAttribute("validFormData"); 
%>
