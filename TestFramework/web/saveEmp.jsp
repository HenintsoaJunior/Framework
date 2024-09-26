<%@page import="modele.Emp"%>

<% 
    Emp emp = (Emp) request.getAttribute("employer");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>test</title>
</head>
<body>

	<h5>Ajout Employer</h5>
	<p>
            <%= emp.getId() %>
            <%= emp.getNom() %>
            <%= emp.getAge() %>
            
    </p>
</body>
</html>



