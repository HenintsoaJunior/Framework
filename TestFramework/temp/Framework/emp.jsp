<%@page import="java.util.List"%>
<%@page import="modele.Emp"%>
<% 
    List<Emp> list_emp = (List<Emp>)request.getAttribute("emp");
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
                <% for (Emp emp : list_emp) { %>
                <tr>
                    <td><%= emp.getId() %></td>
                    <td><%= emp.getNom() %></td>
                    <td><%= emp.getAge() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
        
    </body>
</html>
