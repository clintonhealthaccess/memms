
<%@ page import="org.chai.memms.util.Utils" %>
<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="eventDate"  title="${message(code: 'prevention.event.date.label')}" params="[q:q,'order.id':order?.id]" />
			<g:sortableColumn property="scheduledOn"  title="${message(code: 'prevention.scheduled.on.label')}" params="[q:q,'order.id':order?.id]" />
			<g:sortableColumn property="timeSpend"  title="${message(code: 'prevention.time.spend.label')}" params="[q:q,'order.id':order?.id]" />
			<th><g:message code="entity.descriptions.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="prevention">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<shiro:hasPermission permission="prevention:edit">
								<a href="${createLinkWithTargetURI(controller:'prevention', action:'edit', params:[id: prevention.id])}" class="edit-button"> 
								<g:message code="default.link.edit.label" />
								</a>
							</shiro:hasPermission>
						</li>
						<li>
							<shiro:hasPermission permission="prevention:delete">
								<a href="${createLinkWithTargetURI(controller:'prevention', action:'delete', 
								params:[id: prevention.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button">
								<g:message code="default.link.delete.label" />
								</a>
							</shiro:hasPermission>
						</li>	
					</ul>
				</td>
				<td>
					${Utils.formatDate(prevention.eventDate)}
				</td>
				<td>
					${prevention.scheduledOn.formatDate}
				</td>
				<td>
					 ${prevention.timeSpend.hours ?prevention.timeSpend.hours:0}:${prevention.timeSpend.minutes}
				</td>
				<td>
					<g:stripHtml field="${prevention.descriptions}" chars="40"/>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />
<script type="text/javascript">
	$(document).ready(function() {
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
		showClutips()
	});
</script>
