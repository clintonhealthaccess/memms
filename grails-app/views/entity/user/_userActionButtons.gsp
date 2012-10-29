<shiro:hasPermission permission="${controllerName}:create">
	<g:if test="${!addTemplate}">
		<a href="${createLinkWithTargetURI(controller: (controller)?controller:controllerName, action:'create', params:['location.id': location?.id,'dataLocation.id': dataLocation?.id,'equipment.id':equipment?.id,'workOrder.id':workOrder?.id])}"
			class="next medium left push-r"> <g:message code="default.new.label" args="[entityName]" />
		</a>
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