<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.chai.memms.util.Utils" %>
<table class="items spaced">
	<thead>
		<tr>
			<g:if test="${reportTypeOptions.contains('location')}">
				<th><g:message code="location.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('code')}">
				<th><g:message code="equipment.code.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('serialNumber')}">
				<th><g:message code="equipment.serial.number.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('equipmentType')}">
				<th><g:message code="equipment.type.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('model')}">
				<th><g:message code="equipment.model.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('previousStatus')}">
				<g:sortableColumn property="previousStatus"  title="${message(code: 'equipment.previous.status.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('currentStatus')}">
				<g:sortableColumn property="status"  title="${message(code: 'equipment.status.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('statusChanges')}">
				<th><g:message code="listing.report.equipment.status.changes.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('obsolete')}">
				<th><g:message code="equipment.obsolete.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('manufacturer')}">
				<th><g:message code="provider.type.manufacturer"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('supplier')}">
				<th><g:message code="provider.type.supplier"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('purchaser')}">
				<th><g:message code="equipment.purchaser.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('acquisitionDate')}">
				<th><g:message code="equipment.purchase.date.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('cost')}">
				<th><g:message code="equipment.purchase.cost.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('warrantyProvider')}">
				<th><g:message code="listing.report.equipment.warranty.provider.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('warrantyPeriodRemaining')}">
				<th><g:message code="listing.report.equipment.warranty.period.remaining.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('currentValue')}">
			 	<th><g:message code="equipment.current.value.label"/></th>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="equipmentStatus">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<g:if test="${reportTypeOptions.contains('location')}">
					<td>${equipmentStatus.equipment.dataLocation.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('code')}">
					<td>${equipmentStatus.equipment.code}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('serialNumber')}">
					<td>${equipmentStatus.equipment.serialNumber}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('equipmentType')}">
					<td>${equipmentStatus.equipment.type.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('model')}">
					<td>${equipmentStatus.equipment.model}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('previousStatus')}">
					<td>${message(code: equipmentStatus?.previousStatus?.messageCode+'.'+equipmentStatus?.previousStatus?.name)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('currentStatus')}">
					<td>${message(code: equipmentStatus?.status?.messageCode+'.'+equipmentStatus?.status?.name)}</td>
				</g:if> 
				<g:if test="${reportTypeOptions.contains('statusChanges')}">
					<g:set var="statusChangesEnum" value="${equipmentStatus.getEquipmentStatusChange(customizedListingParams?.statusChanges)}"/>
					<td>${message(code: statusChangesEnum?.messageCode+'.'+statusChangesEnum?.name)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('obsolete')}">
					<td>
						<g:if test="${(!equipmentStatus.equipment.obsolete==true)}">&radic;</g:if>
						<g:else>&nbsp;</g:else>
					</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('manufacturer')}">
					<td>${equipmentStatus.equipment.manufacturer?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('supplier')}">
					<td>${equipmentStatus.equipment.supplier?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('purchaser')}">
					<td>${equipmentStatus.equipment.purchaser?.name}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('acquisitionDate')}">
					<td>${Utils.formatDate(equipmentStatus.equipment.purchaseDate)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('cost')}">
					<td>${equipmentStatus.equipment.purchaseCost}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('warrantyProvider')}">
					<td>${equipmentStatus.equipment.warranty?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('warrantyPeriodRemaining')}">
					<td>${equipmentStatus.equipment.warrantyPeriodRemaining} <g:message code="listing.report.equipment.warranty.in.months.label" /></td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('currentValue')}">
					<td><g:formatNumber number="${equipmentStatus.equipment.currentValueOfThisEquipment}" type="number" format="###.#" maxFractionDigits="0"/> ${equipmentStatus.equipment.currency}</td>
				</g:if>
			</tr>
		</g:each>

	</tbody>	
</table>
<g:render template="/templates/pagination" />