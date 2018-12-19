<div class="filters main">
		  <h2></h2>
			<g:hasErrors bean="${searchCmd}">
				<ul>
					<g:eachError var="err" bean="${searchCmd}">
						<h2><g:message error="${err}" /></h2>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:form  url="[controller:'search', action: 'findEquipments']" method="get" useToken="false" class="general-search-form">
				<ul class="filters-list third">
					<li><g:input name="term" label="${message(code:'equipment.search.term.label')}" bean="${searchCmd}" field="term" dateClass="search-term"/></li>
				</ul>
				<ul class="filters-list third">
					<li><g:selectFromList name="calculationLocationIds" label="${message(code:'equipment.search.location.label')}" bean="${searchCmd}" field="calculationLocationIds" optionKey="id" multiple="true"
				  			ajaxLink="${createLink(controller:'location', action:'getAjaxData', params: [class: 'CalculationLocation'])}"
				  			from="${locations}" value="${location?.id}" values="${locations.collect{it.names}}" />
					</li>
				</ul>
				<div class='clear-left'>
					<button type="submit"><g:message code="default.button.search.label"/></button>
					<a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
				</div>
		  	</g:form>
</div>
<g:if test="${params?.q}">
	<h2 class="filter-results">
		<g:message code="entity.filter.message.label" args="${[message(code: 'spare.part.label'),params?.term]}" />
	</h2>
</g:if>




