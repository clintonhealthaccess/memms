<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>

<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle /></title>

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
					<li><a class="${languageService.currentLanguage==language?'no-link':''}" href="${createLink(controller: controllerName, action: actionName, params: params)}">${language}</a></li>
				</g:each>
			</ul>
      
      <ul id="top_nav">
				<shiro:user>
					<%
						def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])
					%>
					<li>
						<a href="${createLinkWithTargetURI(controller: 'account', action:'editAccount')}">
							<g:message code="header.navigation.myaccount"/> : ${user.firstname} ${user.lastname}
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
    
    <div>
  		<g:if test="${flash.message}">
  			<div>${flash.message}</div>
  		</g:if>	
  	</div>
    
  </div>
  
  <div id="navigation">
    <div class="wrapper">
      <ul id="main-menu" class="menu">
        <li><a href="#" class="active">First item</a></li>
        <li><a href="#">Second item</a></li>
        <li><a href="#">Third item</a></li>
        <li><a href="#">Fourth item</a></li>
        <li><a href="#">Fifth item</a></li>
      </ul>
    </div>
  </div>
	
	<div id="content">
	  <div class="wrapper">
		  <g:layoutBody />
		</div>
	</div>
	
	<div id="footer">
    <div class="wrapper">
      &copy; Clinton Health Access Initiative
      <br>
      <a href="#">About</a>
      |
      <a href="#">Contact</a>
    </div>
  </div>
	
<r:layoutResources/>

</body>
</html>
