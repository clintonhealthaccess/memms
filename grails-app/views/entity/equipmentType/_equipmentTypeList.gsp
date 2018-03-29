<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:q]" title="${message(code: 'equipment.type.code.label')}" />
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:q]" title="${message(code: 'entity.name.label')}" />
			<th><g:message code="entity.description.label"/></th>
			<g:sortableColumn property="observation" params="[q:q]" title="${message(code: 'entity.observation.label')}" />
			<g:sortableColumn property="expectedLifeTime" params="[q:q]" title="${message(code: 'equipmentType.expectedLifeTime.label')}" />
			<g:sortableColumn property="dateCreated" params="[q:q]" title="${message(code: 'equipment.type.added.on.label')}" />
			<g:sortableColumn property="lastUpdated" params="[q:q]" title="${message(code: 'equipment.type.last.modified.on.label')}" />
			<th/>
		</tr>
	</thead>
	<g:render template="/entity/equipmentType/listBody" model="[entities: entities]"/>
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />
