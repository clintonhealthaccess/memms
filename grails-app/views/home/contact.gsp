<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User.UserType" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="contact.page.title" /></title>
<r:require modules="chosen,form,tipsy,cluetip"/>
</head>
<body>
	<div class="main">
	<fieldset class="form-content">
				<h4 class="section-title">
					<span class="question-default"> <img
						src="${resource(dir:'images/icons',file:'star_small.png')}">
					</span>
					<g:message code="home.section.contact.description.label" default="Contact list" />
				</h4>
		<g:render template="/templates/contactList" model="[entities:users]"/>
		</fieldset>
	</div>
</body>
</html>