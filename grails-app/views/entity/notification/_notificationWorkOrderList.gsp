<%@ page import="org.chai.memms.util.Utils" %>
<table class="items">
	<thead>
		<tr>
			<g:sortableColumn property="sender"  title="${message(code: 'notification.sender.label')}" params="[q:q,'workOrder.id':workOrder?.id]" />
			<g:sortableColumn property="dateCreated"  title="${message(code: 'notification.writtenOn.label')}" params="[q:q,'workOrder.id':workOrder?.id]" />
			<th><g:message code="notification.content.label"/></th>
			<th/>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="notification">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					${notification?.sender?.names}
				</td>
				<td>
					${Utils.formatDateWithTime(notification.dateCreated)}
				</td>
				<td><g:stripHtml field="${notification?.content}" chars="30"/></td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'notificationWorkOrder', action:'read', params:[id: notification.id])}">
							<g:message code="notification.read.label" />
						</a>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>