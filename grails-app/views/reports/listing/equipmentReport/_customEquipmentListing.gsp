<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<table class="items spaced">
	<thead>
		<tr>
			<g:if test="${reportTypeOptions.contains('location')}">
				<g:sortableColumn property="dataLocation"  title="${message(code: 'location.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('code')}">
				<g:sortableColumn property="code"  title="${message(code: 'equipment.code.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('serialNumber')}">
				<g:sortableColumn property="serialNumber"  title="${message(code: 'equipment.serial.number.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('equipmentType')}">
				<g:sortableColumn property="type"  title="${message(code: 'equipment.type.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('model')}">
				<g:sortableColumn property="model"  title="${message(code: 'equipment.model.label')}" params="[q:q]" />
			</g:if>
			
			<g:if test="${reportSubType == ReportSubType.STATUSCHANGES && reportTypeOptions.contains('statusChanges')}">
				%{-- TODO SL --}%
			</g:if>

			<g:if test="${reportTypeOptions.contains('currentStatus')}">
				<g:sortableColumn property="currentStatus"  title="${message(code: 'equipment.status.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('obsolete')}">
				<g:sortableColumn property="obsolete"  title="${message(code: 'equipment.obsolete.label')}" params="[q:q]" />
			</g:if>

			<g:if test="${reportTypeOptions.contains('manufacturer')}">
				<g:sortableColumn property="manufacturer"  title="${message(code: 'provider.type.manufacturer')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('supplier')}">
				<g:sortableColumn property="supplier"  title="${message(code: 'provider.type.supplier')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('purchaser')}">
				<g:sortableColumn property="purchaser"  title="${message(code: 'equipment.purchaser.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('acquisitionDate')}">
				%{-- TODO --}%
			</g:if>
			<g:if test="${reportTypeOptions.contains('cost')}">
				%{-- TODO --}%
			</g:if>
			<g:if test="${reportTypeOptions.contains('warrantyProvider')}">
				%{-- TODO --}%
			</g:if>
			<g:if test="${reportTypeOptions.contains('warrantyPeriodRemaining')}">
				%{-- TODO --}%
			</g:if>
			<g:if test="${reportTypeOptions.contains('currentValue')}">
				%{-- TODO --}%
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="equipment">
			<tr>
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
				
				<g:if test="${reportSubType == ReportSubType.STATUSCHANGES && reportTypeOptions.contains('statusChanges')}">
					%{-- TODO --}%
				</g:if>

				<g:if test="${reportTypeOptions.contains('currentStatus')}">
					<td>${equipment.currentStatus?.name}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('obsolete')}">
					<td>
						<g:if name="obsolete" id="${equipment.id}" test="${(!equipment.obsolete==true)}">&radic;</g:if>
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
					<td>${message(code: equipment.purchaser?.messageCode+'.'+equipment.purchaser?.name)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('acquisitionDate')}">
					%{-- TODO --}%
				</g:if>
				<g:if test="${reportTypeOptions.contains('cost')}">
					%{-- TODO --}%
				</g:if>
				<g:if test="${reportTypeOptions.contains('warrantyProvider')}">
					%{-- TODO --}%
				</g:if>
				<g:if test="${reportTypeOptions.contains('warrantyPeriodRemaining')}">
					%{-- TODO --}%
				</g:if>
				<g:if test="${reportTypeOptions.contains('currentValue')}">
					%{-- TODO --}%
				</g:if>
			</tr>
		</g:each>

	</tbody>	
</table>
<g:render template="/templates/pagination" />