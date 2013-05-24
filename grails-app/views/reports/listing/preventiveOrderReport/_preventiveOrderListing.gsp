<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType" %>
<%@ page import="org.chai.memms.preventive.maintenance.DurationBasedOrder" %>
<table class="items">
	<thead>
		<tr>
			<th><g:message code="default.equipment.code.label"/></th>
			<th><g:message code="equipment.serial.number.label"/></th>
			<th><g:message code="default.equipment.type.label"/></th>
			<g:sortableColumn property="${i18nField(field: 'names')}" title="${message(code: 'entity.names.label')}" params="[q:q]" />
			<g:sortableColumn property="type"  title="${message(code: 'entity.type.label')}" params="[q:q]" />
			<th><g:message code="preventive.occurance.label"/></th>
			<th><g:message code="preventive.occurance.interval.label"/></th>
			<g:sortableColumn property="status"  title="${message(code: 'entity.status.label')}" params="[q:q]" />
			<g:sortableColumn property="openOn"  title="${message(code: 'preventive.order.open.on.label')}" params="[q:q]" />
			<th><g:message code="preventive.next.occurence.label"/></th>
			<th><g:message code="entity.descriptions.label"/></th>
			<th><g:message code="prevention.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="order">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${order.equipment.code}</td>
				<td>${order.equipment.serialNumber}</td>
				<td>${order.equipment.type.names}</td>
				<td>${order.names}</td>
				<td>${message(code: order.type?.messageCode+'.'+order.type?.name)}</td>
				<td>${message(code: order.occurency?.messageCode+'.'+order.occurency?.name)}</td>
				<td>${order.occurInterval}</td>
				<td>${message(code: order.status?.messageCode+'.'+order.status?.name)}</td>
				<td>${Utils.formatDateWithTime(order.firstOccurenceOn?.timeDate)}</td>
				<td>${(order.type.equals(PreventiveOrderType.DURATIONBASED))?Utils.formatDateWithTime(order.nextOccurence):''}</td>
				
				<td><g:stripHtml field="${order.description}" chars="30"/></td>
				<td>${order.preventions?.size()}</td>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />