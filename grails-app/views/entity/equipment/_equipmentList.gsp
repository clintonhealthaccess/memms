<table class="items spaced">

	<thead>
		<tr>
			<th></th>
			<g:sortableColumn property="serialNumber" defaultOrder="asc" title="${message(code: 'equipment.serial.number.label', default: 'Serial Number')}" params="[q:q,location:dataLocation.id]" />
			<g:sortableColumn property="type" defaultOrder="asc" title="${message(code: 'equipment.type.label', default: 'Type')}" params="[q:q,location:dataLocation.id]" />
			<g:sortableColumn property="model" defaultOrder="asc" title="${message(code: 'equipment.model.label', default: 'Model')}" params="[q:q,location:dataLocation.id]" />
			
			<g:sortableColumn property="manufacture" defaultOrder="asc" title="${message(code: 'provider.manufacture.label', default: 'Manufacture')}" params="[q:q,location:dataLocation.id]" />
			<g:sortableColumn property="supplier" defaultOrder="asc" title="${message(code: 'provider.supplier.label', default: 'Supplier')}" params="[q:q,location:dataLocation.id]" />
			<th><g:message code="location.label"/></th>
			<th><g:message code="equipment.status.label"/></th>
			<g:sortableColumn property="obsolete" defaultOrder="asc" title="${message(code: 'equipment.obsolete.label', default: 'Obsolete')}" params="[q:q,location:dataLocation.id]" />
			<g:sortableColumn property="donation" defaultOrder="asc" title="${message(code: 'equipment.donation.label', default: 'Donation')}" params="[q:q,location:dataLocation.id]" />
		</tr>
	</thead>

	<tbody>
		<g:each in="${entities}" status="i" var="equipment">
			<tr >
				<td>
					<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'equipment', action:'edit', params:[id: equipment.id,location: equipment.dataLocation.id])}"  class="edit-button">
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
				<td>${equipment.serialNumber}</td>
				<td>${equipment.type.names}</td>
				<td>${equipment.model}</td>
				<td>${equipment.manufacture.contact.contactName}</td>
				<td>${equipment.supplier.contact.contactName}</td>
				<td>
					<g:message code="datalocation.label"/>: ${equipment.dataLocation.names}<br/>
					<g:message code="department.label"/>: ${equipment.department.names}<br/>
					<g:message code="equipment.room.label"/>: ${equipment.room}<br/>
				</td>
				<td>${message(code: equipment.getCurrentStatus()?.status?.messageCode+'.'+equipment.getCurrentStatus()?.status?.name)}</td>
				<td>${(equipment.obsolete)? '\u2713':'X'}</td>
				<td>${(equipment.donation)? '\u2713':'X'}</td>
			</tr>
		</g:each>
	</tbody>
	
</table>