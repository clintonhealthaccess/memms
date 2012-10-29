<div class="entity-list">
	<div>
		<div class="heading1-bar">
	     	<h1><g:message code="default.list.label" args="[entityName]" /></h1>
	     	<g:if test="${!search}">
		     	<span class="right">
		     		<g:render template="${ '/entity/' + actionButtonsTemplate}" />
	  				<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('search')}">
        				<g:searchBox action="search" params="${workOrder?[workOrder:workOrder.id,read:params.read]:'' }"/>
        			</g:if>
		     	</span>
	     	</g:if>
		</div>
		<shiro:hasPermission permission="${controllerName}:filter">
			<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('filter') && filterTemplate != null }">
				<!-- Filter starts here-->
				<g:render template="${filterTemplate}" />
			</g:if>
		</shiro:hasPermission>
		<!-- Template goes here -->

		<div class="main table">
			<g:render template="${template}" />
			<div class="paginateButtons">
				<g:if test="${entityCount != null}">
					<g:paginate total="${entityCount}" params="${params}" action="${actionName}" />
				</g:if>
			</div>
		</div>

		<g:if test="${entities.empty}">
			<div class="main"><g:message code="entity.list.empty.label" args="[entityName]"/></div>
		</g:if>
		<!-- End of template -->
		
	</div>
</div>
