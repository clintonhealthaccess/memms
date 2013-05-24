<g:if test="${entities == null || entities.empty}">
	<div><g:message code="entity.list.empty.label" args="[entityName]"/></div>
</g:if>
<div class="paginateButtons">
	<g:if test="${entityCount != null}">
		<g:paginate total="${entityCount}" params="${params}" action="${actionName}" />
	</g:if>
</div>