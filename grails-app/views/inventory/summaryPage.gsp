<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<g:set var="entityClass" value="${entityClass}" />
		<title><g:message code="inventory.summary.title" /></title>
		<r:require modules="chosen, dropdown"/>
	</head>
	<body>
		<div>
			<div class="filter-bar">
				<g:locationFilter linkParams="${[order:'desc']}" selected="${currentLocation}" selectedTypes="${currentLocationTypes}" skipLevels="${locationSkipLevels}"/>
				<g:dataLocationTypeFilter linkParams="${params}" selected="${currentLocationTypes}"/>
			</div>
						
			<div class="main">
				<g:if test="${inventories == null}">
					<p class="nav-help"> <g:message code="inventory.summary.selectlocation.text"/></p>
				</g:if>
				<g:else>
				<div>
					<div>
						<g:message code="location.label" />:${currentLocation.names}
					</div>
				</div>
				<g:render template="${template}"/>
				</g:else>
				
			</div>
		</div>
	</body>
</html>