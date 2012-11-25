<span class="right">
	<shiro:hasPermission permission="workOrder:create">
		<a href="${createLinkWithTargetURI(controller:'workOrder', action:'create', params:['dataLocation.id': dataLocation?.id,'equipment.id':equipment?.id])}" class="next medium left push-r"> 
			<g:message code="default.new.label" args="[entityName]" />
		</a>
	</shiro:hasPermission>
	<g:searchBox action="search"  controller="workOrderView"/>
</span>
	