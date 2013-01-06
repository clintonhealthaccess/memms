<span class="right">
	<shiro:hasPermission permission="preventiveOrder:create">
			<ul class="new-entity-dropdown">
				<li>
					<a href="#" class="next medium gray left push-r"> 
						<g:message code="default.new.label" args="[entityName]" />
					</a>
					<ul class="new-entity-sub-menu">
						<li>
							<a href="${createLinkWithTargetURI(controller:'durationBasedOrder', action:'create', params:['location.id': location?.id,'dataLocation.id': dataLocation?.id,'equipment.id':equipment?.id])}"> 
								<g:message code="preventive.order.type.duration.based"/>
							</a>
						</li>
						<li>
							<a href="${createLinkWithTargetURI(controller:'workBasedOrder', action:'create', params:['location.id': location?.id,'dataLocation.id': dataLocation?.id,'equipment.id':equipment?.id])}"> 
								<g:message code="preventive.order.type.work.based"/>
							</a>
						</li>
					</ul>
				</li>
	 		</ul>
	</shiro:hasPermission>
	<g:searchBox action="search"  controller="preventiveOrderView"/>
</span>
	