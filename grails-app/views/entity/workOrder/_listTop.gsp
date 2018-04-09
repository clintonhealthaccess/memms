<span class="right">
	<shiro:hasPermission permission="workOrder:create">
		<a href="${createLinkWithTargetURI(controller:'workOrder', action:'create', params:['dataLocation.id': dataLocation?.id,'equipment.id':equipment?.id])}" class="next medium left push-r"> 
			<g:message code="default.new.label" args="[entityName]" />
		</a>
	</shiro:hasPermission>
		<a href="${createLinkWithTargetURI(controller: 'workOrderView', action:'export', params:['dataLocation.id': dataLocation?.id])}" class="next medium gray left export push-r">
			<g:message code="default.export.label" />
		</a>
	<g:searchBox action="search"  controller="workOrderView"/>
</span>
	