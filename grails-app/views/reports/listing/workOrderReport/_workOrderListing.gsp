<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus" %>
<table class="items">
	<thead>
		<tr>
			<th><g:message code="default.equipment.code.label"/></th>
			<th><g:message code="default.equipment.serial.number.label"/></th>
			<th><g:message code="default.equipment.type.label"/></th>
			<g:sortableColumn property="currentStatus" title="${message(code: 'entity.status.label')}" params="[q:q]" />
			<g:sortableColumn property="criticality"  title="${message(code: 'work.order.criticality.label')}" params="[q:q]" />
			<g:sortableColumn property="openOn"  title="${message(code: 'order.open.on.label')}" params="[q:q]" />
			<g:sortableColumn property="closedOn"  title="${message(code: 'order.closed.on.label')}" params="[q:q]" />
			<th><g:message code="work.order.description.label"/></th>
			<th><g:message code="entity.messages.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="order">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				
				<td>${order.equipment.code}</td>
				<td>${order.equipment.serialNumber}</td>
				<td>${order.equipment.type.names}</td>
				<td>${message(code: order.currentStatus?.messageCode+'.'+order.currentStatus?.name)}</td>
				<td>${order.criticality}</td>
				<td>${Utils.formatDateWithTime(order.openOn)}</td>
				<td>${Utils.formatDateWithTime(order.closedOn)}</td>
				<td><g:stripHtml field="${order.description}" chars="30"/></td>
				<td>${order.getUnReadNotificationsForUser(User.findByUuid(SecurityUtils.subject.principal, [cache: true])).size()}</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />