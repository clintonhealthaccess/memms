<r:require module="foldable" />
<li class="${current?.id == location?.id ?'current':''} js_foldable foldable ${location?.level==1 ?'opened':''}">
	<% def locationLinkParams = new HashMap(linkParams) %>
	<% locationLinkParams['location'] = location.id+"" %>
	<% linkParams = locationLinkParams %>
	<g:if test="${location.children != null && !location.children.empty && !locationFilterTree.disjoint(location.children)}">
		<a class="js_foldable-toggle foldable-toggle" href="#">(toggle)</a>
	</g:if>
	<a class="dropdown-link js_dropdown-link parameter" data-type="location"
		data-location="${location.id}"
		href="${createLinkByFilter(controller:controller, action:action, params:linkParams)}">
		${location.names}
	</a>
	<g:if test="${location.children != null && !location.children.empty}">
		<g:each in="${location.children}" var="child">
			<g:if test="${locationFilterTree.contains(child)}">
				<ul class="js_foldable-container foldable-container">
					<g:render template="/tags/filter/locationTree"
						model="[controller: controller,
						action: action,
						current: current,
						location: child,
						locationFilterTree:locationFilterTree,
						linkParams:linkParams]" />
				</ul>
			</g:if>
		</g:each>
	</g:if>
</li>
