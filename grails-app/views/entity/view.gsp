<%@ page import="org.chai.memms.util.Utils" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code)}" />
	<title><g:message code="default.view.label" args="[entityName]" /></title>
	<r:require modules="chosen,fieldselection,cluetip,form,calendar"/>
</head>
<body>
	<g:render template="${template}" model="[model: model]"/>
</body>
</html>