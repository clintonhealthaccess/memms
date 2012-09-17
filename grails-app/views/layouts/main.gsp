<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle /></title>
	<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	<g:layoutHead />	
	<r:require module="core"/>
	<r:layoutResources/>

</head>
<body>
  <div id="header">
    <div class="wrapper">
      
      <h1 id="logo">
        <a href="${createLink(controller:'home', action:'index')}"><g:message code="title.memms"/></a>
      </h1>
      
      <ul class="locales" id="switcher">
				<% def languageService = grailsApplication.mainContext.getBean('languageService') %>
				<g:each in="${languageService.availableLanguages}" var="language" status="i">
					<% params['lang'] = language %>
					<li><a class="${languageService.currentLanguage.language==language? 'no-link':''}" href="${createLink(controller: controllerName, action: actionName, params: params)}">${language}</a></li>
				</g:each>
			</ul>
      
      <ul id="top_nav">
				<shiro:user>
					<%
						def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])
					%>
					<li>
						<a href="${createLinkWithTargetURI(controller: 'account', action:'editAccount')}">
							<g:message code="header.navigation.myaccount"/> : ${user?.firstname} ${user?.lastname}
						</a>
					</li>
				</shiro:user>
				<shiro:user>
					<li>
						<a href="${createLink(controller: 'auth', action: 'signOut')}" class="no-link"><g:message code="header.labels.logout"/></a>
					</li>
				</shiro:user>
				<shiro:notUser>
					<g:if test="${controllerName != 'auth' || actionName != 'login'}">
						<li>
							<a href="${createLink(controller: 'auth', action: 'login')}"><g:message code="header.labels.login"/></a>
						</li>
					</g:if>
					<g:if test="${controllerName != 'auth' || actionName != 'register'}">
						<li>
							<a href="${createLink(controller: 'auth', action: 'register')}"><g:message code="header.labels.register"/></a>
						</li>
					</g:if>
				</shiro:notUser>
			</ul>
      
      <h2>
        <span class="right">
          <img src="${resource(dir:'images',file:'rwanda.png')}" alt="Rwanda coat of arms" width="33" />
        </span>
        <span><g:message code="header.labels.moh"/></span>
				<g:message code="header.labels.memms"/>
      </h2>
      
      <ul id="logout">
				<shiro:hasPermission permission="admin">
					<li>
						<a class="redmine follow" target="_blank" href="http://www.districthealth.moh.gov.rw/redmine/projects/memms">
						<g:message code="header.labels.redmine"/></a>
	   			</li>
				</shiro:hasPermission>
				<li>
					<a href="${createLink(uri:'/helpdesk')}">
					<g:message code="header.labels.helpdesk"/></a>
				</li>
			</ul>
      
    </div>
    
  </div>
  
  <div id="navigation">
    <div class="wrapper">
      <ul id="main-menu" class="menu">
    	<shiro:hasPermission permission="menu:home">
			<li><a class="${controllerName=='home'?'active':''}" href="${createLink(controller:'home', action:'index')}"><g:message code="header.navigation.home"/></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:inventory">
			<li><a class="${controllerName=='equipment'?'active':''}" href="${createLink(controller:'equipment', action:'summaryPage')}"><g:message code="header.navigation.inventory"/></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:correctivemaintenance">
			<li><a class="${controllerName=='dashboard'?'active':''}" href="#"><g:message code="header.navigation.corrective.maintenance"/></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:preventivemaintenance">
			<li><a class="${controllerName=='dashboard'?'active':''}" href="#"><g:message code="header.navigation.preventive.maintenance"/></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:reports">
			<li><a class="${controllerName=='dashboard'?'active':''}" href="#"><g:message code="header.navigation.reports"/></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:admin">
			<li><a href="#"><g:message code="header.navigation.administration"/></a>
	         	<ul class="submenu">
					<li><a href="${createLink(controller: 'department', action:'list')}"><g:message code="department.label"/></a></li>
					<li><a href="${createLink(controller: 'equipmentType', action:'list')}"><g:message code="equipment.type.label"/></a></li>
					<li><a href="${createLink(controller: 'provider', action:'list')}"><g:message code="header.navigation.manufacturer.and.supplier"/></a></li>
					<li><a href="${createLink(controller: 'dataLocation', action:'list')}"><g:message code="datalocation.label"/></a></li>
					<li><a href="${createLink(controller: 'dataLocationType', action:'list')}"><g:message code="datalocation.type.label"/></a></li>
					<li><a href="${createLink(controller: 'location', action:'list')}"><g:message code="location.label"/></a></li>
					<li><a href="${createLink(controller: 'locationLevel', action:'list')}"><g:message code="location.level.label"/></a></li>
					<li><a href="${createLink(controller: 'user', action:'list')}"><g:message code="user.label"/></a></li>
					<li><a href="${createLink(controller: 'equipment', action:'generalExport')}"><g:message code="equipment.export.label"/></a></li>
	         	</ul>
        	 </li>
		</shiro:hasPermission>
        
      </ul>
    </div>
  </div>
  
  <div class="flash-message">
		<g:if test="${flash.message}">
      <p>${flash.message}</p>
		</g:if>	
	</div>
	
	<div id="content">
	  <div class="wrapper">
		  <g:layoutBody />
		</div>
	</div>
	
	<div id="footer">
    <div class="wrapper">
      &copy;<g:message code="footer.labels.chai"/>
      <br>
      <a href="#"><g:message code="footer.labels.about"/></a>
      |
      <a href="#"><g:message code="footer.labels.contact"/></a>
      |
      <a href="#"><g:message code="footer.labels.helpdesk"/></a>
    </div>
  </div>
	
<r:layoutResources/>

</body>
</html>
