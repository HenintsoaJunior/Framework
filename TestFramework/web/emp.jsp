<%-- 
    Document   : emp
    Created on : 31 mai 2024, 13:16:00
    Author     : Henintsoa
--%>
<%@page import="modele.Emp"%>
<% 
    Emp[] list_emp = (Emp[])request.getAttribute("emp");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Liste des employés</title>
        <style>
            table {
                width: 100%;
                border-collapse: collapse;
            }
            table, th, td {
                border: 1px solid black;
            }
            th, td {
                padding: 8px;
                text-align: left;
            }
            th {
                background-color: #f2f2f2;
            }
        </style>
    </head>
    <body>
        
        <h1>Liste des employés</h1>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nom</th>
                    <th>Age</th>
                </tr>
            </thead>
            <tbody>
                <% for(int j = 0; j < list_emp.length; j++) { %>
                <tr>
                    <td><%= list_emp[j].getId() %></td>
                    <td><%= list_emp[j].getNom() %></td>
                    <td><%= list_emp[j].getAge() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
        
    </body>
</html>
