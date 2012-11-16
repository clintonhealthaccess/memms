<%@ page import="org.chai.memms.util.Utils" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<g:set var="entityName" value="${message(code: code)}" />
	<title><g:message code="notification.read.label" /> Notification </title>
	<r:require modules="chosen,fieldselection,cluetip,form"/>
</head>
<body>
	<div  class="entity-form-container togglable">
	<div class="heading1-bar">
		<h1>
			<g:message code="notification.equipment.read.label" />
		</h1>
	</div>
	
	<div class="main">
	<div class="comment-section">
	<ul class="comment-list">
	<li>
	<div class="comment-meta">
				<span class="comment-written-by">${notification.sender.firstname} ${notification.sender.lastname}</span>
				<span class="comment-written-on">${Utils.formatDateWithTime(notification?.writtenOn)}</span>
			</div>
			<div class="comment-content">${notification.content}</div>
  	
  	
	  		
  	</li>
  	<li>
  	<div class="comment-button">
	  			<a href="${createLink(uri: targetURI)}"><button class="medium" id="add-comment"><g:message code="default.button.back.label" args="${['']}"/></button></a>
	  		</div> 
	  		</li>
  	</ul>
  	</div>
  </div>
</div>
</body>
</html>