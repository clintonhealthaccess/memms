<ul class="v-tabs-subnav slide">
	<li>
		<a class="${actionName == 'generalEquipmentsListing'?'active':''}" 
			href="${createLinkWithTargetURI(controller: 'listing', action:'generalEquipmentsListing')}" id="report-1">
			<g:message code="default.all.equipments.label" />
		</a></li>
	<li>
		<a class="${actionName == 'obsoleteEquipments'?'active':''}" 
		href="${createLinkWithTargetURI(controller: 'listing', action:'obsoleteEquipments')}" id="report-2">
			<g:message code="default.obsolete.label" />
		</a></li>
	<li>
		<a class="${actionName == 'disposedEquipments'?'active':''}" 
		href="${createLinkWithTargetURI(controller: 'listing', action:'disposedEquipments')}" id="report-3">
			<g:message code="default.disposed.label" />
		</a></li>
	<li>
		<a class="${actionName == 'underMaintenanceEquipments'?'active':''}" 
		href="${createLinkWithTargetURI(controller: 'listing', action:'underMaintenanceEquipments')}" id="report-4">
			<g:message code="default.under.maintenance.label" />
		</a></li>
	<li>
		<a class="${actionName == 'inStockEquipments'?'active':''}" 
		href="${createLinkWithTargetURI(controller: 'listing', action:'inStockEquipments')}" id="report-5">
			<g:message code="default.in.stock.label" />
		</a></li>
	<li>
		<a class="${actionName == 'underWarrantyEquipments'?'active':''}" 
		href="${createLinkWithTargetURI(controller: 'listing', action:'underWarrantyEquipments')}" id="report-6">
			<g:message code="default.under.waranty.label" />
		</a></li>
</ul>