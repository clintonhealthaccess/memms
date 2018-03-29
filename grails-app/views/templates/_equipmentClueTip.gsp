<span class="label"><g:message code="equipment.code.label" />:</span>
<span class="text">${equipment.code}</span><br/>
<span class="label"><g:message code="equipment.type.label" />:</span>
<span class="text">${equipment.type.names}</span><br/>
<span class="label"><g:message code="equipment.serial.number.label" />:</span>
<span class="text">${equipment.serialNumber}</span><br/>
<span class="label"><g:message code="equipment.type.code.label" />:</span>
<span class="text">${equipment.type.code}</span><br/>
<span class="label"><g:message code="datalocation.label" />:</span>
<span class="text">${equipment.dataLocation.names}</span><br/>
<span class="label"><g:message code="department.label" />:</span>
<span class="text">${equipment.department.names}</span><br/>
<span class="label"><g:message code="equipment.room.label" />:</span>
<span class="text">${equipment.room}</span><br/>
<span class="label"><g:message code="equipment.status.label" />:</span>
<span class="text">${message(code: equipment.currentStatus?.messageCode+'.'+equipment.currentStatus?.name)}</span>

<a href="${createLinkWithTargetURI(controller:'equipment', action:'edit', params:[id: equipment.id])}" >
	<g:message code="default.edit.label" args="[message(code:'equipment.label')]" />
</a>