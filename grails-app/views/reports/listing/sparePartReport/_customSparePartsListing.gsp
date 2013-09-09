<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.memms.security.User" %>
<table class="items">
	<thead>
		<tr>
			<g:sortableColumn property="dataLocation"  title="${message(code: 'location.label')}" params="${params}"/>
			<g:if test="${reportTypeOptions.contains('sparePartType')}">
				<g:sortableColumn property="type"  title="${message(code: 'listing.report.spare.part.type.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('locationOfStock')}">
				<g:sortableColumn property="stockLocation"  title="${message(code: 'spare.part.stockLocation.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('purchaseDate')}">
				<g:sortableColumn property="purchaseDate"  title="${message(code: 'listing.report.spare.part.purchase.date.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('deliveryDate')}">
				<g:sortableColumn property="deliveryDate"  title="${message(code: 'listing.report.spare.part.delivery.date.label')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('currentStatus')}">
				<g:sortableColumn property="status"  title="${message(code: 'listing.report.spare.part.current.status.label')}" params="${params}" />
			</g:if>

			<g:if test="${reportSubType == ReportSubType.STATUSCHANGES && reportTypeOptions.contains('statusChanges')}">
				<th><g:message code="reports.statusChange"/></th>
			</g:if>

			<g:if test="${reportTypeOptions.contains('manufacturer')}">
				<th><g:message code="provider.type.manufacturer"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('supplier')}">
				<th><g:message code="listing.report.spare.part.supplier.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('purchaser')}">
				<th><g:message code="listing.report.spare.part.purchaser.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('cost')}">
				<th><g:message code="listing.report.spare.part.cost.label"/></th>
			</g:if>
			<g:if test="${reportTypeOptions.contains('discontinuedDate')}">
				<th><g:message code="listing.report.spare.part.discontinued.date.label"/></th>
			</g:if>

			<g:if test="${reportTypeOptions.contains('initialQuantity')}">
				<g:sortableColumn property="initialQuantity"  title="${message(code: 'listing.report.spare.part.initial.quantity')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('quantityInStock')}">
				<g:sortableColumn property="inStockQuantity"  title="${message(code: 'listing.report.spare.part.in.stock.quantity')}" params="${params}" />
			</g:if>
			<g:if test="${reportTypeOptions.contains('usedQuantity')}">
				<th><g:message code="spare.part.used.quantity"/></th>
			</g:if>

			<g:if test="${reportSubType == ReportSubType.STOCKOUT && reportTypeOptions.contains('forecastedStockOut')}">
			    <th><g:message code="listing.report.spare.part.forecasted.stock.out.label"/></th>
			</g:if>
			<g:if test="${reportSubType == ReportSubType.USERATE && reportTypeOptions.contains('useRate')}">
			    <th><g:message code="listing.report.spare.part.use.rate.label"/></th>
			</g:if>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="sparePart">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<g:if test="${sparePart.dataLocation != null}">
						<g:message code="datalocation.label"/>: ${sparePart.dataLocation?.names}<br/>
					</g:if>
					<g:else>
						${sparePart.stockLocation?.name.toUpperCase()}
					</g:else>
					<g:if test="${!sparePart.room.equals('') && sparePart.room!=null}">
						<g:message code="spare.part.room.label"/>: ${sparePart.room}<br/>
					</g:if>
					<g:if test="${!sparePart.shelf.equals('') && sparePart.shelf!=null}">
						<g:message code="spare.part.shelf.label"/>: ${sparePart.shelf}<br/>
					</g:if>
				</td>
				<g:if test="${reportTypeOptions.contains('sparePartType')}">
					<td>${sparePart.type.names}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('locationOfStock')}">
					<td>
					${message(code: sparePart.stockLocation?.messageCode+'.'+sparePart.stockLocation?.name)}
				</td>
				</g:if>

				<g:if test="${reportTypeOptions.contains('purchaseDate')}">
					<td>${Utils.formatDate(sparePart.purchaseDate)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('deliveryDate')}">
					<td>${Utils.formatDate(sparePart.deliveryDate)}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('currentStatus')}">
					<td>${message(code: sparePart.status?.messageCode+'.'+sparePart.status?.name)}</td>
				</g:if>

				<g:if test="${reportSubType == ReportSubType.STATUSCHANGES && reportTypeOptions.contains('statusChanges')}">
					<g:set var="statusChangesEnum" value="${sparePart.getSparePartStatusChange(customizedListingParams?.statusChanges)}"/>
					<td>${message(code: statusChangesEnum?.messageCode+'.'+statusChangesEnum?.name)}</td>
				</g:if>

				<g:if test="${reportTypeOptions.contains('manufacturer')}">
					<td>${sparePart.type.manufacturer?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('supplier')}">
					<td>${sparePart.supplier?.contact?.contactName}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('purchaser')}">
					<td>${sparePart.sparePartPurchasedBy}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('cost')}">
					%{-- TODO AR format cost --}%
					<td>${sparePart.purchaseCost} ${sparePart.currency}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('discontinuedDate')}">
					<td>${Utils.formatDate(sparePart.type.discontinuedDate)}</td>
				</g:if>

				<g:if test="${reportTypeOptions.contains('initialQuantity')}">
					<td>${sparePart.initialQuantity}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('quantityInStock')}">
					<td>${sparePart.inStockQuantity}</td>
				</g:if>
				<g:if test="${reportTypeOptions.contains('usedQuantity')}">
					<td>${sparePart.usedQuantity?:0}</td>
				</g:if>

				<g:if test="${reportSubType == ReportSubType.STOCKOUT && reportTypeOptions.contains('forecastedStockOut')}">
				    <td>%{-- TODO AR calculation AFTER RELEASE --}%</td>
				</g:if>
				<g:if test="${reportSubType == ReportSubType.USERATE && reportTypeOptions.contains('useRate')}">
				    <td>%{-- TODO AR calculation AFTER RELEASE --}%</td>
				</g:if>
			</tr>
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />
