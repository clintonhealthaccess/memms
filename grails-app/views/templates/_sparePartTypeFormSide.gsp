<%@ page import="org.chai.memms.util.Utils" %>
<div id="form-aside-${field}-${type.id}" class="${(cssClass)?:cssClass}">
	<h5><g:message code="spare.part.type.details.label" /></h5>
	<ul class="half">
		<li>
			<span class="label"><g:message code="entity.code.label" /></span>
			<span class="text">${type?.code}</span>
		</li>
		<li>
			<span class="label"><g:message code="entity.name.label" /></span>
			<span class="text">${type?.names}</span>
		</li>
	</ul>
	<ul class="half">
		<li>
			<span class="label"><g:message code="spare.part.type.added.on.label" /></span>
			<span class="text">${Utils.formatDate(type?.dateCreated)}</span>
		</li>
		<li>
			<span class="label"><g:message code="spare.part.type.last.modified.on.label" /></span>
			<span class="text">${Utils.formatDate(type?.lastUpdated)}</span>
		</li>
	</ul>
	<ul>
		<li>
			<h6>${message(code:"spare.part.type.manufacturer.label")}</h6>
			<ul class="half">
				<li><span class="label"><g:message code="entity.code.label"/>:</span><span class="text">${type?.manufacturer?.code}</span></li>
				<li><span class="label"><g:message code="entity.name.label"/>:</span><span class="text">${type?.manufacturer?.contact?.contactName}</span></li>
				<li><span class="label"><g:message code="contact.email.label"/>:</span><span class="text">${type?.manufacturer?.contact?.email}</span></li>
			</ul>
			<ul class="half">
				<li><span class="label"><g:message code="contact.address.label"/>:</span></li>
				<li><span class="label"><g:message code="contact.phone.label"/>:</span><span class="text">${type?.manufacturer?.contact?.phone}</span></li>
				<li><span class="label"><g:message code="contact.pobox.label"/>:</span><span class="text">${type?.manufacturer?.contact?.poBox}</span></li>
			</ul>
		</li>
	</ul>
</div>
