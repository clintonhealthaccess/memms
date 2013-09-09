<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.chai.memms.util.Utils" %>
<table class="items spaced">
	<thead>
		<tr>
			<g:if test="${reportTypeOptions.contains('location')}">
				<g:sortableColumn property="dataLocation"  title="${message(code: 'location.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('code')}">
				<g:sortableColumn property="code"  title="${message(code: 'equipment.code.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('serialNumber')}">
				<g:sortableColumn property="serialNumber"  title="${message(code: 'equipment.serial.number.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('equipmentType')}">
				<g:sortableColumn property="type"  title="${message(code: 'equipment.type.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('model')}">
				<g:sortableColumn property="model"  title="${message(code: 'equipment.model.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('currentStatus')}">
				<g:sortableColumn property="currentStatus"  title="${message(code: 'equipment.status.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('obsolete')}">
				<g:sortableColumn property="obsolete"  title="${message(code: 'equipment.obsolete.label')}" params="${params}" />
			</g:if>

			<g:if test="${reportTypeOptions.contains('manufacturer')}">
				<g:sortableColumn property="manufacturer"  title="${message(code: 'provider.type.manufacturer')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('supplier')}">
				<g:sortableColumn property="supplier"  title="${message(code: 'provider.type.supplier')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('purchaser')}">
				<g:sortableColumn property="purchaser"  title="${message(code: 'equipment.purchaser.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('acquisitionDate')}">
				<g:sortableColumn property="purchaseDate"  title="${message(code: 'equipment.purchase.date.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('cost')}">
				<g:sortableColumn property="purchaseCost"  title="${message(code: 'equipment.purchase.cost.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('warrantyProvider')}">
				<g:sortableColumn property="warranty"  title="${message(code: 'listing.report.equipment.warranty.provider.label')}" params="${params}" />
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
		<g:each in="${entities}" status="i" var="equipment">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<g:if test="${reportTypeOptions.contains('location')}">
					<td>${equipment.dataLocation.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('code')}">
					<td>${equipment.code}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('serialNumber')}">
					<td>${equipment.serialNumber}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('equipmentType')}">
					<td>${equipment.type.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('model')}">
					<td>${equipment.model}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('currentStatus')}">
					<td>${message(code: equipment.currentStatus?.messageCode+'.'+equipment.currentStatus?.name)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('obsolete')}">
					<td>
						<g:if test="${(!equipment.obsolete==true)}">&radic;</g:if>
						<g:else>&nbsp;</g:else>
					</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('manufacturer')}">
					<td>${equipment.manufacturer?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('supplier')}">
					<td>${equipment.supplier?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('purchaser')}">
					<td>${equipment.purchaser?.name}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('acquisitionDate')}">
					<td>${Utils.formatDate(equipment.purchaseDate)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('cost')}">
					<td>${equipment.purchaseCost} ${equipment.currency}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('warrantyProvider')}">
					<td>${equipment.warranty?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('warrantyPeriodRemaining')}">
					<td>${equipment.warrantyPeriodRemaining} <g:message code="listing.report.equipment.warranty.in.months.label" /></td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('currentValue')}">
					<td><g:formatNumber number="${equipment.currentValueOfThisEquipment}" type="number" format="###.#" maxFractionDigits="0"/> ${equipment.currency}</td>
				</g:if>
			</tr>
		</g:each>

	</tbody>	
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />
