<ul class="v-tabs-subnav">
	<li>
		<a class="${actionName == 'generalPreventiveOrdersListing'?'active':''}" 
			href="${createLinkWithTargetURI(controller: 'listing', action:'generalPreventiveOrdersListing')}" id="report-1">
			TODO All Preventions
		</a></li>
	<li>
		<a class="${actionName == 'equipmentsWithPreventionPlan'?'active':''}" 
			href="${createLinkWithTargetURI(controller: 'listing', action:'equipmentsWithPreventionPlan')}" id="report-1">
			Equipments with Prevention Plan
		</a></li>
	<li>
		<a class="${actionName == 'preventionsDelayed'?'active':''}" 
		href="${createLinkWithTargetURI(controller: 'listing', action:'preventionsDelayed')}" id="report-2">
			Preventions Delayed
		</a></li>
</ul>