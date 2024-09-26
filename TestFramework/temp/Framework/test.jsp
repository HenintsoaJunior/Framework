<%@page import="test.Formulaire"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>test</title>
</head>
<body>

	<h5>Ajout Employer</h5>
	<p>
            <%= request.getParameter("id") %>
            <%= request.getParameter("nom") %>
            <%= request.getParameter("age") %>
            
    </p>
</body>
</html>



