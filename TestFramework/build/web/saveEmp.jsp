<%@page import="etu2802.FileUpload"%>
<%@page import="modele.Emp"%>

<% 
    Emp emp = (Emp) request.getAttribute("employer");
    String image = request.getParameter("image");
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
         
            <%= image %>
            
    </p>
</body>
</html>



