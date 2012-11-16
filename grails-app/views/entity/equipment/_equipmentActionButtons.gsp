<shiro:hasPermission permission="${controllerName}:create">
	<g:if test="${!addTemplate}">
		<g:equipmentRegistration dataLocation="${dataLocation.id}" entityName="${[entityName]}"/>
	</g:if>
	<g:else>
		<g:render template="${addTemplate}" />
	</g:else>

</shiro:hasPermission>

<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('importer')}"> &nbsp;
	 <g:if test="${entityClass != null}">
		<a href="${createLinkWithTargetURI(controller: 'task', action:'taskForm', params:[class: importTask, entityClass: entityClass.name])}" class="next medium gray left import"> 
		<g:message code="default.import.label" />
		</a>
	</g:if>
</g:if>
<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('exporter')}"> &nbsp;
	<a href="${createLinkWithTargetURI(controller: controllerName, action:'exporter',params:[class: exportTask, entityClass: entityClass.name])}" class="next medium gray left export">
	 <g:message code="default.export.label" />
	</a>
</g:if>

<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('export')}"> &nbsp;
	<a href="${createLinkWithTargetURI(controller: controllerName,action:'export')+(request.queryString==null?'':'&'+request.queryString)}" class="next medium gray left export">
		<g:message code="default.export.label" />
	</a>
</g:if>