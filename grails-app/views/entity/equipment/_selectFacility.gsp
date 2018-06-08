<table class="items spaced">
	<thead>
		<th><g:message code="datalocation.label" /></th>
		<th><g:message code="inventory.equipment.count" /></th>
		<th colspan="2"></th>
	</thead>
	<tbody>
		<g:each in="${inventories}" var="inventory">
			<tr>
				<td>
					${inventory.dataLocation.names}
				</td>
				<td>
					${inventory.equipmentCount}
				</td>
				<td>
					<shiro:hasPermission permission="equipment:create">
						<a href="${createLinkWithTargetURI(controller: 'equipment', action:'create', params:['dataLocation.id': inventory.dataLocation.id])}"><g:message code="inventory.add.equipment.label" /></a>
					</shiro:hasPermission>
				</td>
				<td>
					<shiro:hasPermission permission="equipment:list">
						<a href="${createLinkWithTargetURI(controller: 'equipmentView', action: 'list', params:['dataLocation.id': inventory.dataLocation.id] )}"><g:message code="inventory.manage.equipment.label" /></a>
					</shiro:hasPermission>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
</body>
</html>
