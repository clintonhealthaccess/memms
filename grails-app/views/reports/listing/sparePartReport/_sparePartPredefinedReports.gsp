<ul class="v-tabs-subnav">
	<li>
		<a class="${actionName == 'generalSparePartsListing'?'active':''}" 
			href="${createLinkWithTargetURI(controller: 'listing', action:'generalSparePartsListing')}" id="report-1">
			TODO All Spare Parts
		</a></li>
	<li>
		<a class="${actionName == 'otherSparePartsListing'?'active':''}" 
			href="${createLinkWithTargetURI(controller: 'listing', action:'otherSparePartsListing')}" id="report-1">
			TODO Other Spare Parts
		</a></li>
</ul>