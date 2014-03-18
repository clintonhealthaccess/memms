<%@ page import="org.chai.memms.util.Utils" %>
<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:q]" title="${message(code: 'entity.code.label')}" />
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:q]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="dateCreated" params="[q:q]" title="${message(code: 'hmis.report.dateCreated.label')}" />
			<th><g:message code="number.of.hmis.facility.reports.label"/></th>
			<th/>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="type">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<shiro:hasPermission permission="hmisReport:edit">
								<a href="${createLinkWithTargetURI(controller:'hmisReport', action:'edit', params:[id: type.id])}" class="edit-button"> 
									<g:message code="default.link.edit.label" />
								</a>
							</shiro:hasPermission>
						</li>
					</ul>
				</td>	
				<td>
					${type.periodCode}
				</td>
				<td>
					${type.names}
				</td>
				<td>
					${Utils.formatDate(type.dateCreated)}
				</td>
				<td>
					${type.hmisFacilityReports.size()}
				</td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'hmisReport', action:'export', params:['id': type.id])}" class="next gray medium"> 
						<g:message code="generate.report" />
					</a>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />
