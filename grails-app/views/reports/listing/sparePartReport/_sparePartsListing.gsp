<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<table class="items">
	<thead>
		<tr>
			<th>Spare Part Type</th>
			<th>Location of Stock</th>
			<th>Quantity in Stock</th>
			<th>Status</th>
			<g:sortableColumn property="model"  title="${message(code: 'equipment.model.label')}" params="[q:q]" />
			<g:sortableColumn property="manufacturer"  title="${message(code: 'provider.type.manufacturer')}" params="[q:q]" />
			<th>Equipment Code Associated With Spare Part</th>
			<th>Cost</th>
			<th>Warranty Period Remaining</th>
			<th>Discontinued Date</th>
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