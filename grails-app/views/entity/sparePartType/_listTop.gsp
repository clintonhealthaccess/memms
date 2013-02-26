<span class="right">
	<shiro:hasPermission permission="sparePartType:create">
		<a href="${createLinkWithTargetURI(controller:'sparePartType', action:'create',params:params)}" class="next medium left push-r"> 
			<g:message code="default.new.label" args="[entityName]" />
		</a>
	</shiro:hasPermission>
<g:searchBox action="search"  controller="sparePartType"/>
</span>


