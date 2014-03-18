<span class="right">
	<shiro:hasPermission permission="hmisEquipmentType:create">
		<a href="${createLinkWithTargetURI(controller:'hmisEquipmentType', action:'create',params:params)}" class="next medium left push-r"> 
			<g:message code="default.new.label" args="[entityName]" />
		</a>
	</shiro:hasPermission>
	<g:searchBox action="search"  controller="hmisEquipmentType"/>
</span>