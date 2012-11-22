<span class="right">
	<g:if test="${params.action != 'selectFacility'}">
		<shiro:hasPermission permission="equipment:create">
			<g:equipmentRegistration dataLocation="${dataLocation?.id}" entityName="${[entityName]}"/>
		</shiro:hasPermission>
		<a href="${createLinkWithTargetURI(controller: 'equipmentView', action:'export', params:['location.id': location?.id,'dataLocation.id': dataLocation?.id])}" class="next medium gray left export push-r">
			<g:message code="default.export.label" />
		</a>
		<g:searchBox action="search"  controller="equipmentView"/>
	</g:if>
</span>

