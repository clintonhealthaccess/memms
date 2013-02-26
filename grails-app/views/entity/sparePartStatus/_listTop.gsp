<span class="right">
	<shiro:hasPermission permission="sparePartStatus:create">
		<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'create',params:params)}" class="next medium left push-r"> 
			<g:message code="default.new.label" args="[entityName]" />
		</a>
	</shiro:hasPermission>
</span>