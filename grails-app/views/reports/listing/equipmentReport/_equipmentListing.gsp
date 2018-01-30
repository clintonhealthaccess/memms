<table class="items spaced">
	<thead>
		<tr>
			<g:sortableColumn property="dataLocation"  title="${message(code: 'location.label')}" params="[q:q]" />
			<g:sortableColumn property="code"  title="${message(code: 'equipment.code.label')}" params="[q:q]" />
			<g:sortableColumn property="serialNumber"  title="${message(code: 'equipment.serial.number.label')}" params="[q:q]" />
			<g:sortableColumn property="type"  title="${message(code: 'equipment.type.label')}" params="[q:q]" />
			<g:sortableColumn property="model"  title="${message(code: 'equipment.model.label')}" params="[q:q]" />
			<g:sortableColumn property="currentStatus"  title="${message(code: 'equipment.status.label')}" params="[q:q]" />
			<g:sortableColumn property="manufacturer"  title="${message(code: 'provider.type.manufacturer')}" params="[q:q]" />
			<g:sortableColumn property="supplier"  title="${message(code: 'provider.type.supplier')}" params="[q:q]" />
			<g:sortableColumn property="purchaser"  title="${message(code: 'equipment.purchaser.label')}" params="[q:q]" />
			<th><g:message code="equipment.current.value.label"/></th>
		</tr>
	</thead>
	<tbody>
	 
		<g:each in="${entities}" status="i" var="equipment">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${equipment.dataLocation.names}</td>
				<td>${equipment.code}</td>
				<td>${equipment.serialNumber}</td>
				<td>${equipment.type.names}</td>
				<td>${equipment.model}</td>
				<td>${message(code: equipment.currentStatus?.messageCode+'.'+equipment.currentStatus?.name)}</td>
				<td>${equipment.manufacturer?.contact?.contactName}</td>
				<td>${equipment.supplier?.contact?.contactName}</td>
				<td>${message(code: equipment.purchaser?.messageCode+'.'+equipment.purchaser?.name)}</td>
				<td><g:formatNumber number="${equipment.currentValueOfThisEquipment}" type="number" format="###.#" maxFractionDigits="0"/> ${equipment.currency} </td>
			</tr>
		</g:each>

	</tbody>	
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />
