<%@ page import="org.chai.memms.util.Utils" %>
<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:q]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="partNumber" params="[q:q]" title="${message(code: 'entity.part.number.label')}" />	
			<th><g:message code="entity.manufacturer.label"/></th>
			<th><g:message code="sparePartType.quantity.in.stock.label"/></th>	
			<th><g:message code="sparePartType.estimated.date.stockout.label"/></th>
			<g:sortableColumn property="discontinuedDate" params="[q:q]" title="${message(code: 'entity.discontinued.date.label')}" />
			<th><g:message code="equipmentType.compatible.equipment.types.label" /></th>
			<th><g:message code="provider.vendors.label" /></th>
       </tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="type">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'sparePartType', action:'edit', params:[id: type.id])}" class="edit-button">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:'sparePartType', action:'delete', params:[id: type.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
						</li>		
					</ul>
				</td>
				<td>
					${type.names}
				</td>
				<td>
					${type.partNumber}
				</td>
				<td>
					${type.manufacturer.contact.contactName}
				</td>
				<td>
					<g:if test="${(type.inStockSpareParts?.size()>0)}">
						<a href="${createLinkWithTargetURI(controller:'sparePartView', action:'list', params:['type.id': type.id, 'status':"INSTOCK"])}">
							${type.inStockSparePartsQuantity}
						</a>
					</g:if>
					<g:else>0</g:else>
				</td>
				<td>
				   
				</td>
				<td>
					${Utils.formatDateWithTime(type?.discontinuedDate)}
				</td>
				<td>
					<g:if test="${(type.compatibleEquipmentTypes?.size()>0)}">
						<a href="#" class="toggle-list type"> 
							${type.compatibleEquipmentTypes.size()}
						</a>
					</g:if>
					<g:else>0</g:else>
				</td> 
				<td>
					<g:if test="${(type.vendors?.size()>0)}">
						<a href="#" class="toggle-list vendor"> 
							${type.vendors.size()} 
						</a>
					</g:if>
					<g:else>0</g:else>
				</td>    
			</tr>
			<g:if test="${(type.compatibleEquipmentTypes?.size()>0)}">
				<tr class="sub-list types">
					<td colspan="9">
						<h3><g:message code="equipmentType.compatible.equipment.types.label" /></h3>
						<g:render template="/entity/sparePartType/compatibleEquipmentTypeList" model="[entities: type.compatibleEquipmentTypes]"/>
					</td>
				</tr>
			</g:if >
			<g:if test="${(type.vendors?.size()>0)}">
				<tr class="sub-list vendors">
					<td colspan="9">
						<h3><g:message code="provider.vendors.label" /></h3>
						<g:render template="/entity/sparePartType/vendorList" model="[entities: type.vendors]"/>
					</td>
				</tr>
			</g:if >
		</g:each>
	</tbody>
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />
<script type="text/javascript">
	$(document).ready(function() {
		$(".sub-list").hide();
	});
</script>

