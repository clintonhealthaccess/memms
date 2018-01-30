<%@ page import="org.chai.memms.util.Utils" %>
<div id="form-aside-${field}-${sparePart.id}" class="${(cssClass)?:cssClass}">	
	<ul>	
		<li>
			<h6>${message(code:"spare.part.type.label")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="entity.code.label" /></span>
					<span class="text">${sparePart.type?.code}</span>
					<span class="label"><g:message code="entity.name.label" /></span>
					<span class="text">${sparePart.type?.names}</span>
					<span class="label"><g:message code="entity.part.number.label" /></span>
					<span class="text">${sparePart.type?.partNumber}</span>
					<span class="label"><g:message code="entity.model.label" /></span>
					<span class="text">${sparePart.type?.model}</span>
				</li>
			</ul>
		</li>
		<li>
			<h6>${message(code:"spare.part.status.current.status")}</h6>
			<ul>
				<li>
					<span class="label"><g:message code="spare.part.status.label"/>:</span>
					<span class="text">${message(code: sparePart.status?.messageCode+'.'+sparePart.status?.name)}</span>
				</li>
			</ul>
		</li>
		
		<li>
			<h6>${message(code:"spare.part.type.manufacturer.label")}</h6>
			<ul>
				<li>
					<ul class="half">
						<li><span class="label"><g:message code="entity.name.label"/>:</span><span class="text">${sparePart.type?.manufacturer?.contact?.contactName}</span></li>
						<li><span class="label"><g:message code="contact.email.label"/>:</span><span class="text">${sparePart.type?.manufacturer?.contact?.email}</span></li>
					</ul>
					<ul class="half">
						<li><span class="label"><g:message code="contact.address.label"/>:</span></li>
						<li><span class="label"><g:message code="contact.phone.label"/>:</span><span class="text">${sparePart.type?.manufacturer?.contact?.phone}</span></li>
						<li><span class="label"><g:message code="contact.pobox.label"/>:</span><span class="text">${sparePart.type?.manufacturer?.contact?.poBox}</span></li>
					</ul>
				</li>
			</ul>
		</li>
	
	</ul>
</div>
