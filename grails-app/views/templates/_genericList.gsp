<div class="entity-list">
	<div>
		<div class="heading1-bar">
	     	<h1><g:message code="default.list.label" args="[entityName]" /></h1>
	     	<g:if test="${!search}">
		     	<span class="right">
		     	<shiro:hasPermission permission="${controllerName}:create">
					<g:if test="${!addTemplate}">
		  				<a href="${createLinkWithTargetURI(controller: controllerName, action:'create', params:[id:equipment?.id,location: dataLocation?.id])+(request.queryString==null?'':'&'+request.queryString)}" class="next medium left push-r">
		  					<g:message code="default.new.label" args="[entityName]"/>
		  				</a>
		  			</g:if>
		  			<g:else>
		  				<g:render template="${addTemplate}"/>
		  			</g:else>
		  			
		  			</shiro:hasPermission>
		  			
		  			<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('importer')}">
	  				&nbsp;
		  				<g:if test="${entityClass != null}">
			  				<a
							href="${createLinkWithTargetURI(controller: 'task', action:'taskForm', params:[class: importTask, entityClass: entityClass.name])+(request.queryString==null?'':'&'+request.queryString)}" class="next medium gray left import">
							<g:message code="default.import.label" />
						</a>
		  				</g:if>
	  				</g:if>
		  			<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('export')}">
		  			&nbsp;
			  			<a href="${createLinkWithTargetURI(controller: 'task', action:'create',params:[class: exportTask, entityClass: entityClass.name])+(request.queryString==null?'':'&'+request.queryString)}" class="next medium gray left export">
		  					<g:message code="default.export.label" />
		  				</a>
	  				</g:if>

	  				<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('search')}">
        				<g:searchBox action="search"/>
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
