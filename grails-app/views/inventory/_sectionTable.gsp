<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
	<div class="main">
		<table class="listing">
			<thead>
				<th><g:message code="inventory.datalocation.label" /></th>
				<th><g:message code="inventory.equipment.count" /></th>
				<th></th>
				<th></th>
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
								<a href="${createLink(controller: 'equipment', action: 'create', params:[location: inventory.dataLocation.id])}"><g:message code="inventory.add.equipment.label" /></a>
							</shiro:hasPermission>
						</td>
						<td>
							<shiro:hasPermission permission="equipment:list">
								<a href="${createLink(controller: 'equipment', action: 'list', params:[location: inventory.dataLocation.id] )}"><g:message code="inventory.manage.equipment.label" /></a>
							</shiro:hasPermission>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</body>
</html>
