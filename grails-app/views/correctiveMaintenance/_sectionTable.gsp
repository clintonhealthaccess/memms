<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>

		<table class="items">
			<thead>
				<th><g:message code="datalocation.label" /></th>
				<th><g:message code="correctiveMaintenance.workOrder.count" /></th>
				<th></th>
			</thead>
			<tbody>
				<g:each in="${correctiveMaintenances}" var="correctiveMaintenance">
					<tr>
						<td>
							${correctiveMaintenance.dataLocation.names}
						</td>
						<td>
							${correctiveMaintenance.workOrderCount}
						</td>
						<td>
							<shiro:hasPermission permission="workOrder:list">
								<a href="${createLink(controller: 'workOrder', action: 'list', params:['dataLocation.id': correctiveMaintenance.dataLocation.id] )}"><g:message code="correctiveMaintenance.manage.workOrder.label" /></a>
							</shiro:hasPermission>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>
		<div class="paginateButtons">
		<g:paginate total="${entityCount}" params="${params}" action="${actionName}"/>
		 </div>

</body>
</html>
