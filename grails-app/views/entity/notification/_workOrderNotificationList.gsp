<%@ page import="org.chai.memms.util.Utils" %>
<table class="items">
	<thead>
		<tr>
			<th><g:message code="notification.sender.label"/></th>
			<th><g:message code="notification.writtenOn.label"/></th>
			<th><g:message code="notification.content.label"/></th>
			<th/>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="notification">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					${notification?.sender?.firstname +" "+ notification?.sender?.lastname}
				</td>
				<td>
					${Utils.formatDateWithTime(notification.writtenOn)}
				</td>
				<td><g:stripHtml field="${notification?.content}" chars="30"/></td>
				<td>
					<g:if test="${!notification.read}">
						<a href="${createLinkWithTargetURI(controller:'notification', action:'read', params:[id: notification.id])}">
							<g:message code="notification.read.label" />
						</a>
					</g:if>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>