<%@ page import="org.chai.memms.util.Utils" %>
<div id="form-aside-${field}-${equipment.id}" class="${(cssClass)?:cssClass}">
	<a href="${createLinkWithTargetURI(controller:'equipment', action:'edit', params:[id: equipment.id])}" class="next small gray right pulled">
		<g:message code="default.edit.label" args="[message(code:'equipment.label')]" />
	</a>
	<h5><g:message code="equipment.details.label" /></h5>
	
	<ul>
		<li>
			<h6>${message(code:"equipment.section.basic.information.label")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="equipment.code.label" /></span>
					<span class="text">${equipment.code}</span>
					<span class="label"><g:message code="equipment.serial.number.label" /></span>
					<span class="text">${equipment.serialNumber}</span>
					<span class="label"><g:message code="equipment.model.label" /></span>
					<span class="text">${equipment.model}</span>
					<span class="label"><g:message code="datalocation.label"/>:</span>
					<span class="text">${equipment.dataLocation.names}</span>
					<span class="label"><g:message code="department.label"/>:</span>
					<span class="text">${equipment.department.names}</span>
					<span class="label"><g:message code="equipment.room.label"/>:</span>
					<span class="text"> ${equipment.room}</span>
				</li>
			</ul>
		</li>
		<li>
			<h6>${message(code:"equipment.type.label")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="entity.code.label" /></span>
					<span class="text">${equipment.type?.code}</span>
					<span class="label"><g:message code="entity.name.label" /></span>
					<span class="text">${equipment.type?.names}</span>
				</li>
			</ul>
		</li>
		<li>
			<h6>${message(code:"provider.type.manufacturer")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="entity.name.label"/>:</span>
					<span class="text">${equipment.manufacturer?.contact?.contactName}</span>
					<span class="label"><g:message code="contact.email.label"/>:</span>
					<span class="text">${equipment.manufacturer?.contact?.email}</span>
					<span class="label"><g:message code="contact.phone.label"/>:</span>
					<span class="text">${equipment.manufacturer?.contact?.phone}</span>
				</li>
			</ul>
		</li>
		<li>
			<h6>${message(code:"provider.type.supplier")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="entity.name.label"/>:</span>
					<span class="text">${equipment.supplier?.contact?.contactName}</span>
					<span class="label"><g:message code="contact.email.label"/>:</span>
					<span class="text">${equipment.supplier?.contact?.email}</span>
					<span class="label"><g:message code="contact.phone.label"/>:</span>
					<span class="text">${equipment.supplier?.contact?.phone}</span>
				</li>
			</ul>
		</li>
		<li>
			<h6>${message(code:"equipment.status.current.status")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="equipment.status.label"/>:</span>
					<span class="text">${message(code: equipment.currentState?.status?.messageCode+'.'+equipment.currentState?.status?.name)}</span>
					<span class="label"><g:message code="equipment.status.date.of.event.label"/>:</span>
					<span class="text">${Utils.formatDate(equipment.currentState?.dateOfEvent)}</span>
				</li>
			</ul>
		</li>
	
	</ul>
</div>
