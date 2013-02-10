<table class="items">
	<thead>
		<th><g:message code="datalocation.label" /></th>
		<th>${message(code:code)}</th>
		<th>View mapper</th>
		<th></th>
	</thead>
	<tbody>
		<g:each in="${maintenances}" var="maintenance">
			<tr>
				<td>
					${maintenance.dataLocation.names}
				</td>
				<td>
					${maintenance.orderCount}
				</td>
				<td>
				  <a href="${createLinkWithTargetURI(controller:'durationBasedOrder',action:'calendar',params:['dataLocation.id': dataLocation?.id])}">
            <img src="${resource(dir:'images',file:'icon_calendar2.png')}" alt="View mapper"/>
          </a>
				</td>
				<td>
					<shiro:hasPermission permission="order:list">
						<a href="${createLink(controller: controller, action: 'list', params:['dataLocation.id': maintenance.dataLocation.id] )}">
							<g:message code="entity.list.manage.label" />
						</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<div class="paginateButtons">
	<g:paginate total="${entityCount}" params="${params}" action="${actionName}"/>
</div>

