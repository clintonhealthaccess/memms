<table class="items spaced">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" defaultOrder="asc" title="${message(code: 'equipment.code.label')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="type" defaultOrder="asc" title="${message(code: 'equipment.type.label')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="model" defaultOrder="asc" title="${message(code: 'equipment.model.label')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<th><g:message code="location.label"/></th>
			<g:sortableColumn property="currentStatus" defaultOrder="asc" title="${message(code: 'equipment.status.label')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="obsolete" defaultOrder="asc" title="${message(code: 'equipment.obsolete.label')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="manufacturer" defaultOrder="asc" title="${message(code: 'provider.type.manufacturer')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="supplier" defaultOrder="asc" title="${message(code: 'provider.type.supplier')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<g:sortableColumn property="purchaser" defaultOrder="asc" title="${message(code: 'equipment.purchaser.label')}" params="[q:q,'dataLocation.id':dataLocation?.id]" />
			<th><g:message code="work.order.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="equipment">
			<tr >
				<td>
					<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'equipment', action:'edit', params:[id: equipment.id])}"  class="edit-button">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:'equipment', action:'delete', params:[id: equipment.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td>${equipment.code}</td>
				<td>${equipment.type.names}</td>
				<td>${equipment.model}</td>
				<td>
					<g:message code="datalocation.label"/>: ${equipment.dataLocation.names}<br/>
					<g:message code="department.label"/>: ${equipment.department.names}<br/>
					<g:message code="equipment.room.label"/>: ${equipment.room}<br/>
				</td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'equipmentStatus', action:'create', params:['equipment.id': equipment?.id])}" title="${message(code: 'tooltip.click.update.status')}" class="tooltip">
  	    				${message(code: equipment.currentStatus?.messageCode+'.'+equipment.currentStatus?.name)}
  	    			</a>
				</td>
				<td>
					<g:listCheckBox name="obsolete" id="${equipment.id}" checked="${(!equipment.obsolete)?:'checked'}"/>
				</td>
				<td>${equipment.manufacturer.contact.contactName}</td>
				<td>${equipment.supplier.contact.contactName}</td>
				
				
				<td>${message(code: equipment.purchaser?.messageCode+'.'+equipment.purchaser?.name)}</td>
				
				<td>
					<a href="${createLinkWithTargetURI(controller:'workOrderView', action:'list', params:['equipment.id': equipment?.id])}" title="${message(code: 'work.order.see.list.label')}" class="tooltip">
  	    				${equipment.workOrders?.size()}
  	    			</a>
				</td>
			</tr>
		</g:each>
	</tbody>	
</table>
<g:render template="/templates/pagination" />
<script type="text/javascript">
	$(document).ready(function() {
		updateEquipment("${createLink(controller:'equipmentView',action: 'updateObsolete')}");
	});
</script>

