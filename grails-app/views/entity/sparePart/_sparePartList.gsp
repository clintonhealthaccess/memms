<%@ page import="org.chai.memms.spare.part.SparePart.SparePartStatus" %>
<%@ page import="org.chai.memms.util.Utils" %>
<table class="items spaced">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="type"  title="${message(code: 'spare.part.type.label')}" params="[q:q,'type.id':type?.id,status:status]" />
			<g:sortableColumn property="stockLocation"  title="${message(code: 'spare.part.stockLocation.label')}" params="[q:q,'type.id':type?.id,status:status]" />
			<g:sortableColumn property="status"  title="${message(code: 'spare.part.status.label')}" params="[q:q,'type.id':type?.id,status:status]" />
			<th><g:message code="location.label"/></th>
			<g:sortableColumn property="purchaseDate"  title="${message(code: 'spare.part.purchase.date.label')}" params="[q:q,'type.id':type?.id,status:status]" />
			<g:sortableColumn property="sparePartPurchasedBy"  title="${message(code: 'spare.part.purchaser.label')}" params="[q:q,'type.id':type?.id,status:status]" />	
			<g:sortableColumn property="purchaseCost"  title="${message(code: 'spare.part.purchase.cost.label')}" params="[q:q,'type.id':type?.id,status:status]" />
			<g:sortableColumn property="supplier"  title="${message(code: 'provider.type.supplier')}" params="[q:q,'type.id':type?.id,status:status]" />
			<g:sortableColumn property="initialQuantity"  title="${message(code: 'spare.part.initial.quantity')}" params="[q:q,'type.id':type?.id,status:status]" />
			<g:sortableColumn property="inStockQuantity"  title="${message(code: 'spare.part.in.stock.quantity')}" params="[q:q,'type.id':type?.id,status:status]" />
			<th><g:message code="spare.part.used.quantity"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="sparePart">
			<tr >
				<td>
					<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'sparePart', action:'edit', params:[id: sparePart.id])}"  class="edit-button">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:'sparePart', action:'delete', params:[id: sparePart.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td>
					${sparePart.type.names}
				</td>
				<td>
					${message(code: sparePart.stockLocation?.messageCode+'.'+sparePart.stockLocation?.name)}
				</td>
				<td>
					${message(code: sparePart.status?.messageCode+'.'+sparePart.status?.name)}
				</td>
				<td>
					<g:if test="${sparePart.dataLocation != null}">
						<g:message code="datalocation.label"/>: ${sparePart.dataLocation?.names}<br/>
					</g:if>
					<g:if test="${!sparePart.room.equals("") && sparePart.room!=null}">
						<g:message code="spare.part.room.label"/>: ${sparePart.room}<br/>
					</g:if>
					<g:if test="${!sparePart.shelf.equals("") && sparePart.shelf!=null}">
						<g:message code="spare.part.shelf.label"/>: ${sparePart.shelf}<br/>
					</g:if>
				</td>
				<td>
					${Utils.formatDate(sparePart.purchaseDate)}
				</td>
				<td>
					${message(code: sparePart.sparePartPurchasedBy?.messageCode+'.'+sparePart.sparePartPurchasedBy?.name)}
				</td>
				<td>
					${sparePart.purchaseCost} ${sparePart.currency}
				</td>
				<td>
					${sparePart.supplier?.contact?.contactName}
				</td>	
				<td>
					${sparePart.initialQuantity}
				</td>
				<td>
					${sparePart.inStockQuantity}
				</td>		
				<td>
					${sparePart.usedQuantity}
				</td>								
			</tr>
		</g:each>
	</tbody>	
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />