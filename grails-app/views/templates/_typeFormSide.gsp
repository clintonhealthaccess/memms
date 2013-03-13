<%@ page import="org.chai.memms.util.Utils" %>
<div id="form-aside-${field}-${type.id}" class="${(cssClass)?:cssClass}">
	<h5><g:message code="equipment.type.details.label" /></h5>
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
</div>
