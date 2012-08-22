<%@ page import="org.apache.shiro.SecurityUtils" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<title><g:message code="landingpage.title" /></title>
</head>
<body>
	<h3><g:message code="landingpage.welcome.label"/></h3>
	
	<ul>
		<li><a href="${createLink(controller: 'Equipment', action:'list')}">Equipments</a></li>
		<li><a href="${createLink(controller: 'EquipmentStatus', action:'list')}">Equipment Status</a></li>
	</ul>
</body>
</html>