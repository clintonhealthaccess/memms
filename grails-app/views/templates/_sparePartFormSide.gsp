<%@ page import="org.chai.memms.util.Utils" %>
<div id="form-aside-${field}-${sparePart.id}" class="${(cssClass)?:cssClass}">
	<a href="${createLinkWithTargetURI(controller:'sparePart', action:'edit', params:[id: sparePart.id])}" class="next small gray right pulled">
		<g:message code="default.edit.label" args="[message(code:'spare.part.label')]" />
	</a>
	<h5><g:message code="spare.part.details.label" /></h5>
	
	<ul>
		<li>
			<h6>${message(code:"spare.part.section.basic.information.label")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="spare.part.code.label" /></span>
					<span class="text">${sparePart.code}</span>
					<span class="label"><g:message code="spare.part.serial.number.label" /></span>
					<span class="text">${sparePart.serialNumber}</span>
					<span class="label"><g:message code="spare.part.model.label" /></span>
					<span class="text">${sparePart.model}</span>
					<span class="label"><g:message code="stocklocation.label"/>:</span>
					<span class="text">${sparePart.stockLocation?.name}</span>
					<span class="label"><g:message code="datalocation.label"/>:</span>
					<span class="text">${sparePart.dataLocation?.names}</span>
					<span class="label"><g:message code="entity.in.system.since.label"/>:</span>
					<span class="text"> ${Utils.formatDate(sparePart.dateCreated)}</span>
				</li>
			</ul>
		</li>
		<li>
			<h6>${message(code:"spare.part.type.label")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="entity.code.label" /></span>
					<span class="text">${sparePart.type?.code}</span>
					<span class="label"><g:message code="entity.name.label" /></span>
					<span class="text">${sparePart.type?.names}</span>
				</li>
			</ul>
		</li>
		
		<li>
			<h6>${message(code:"provider.type.supplier")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="entity.name.label"/>:</span>
					<span class="text">${sparePart.supplier?.contact?.contactName}</span>
					<span class="label"><g:message code="contact.email.label"/>:</span>
					<span class="text">${sparePart.supplier?.contact?.email}</span>
					<span class="label"><g:message code="contact.phone.label"/>:</span>
					<span class="text">${sparePart.supplier?.contact?.phone}</span>
				</li>
			</ul>
		</li>
		<li>
			<h6>${message(code:"spare.part.status.current.status")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="spare.part.status.label"/>:</span>
					<span class="text">${message(code: sparePart.statusOfSparePart?.messageCode+'.'+sparePart.statusOfSparePart?.name)}</span>
					<span class="label"><g:message code="spare.part.status.date.of.event.label"/>:</span>
					<span class="text">${Utils.formatDate(sparePart.timeBasedStatus?.dateOfEvent)}</span>
				</li>
			</ul>
		</li>
	
	</ul>
</div>
