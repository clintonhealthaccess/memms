<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" defaultOrder="asc" title="${message(code: 'entity.code.label')}" params="[q:q]" />
			<g:sortableColumn property="${names}" defaultOrder="asc" title="${message(code: 'entity.name.label')}"  params="[q:q]" />
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="locationLevel">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'locationLevel', action:'edit', params:[id: locationLevel.id])}" class="edit-button">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:'locationLevel', action:'delete', params:[id: locationLevel.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>
					${locationLevel.code}
				</td>
				<td>
					${locationLevel.names}
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />