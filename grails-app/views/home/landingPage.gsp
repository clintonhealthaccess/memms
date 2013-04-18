<%@ page import="org.apache.shiro.SecurityUtils" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<title><g:message code="landingpage.title" /></title>
</head>
<body>
  <h2 class="login-heading"><g:message code="landingpage.welcome.label"/></h2>
	<div class="form-box wide">
  	<ul class="todo">
  	<shiro:hasPermission permission="menu:inventory">
  		<li><a href="${createLink(controller: 'equipmentView', action:'summaryPage')}"><g:message code="header.navigation.inventory"/></a></li>
  		</shiro:hasPermission>
  		<shiro:hasPermission permission="menu:correctivemaintenance">
  		<li><a href="${createLink(controller: 'workOrderView', action:'summaryPage')}"><g:message code="header.navigation.corrective.maintenance"/></a></li>
  		</shiro:hasPermission>
  		<shiro:hasPermission permission="menu:preventivemaintenance">
  		<li><a href="${createLink(controller: 'preventiveOrderView', action:'summaryPage')}"><g:message code="header.navigation.preventive.maintenance"/></a></li>
  		</shiro:hasPermission>
  		<shiro:hasPermission permission="menu:reports">
  		<li><a href="#"><g:message code="header.navigation.reports"/></a></li>
  		</shiro:hasPermission>
  	</ul>
  </div>
</body>
</html>