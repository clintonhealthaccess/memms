<table class="items spaced">
	<thead>
		<th><g:message code="datalocation.label" /></th>
		<th></th>
	</thead>
	<tbody>
		<g:each in="${dataLocations}" var="dataLocation">
			<tr>
				<td>
					${dataLocation.names}
				</td>
				<td>
					<shiro:hasPermission permission="equipment:create">
						<a href="${createLinkWithTargetURI(controller: 'equipment', action:'create', params:['dataLocation.id': dataLocation.id])}"><g:message code="inventory.add.equipment.label" /></a>
					</shiro:hasPermission>
				</td>
				<td>
					<shiro:hasPermission permission="equipment:list">
						<a href="${createLink(controller: 'equipmentView', action: 'list', params:['dataLocation.id': dataLocation.id] )}"><g:message code="inventory.manage.equipment.label" /></a>
					</shiro:hasPermission>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
</body>
</html>
