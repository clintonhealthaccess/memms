<%@ page import="org.chai.memms.spare.part.SparePart.StockLocation" %>
<div class="filters main">
		  <h2></h2>
			<g:hasErrors bean="${searchCmd}">
				<ul>
					<g:eachError var="err" bean="${searchCmd}">
						<h2><g:message error="${err}" /></h2>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:form  url="[controller:'search', action: 'findSpareParts']" method="get" useToken="false" class="general-search-form">
				<ul class="filters-list third">
					<li><g:input name="term" label="${message(code:'spare.part.search.term.label')}" bean="${searchCmd}" field="term" dateClass="search-term"/></li>
				</ul>
				<ul class="filters-list third">
					<li>
						<g:selectFromEnum name="stockLocation" bean="${cmd}" values="${StockLocation.values()}" field="stockLocation" label="${message(code:'spare.part.stockLocation.label')}"/>
					</li>
					<li class="hidden-item">
						<g:selectFromList name="calculationLocationIds" label="${message(code:'spare.part.search.location.label')}" bean="${searchCmd}" field="calculationLocationIds" optionKey="id" multiple="true"
					  			ajaxLink="${createLink(controller:'location', action:'getAjaxData', params: [class: 'CalulculationLocation'])}"
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

<script type="text/javascript">
	$(document).ready(function() {
		$(".hidden-item").hide();
		$("select[name=stockLocation]").change(function(e){
			if($(this).val()=="FACILITY") $(".hidden-item").slideDown("slow");
			else $(".hidden-item").slideUp("slow");
		});
		$('.clear-form').click(function() {
		 $(".hidden-item").slideUp("slow");
		});
	});
</script>









