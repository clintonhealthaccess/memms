<%@ page import="org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart" %>
<table class="items spaced">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code"  title="${message(code: 'spare.part.code.label')}" params="[q:q]" />
			<g:sortableColumn property="type"  title="${message(code: 'spare.part.type.label')}" params="[q:q]" />
			<g:sortableColumn property="model"  title="${message(code: 'spare.part.model.label')}" params="[q:q]" />
			<g:sortableColumn property="statusOfSparePart"  title="${message(code: 'spare.part.status.label')}" params="[q:q]" />
			<g:sortableColumn property="usedOnEquipment"  title="${message(code: 'spare.part.used.on.equipment.label')}" params="[q:q]" />
			<th><g:message code="location.label"/></th>
			<g:sortableColumn property="purchaseDate"  title="${message(code: 'spare.part.purchase.date.label')}" params="[q:q]" />
			<g:sortableColumn property="purchaseCost"  title="${message(code: 'spare.part.purchase.cost.label')}" params="[q:q]" />
			<g:sortableColumn property="sameAsManufacturer"  title="${message(code: 'spare.part.same.as.manufacturer.label')}" params="[q:q]" />
			<g:sortableColumn property="supplier"  title="${message(code: 'provider.type.supplier')}" params="[q:q]" />
			<g:sortableColumn property="sparePartPurchasedBy"  title="${message(code: 'spare.part.purchaser.label')}" params="[q:q]" />
			
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
				<td>${sparePart.code}</td>
				<td>${sparePart.type.names}</td>
				<td>${sparePart.model}</td>
				<td>
					<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'create', params:['sparePart.id': sparePart?.id])}" title="${message(code: 'tooltip.click.update.status')}" class="tooltip">
  	    				${message(code: sparePart.statusOfSparePart?.messageCode+'.'+sparePart.statusOfSparePart?.name)}
  	    			</a>
				</td>
				<td>
				<g:if test="${sparePart?.usedOnEquipment != null}">
					<g:if test="${sparePart.statusOfSparePart.equals(StatusOfSparePart.DISPOSED)}">
					</g:if>
					<g:if test="${sparePart.statusOfSparePart.equals(StatusOfSparePart.OPERATIONAL)}">
					${sparePart.usedOnEquipment.code}
					</g:if>
				</g:if>
				<g:elseif test="${sparePart?.usedOnEquipment == null && sparePart.statusOfSparePart.equals(StatusOfSparePart.PENDINGORDER)}">
					<a href="${createLinkWithTargetURI(controller:'sparePart', action:'edit', params:[id: sparePart.id])}">
						<button class="escalate next small gray"><g:message code="spare.part.arrive.in.stock.link.label" /></button>
					</a>
				</g:elseif>
				<g:elseif test="${sparePart?.usedOnEquipment == null && sparePart.statusOfSparePart.equals(StatusOfSparePart.DISPOSED)}">

				</g:elseif>
				<g:else>
					<a href="${createLinkWithTargetURI(controller:'sparePart', action:'edit', params:[id: sparePart.id])}">
						<button class="escalate next small gray"><g:message code="spare.part.assign.equipment.link.label" /></button>
					</a>		
				</g:else>
				</td>
				<td>
					<g:message code="datalocation.label"/>: ${sparePart.dataLocation?.names}<br/>
					
				</td>
				<td>${sparePart.purchaseDate}</td>
				<td>${sparePart.purchaseCost} ${sparePart.currency}</td>
				<td>
					<g:if name="sameAsManufacturer" id="${sparePart.id}" test="${(sparePart.sameAsManufacturer==true)}">&radic;</g:if>
					<g:else>&nbsp;</g:else>
				</td>					
				<td>${sparePart.supplier.contact.contactName}</td>				
				<td>${message(code: sparePart.sparePartPurchasedBy?.messageCode+'.'+sparePart.sparePartPurchasedBy?.name)}</td>				
			</tr>
		</g:each>
	</tbody>	
</table>
<g:render template="/templates/pagination" />
<script type="text/javascript">
	$(document).ready(function() {
		updateEquipment("${createLink(controller:'sparePartView',action: 'updateSameAsManufacturer')}");
	});
</script>

