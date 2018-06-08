<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User.UserType" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<title><g:message code="landingpage.title" /></title>
  <r:require modules="chosen,form,tipsy,cluetip"/>
</head>
<body>
  <g:if test="${!user.userType.equals(UserType.TECHNICIANDH) && !user.userType.equals(UserType.TECHNICIANMMC)}">
  <h2 class="login-heading"><g:message code="landingpage.welcome.label"/></h2>
	<div class="form-box wide">
  	<ul class="todo">
  	<shiro:hasPermission permission="menu:inventory">
  		<li><a href="${createLink(controller: 'equipmentView', action:'summaryPage')}"><g:message code="header.navigation.equipment"/></a></li>
  		</shiro:hasPermission>
  		<shiro:hasPermission permission="menu:inventory">
  		<li><a href="${createLink(controller:'infrastructureView', action:'summaryPage')}"> <g:message code="header.navigation.infrastructure"/></a> </li>
  		</shiro:hasPermission>
  		<shiro:hasPermission permission="menu:correctivemaintenance">
  		<li><a href="${createLink(controller: 'workOrderView', action:'summaryPage')}"><g:message code="header.navigation.corrective.maintenance"/></a></li>
  		</shiro:hasPermission>
  		<shiro:hasPermission permission="menu:preventivemaintenance">
  		<li><a href="${createLink(controller: 'preventiveOrderView', action:'summaryPage')}"><g:message code="header.navigation.preventive.maintenance"/></a></li>
  		</shiro:hasPermission>
  		<shiro:hasPermission permission="menu:dashboard">
            <li>
              <a href="${createLink(controller:'dashboard', action:'view')}">
                  <g:message code="header.navigation.reports.dashboard"/>
              </a>
            </li>
        </shiro:hasPermission>
        <shiro:hasPermission permission="menu:listing">
            <li>
               <a href="${createLink(controller:'listing', action:'view') }">
                  <g:message code="header.navigation.reports.listing"/>
               </a>
             </li>
        </shiro:hasPermission>
  	</ul>
  </div>
  </g:if>
  <g:else>
    <div class="heading1-bar">
      <g:if test="${user.userType.equals(UserType.TECHNICIANMMC)}">
        <h1><g:message code="header.navigation.corrective.maintenance.escalated.to.mmc" /></h1>
      </g:if>
      <g:if test="${user.userType.equals(UserType.TECHNICIANDH)}">
        <h1><g:message code="header.navigation.corrective.maintenance.opened.at.fosa" /></h1>
      </g:if>
    </div>
    <g:render template="/templates/homeWorkOrder" model="[entities:workOrders]"/>
  </g:else>
</body>
</html>