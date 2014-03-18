<%@ page import="org.chai.memms.util.Utils" %>
<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:q]" title="${message(code: 'entity.code.label')}" />
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:q]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="dateCreated" params="[q:q]" title="${message(code: 'hmis.equipment.type.dateCreated.label')}" />
			<g:sortableColumn property="lastUpdated" params="[q:q]" title="${message(code: 'hmis.equipment.type.lastUpdated.label')}" />
			<th><g:message code="number.of.equipment.type.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="type">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<shiro:hasPermission permission="hmisEquipmentType:edit">
								<a href="${createLinkWithTargetURI(controller:'hmisEquipmentType', action:'edit', params:[id: type.id])}" class="edit-button"> 
								<g:message code="default.link.edit.label" />
								</a>
							</shiro:hasPermission>
						</li>
						<li>
							<shiro:hasPermission permission="hmisEquipmentType:delete">
								<a href="${createLinkWithTargetURI(controller:'hmisEquipmentType', action:'delete', 
								params:[id: type.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button">
								<g:message code="default.link.delete.label" />
								</a>
							</shiro:hasPermission>
						</li>	
					</ul>
				</td>
				<td>
					${type.code}
				</td>
				<td>
					${type.names}
				</td>
				<td>
					${Utils.formatDate(type.dateCreated)}
				</td>
				<td>
					${Utils.formatDate(type.lastUpdated)}
				</td>
				<td>
					${type.equipmentTypes.size()}
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />
