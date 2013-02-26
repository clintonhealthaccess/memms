<%@ page import="org.chai.memms.util.Utils" %>
<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:q]" title="${message(code: 'entity.code.label')}" />
			<g:sortableColumn property="partNumber" params="[q:q]" title="${message(code: 'entity.part.number.label')}" />
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:q]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="manufacturer" params="[q:q]" title="${message(code: 'entity.manufacturer.label')}" />
			<g:sortableColumn property="discontinuedDate" params="[q:q]" title="${message(code: 'entity.discontinued.date.label')}" />
	        <g:sortableColumn property="${i18nField(field: 'descriptions')}" params="[q:q]" title="${message(code: 'entity.description.label')}" />
			<g:sortableColumn property="dateCreated" params="[q:q]" title="${message(code: 'spare.part.type.added.on.label')}" />
			<g:sortableColumn property="lastUpdated" params="[q:q]" title="${message(code:'spare.part.type.last.modified.on.label')}" />
       </tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="type">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'sparePartType', action:'edit', params:[id: type.id])}" class="edit-button">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:'sparePartType', action:'delete', params:[id: type.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>
					${type.code}
				</td>
				<td>
					${type.partNumber}
				</td>
				<td>
					${type.names}
				</td>
				<td>
					${type.manufacturer.contact.contactName}
				</td>
				<td>
					${Utils.formatDateWithTime(type?.discontinuedDate)}
				</td>
				<td>
					${type.descriptions}
				</td>
				    
				<td>
					${Utils.formatDateWithTime(type?.dateCreated)}
				</td>
				<td>
					${Utils.formatDateWithTime(type?.lastUpdated)}
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />
