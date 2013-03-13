<%@ page import="org.chai.memms.util.Utils" %>
<div id="form-aside-${field}-${dataLocation.id}" class="${(cssClass)?:cssClass}">
	<h5><g:message code="spare.part.data.location.label" /></h5>
	<ul>
		<li>
			<span class="label"><g:message code="entity.name.label" /></span>				
			<span class="text">${dataLocation?.names}</span>
			<span class="label"><g:message code="entity.type.label" /></span>
			<span class="text">${dataLocation?.type?.names}</span>
			<span class="label"><g:message code="spare.part.location.label" /></span>
			<span class="text">${dataLocation?.location?.names}</span>
		</li>
		
	</ul>
</div>