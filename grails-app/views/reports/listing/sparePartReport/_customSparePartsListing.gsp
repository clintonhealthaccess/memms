<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<table class="items">
	<thead>
		<tr>
			<g:if test="${reportTypeOptions.contains('sparePartType')}">
				<th>Spare Part Type</th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('locationOfStock')}">
				<th>Location of Stock</th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('quantityInStock')}">
				<th>Quantity in Stock</th>
			</g:if>
			<g:if test="${reportSubType == ReportSubType.STATUSCHANGES && reportTypeOptions.contains('statusChanges')}">
				%{-- TODO SL --}%
			</g:if>
			<g:if test="${reportTypeOptions.contains('status')}">
				<th>Status</th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('model')}">
				<g:sortableColumn property="model"  title="${message(code: 'equipment.model.label')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('manufacturer')}">
				<g:sortableColumn property="manufacturer"  title="${message(code: 'provider.type.manufacturer')}" params="[q:q]" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('equipmentCode')}">
				<th>Equipment Code Associated With Spare Part</th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('cost')}">
				<th>Cost</th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('warrantyPeriodRemaining')}">
				<th>Warranty Period Remaining</th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('discontinuedDate')}">
				<th>Discontinued Date</th>
			</g:if>
			<g:if test="${reportSubType == ReportSubType.STOCKOUT && reportTypeOptions.contains('forecastedStockOut')}">
			    <th>Forecasted Stock Out</th>
			</g:if>
			<g:if test="${reportSubType == ReportSubType.USERATE && reportTypeOptions.contains('useRate')}">
			    <th>Use Rate</th>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="order">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				%{-- TODO --}%
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" />