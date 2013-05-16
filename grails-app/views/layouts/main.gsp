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
					<% def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])%>
					<li>
						<a href="${createLinkWithTargetURI(controller: 'account', action:'editAccount')}">
							<g:message code="header.navigation.myaccount"/> : ${user.names}
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
					<a href="#">
					<g:message code="header.labels.helpdesk"/></a>
				</li>
			</ul>
      
    </div>
    
  </div>
  <% def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true]) %>
  <div id="navigation">
    <div class="wrapper">
      <ul id="main-menu" class="menu">
    	<shiro:hasPermission permission="menu:home">
			<li>
				<a class="${controllerName=='home'?'active':''}" href="${createLink(controller:'home', action:'index')}">
					<g:message code="header.navigation.home"/>
				</a>
			</li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:inventory">
			<li>
				<a class="${controllerName=='equipment' || controllerName=='equipmentView' || controllerName=='notificationEquipment' ?'active':''}" href="#">
					<g:message code="header.navigation.inventory"/>
				</a>
				<ul class="submenu">
					<li>
						<a class="${controllerName=='equipment' || controllerName=='equipmentView' ?'active':''}" href="${createLink(controller:'equipmentView', action:'summaryPage')}">
							<g:message code="header.navigation.inventory"/>
						</a>
					</li>
					<li>
						<a class="${controllerName=='notificationEquipment' ?'active':''}" href="${createLink(controller:'notificationEquipment', action:'list')}">
							<g:message code="header.navigation.notification.equipment"/>
						</a>
					</li>
				</ul>
	         </li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:sparePart">
			<li>
				<a class="${controllerName=='sparePart' || controllerName=='sparePartView' ?'active':''}" href="${createLink(controller: 'sparePartView', action:'list')}">
					<g:message code="spare.part.label"/>
				</a>
			</li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:maintenance">
			<li><a class="${controllerName=='workOrder' || controllerName=='workOrderView' || controllerName=='preventiveOrder' || controllerName=='preventiveOrderView' || controllerName=='notificationWorkOrder' ?'active':''}" href="#"><g:message code="header.navigation.maintenance"/></a>
				<ul class="submenu">
					<shiro:hasPermission permission="menu:correctivemaintenance">
						<li>
							<a class="${controllerName=='workOrder' || controllerName=='workOrderView' ?'active':''}" href="${createLink(controller:'workOrderView', action:'summaryPage')}">
								<g:message code="header.navigation.corrective.maintenance"/>
							</a>
						</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:preventivemaintenance">
						<li>
							<a class="${controllerName=='preventiveOrder' || controllerName=='preventiveOrderView' ?'active':''}" href="${createLink(controller:'preventiveOrderView', action:'summaryPage')}">
								<g:message code="header.navigation.preventive.maintenance"/>
							</a>
						</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:notificationWorkOrder">
						<li>
							<a class="${controllerName=='notificationWorkOrder' ?'active':''}" href="${createLink(controller: 'notificationWorkOrder', action:'list')}">
								<g:message code="notification.work.order.label"/>
							</a>
						</li>
					</shiro:hasPermission>
	         	</ul>
         	</li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:reports">
			<li><a class="${controllerName=='report'?'active':''}" href="#"><g:message code="header.navigation.reports"/></a>
				<ul class="submenu">
					%{-- <shiro:hasPermission permission="menu:dashboard">
							<li>
								<a href="#${createLink(controller:'dashboardReport', action:'dashboard')}">
									<g:message code="header.navigation.reports.dashboard"/>
								</a>
							</li>
					</shiro:hasPermission>--}%
					<shiro:hasPermission permission="menu:listing">
							<li>
								<a href="${createLink(controller:'listing', action:'view') }">
									<g:message code="header.navigation.reports.listing"/>
								</a>
							</li>
					</shiro:hasPermission> 
					<shiro:hasPermission permission="menu:equipmentExport">
							<li>
								<a href="${createLink(controller: 'equipmentView', action:'generalExport')}">
									<g:message code="equipment.export.label"/>
								</a>
							</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:sparePartExport">
							<li>
								<a href="${createLink(controller: 'sparePartView', action:'generalExport')}">
									<g:message code="spare.part.export.label"/>
								</a>
							</li>
					</shiro:hasPermission>
				</ul>
			</li>
		</shiro:hasPermission>
		<shiro:hasPermission permission="menu:admin">
			<li><a href="#"><g:message code="header.navigation.administration"/></a>
	         	<ul class="submenu">
	         		<shiro:hasPermission permission="menu:equipmentType">
						<li>
							<a href="${createLink(controller: 'equipmentType', action:'list')}">
								<g:message code="equipment.type.label"/>
							</a>
						</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:sparePartType">
						<li>
							<a href="${createLink(controller: 'sparePartType', action:'list')}">
								<g:message code="spare.part.type.label"/>
							</a>
						</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:location">
						<li><a href="#"><g:message code="location.label"/></a>
							<div class="sub-submenu">
								<ul class="submenu">
									<li><a href="${createLink(controller: 'location', action:'list')}"><g:message code="location.label"/></a></li>
									<li><a href="${createLink(controller: 'locationLevel', action:'list')}"><g:message code="location.level.label"/></a></li>
									<li><a href="${createLink(controller: 'dataLocation', action:'list')}"><g:message code="datalocation.label"/></a></li>
									<li><a href="${createLink(controller: 'dataLocationType', action:'list')}"><g:message code="datalocation.type.label"/></a></li>
								</ul>
							</div>
						</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:department">
						<li><a href="${createLink(controller: 'department', action:'list')}"><g:message code="department.label"/></a></li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:provider">
						<li><a href="${createLink(controller: 'provider', action:'list')}"><g:message code="header.navigation.manufacturer.and.supplier"/></a></li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:user">
						<li><a href="#"><g:message code="user.manage.users.label"/></a>
							<div class="sub-submenu">
								<ul class="submenu">
									<shiro:hasPermission permission="menu:role">
										<li><a href="${createLink(controller: 'role', action:'list')}"><g:message code="roles.label" /></a></li>
									</shiro:hasPermission>
									<shiro:hasPermission permission="menu:user">
										<li><a href="${createLink(controller: 'user', action:'list')}"><g:message code="users.label" /></a></li>
									</shiro:hasPermission>
								</ul>
							</div>
						</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:managehelp">
						<li><a href="#"><g:message code="header.manage.help.label"/></a></li>
					</shiro:hasPermission>
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
	<div class="build-info">
        <build:buildInfo/>
    </div>
<r:layoutResources/>

</body>
</html>
