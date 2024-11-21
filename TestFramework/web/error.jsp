<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html>
<head>
    <title>Erreur de validation</title>
    <style>
        .error-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ff4d4d;
            background-color: #ffe6e6;
            color: #ff3333;
            font-family: Arial, sans-serif;
        }
        h2 {
            color: #cc0000;
        }
        .error-message {
            font-size: 16px;
            margin-top: 10px;
        }
        .error-field {
            font-weight: bold;
            margin-top: 10px;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            text-decoration: none;
            color: #cc0000;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <h2>Erreur de Validation</h2>
        <div class="error-message">
            <%
                String errorMessages = (String) request.getAttribute("errorMessage");
                out.println("<div class='error-field'>");
                out.println("Error"+ errorMessages);
                out.println("</div>");
            
            %>
         </div>
        <a href="javascript:history.back()" class="back-link">← Retourner à la page précédente</a>
    </div>
</body>
</html>