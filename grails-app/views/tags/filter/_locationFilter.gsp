<div class="left">
	<span class="js_dropdown dropdown">
		<a class="location js_dropdown-link nice-button with-highlight" href="#" data-type="location">
			<g:if test="${currentLocation != null}">
				${currentLocation.names}
			</g:if>
			<g:else>
				<g:message code="filter.location.noselection.label"/>
			</g:else> 
		</a>
		<div class="dropdown-list js_dropdown-list push-top-10">
			<g:if test="${locationFilterTree != null && !locationFilterTree.empty}">
				<ul>
					<g:render template="/tags/filter/locationTree"
						model="[
							controller: controllerName,
							action: actionName,
							current: currentLocation,
							location: locationFilterRoot,
							locationFilterTree: locationFilterTree,				
							linkParams: linkParams
						]"/>
				</ul>
			</g:if>
		</div>
	</span>
</div>
