<div class="filters main">
	<h2>
		<g:message code="dashboard.filter.label" />
		<a href="#" id="showhide" class="right"><g:message code="entity.show.hide.filter.label" /></a>
	</h2>

	<g:hasErrors bean="${filterCmd}">
		<ul>
			<g:eachError var="err" bean="${filterCmd}">
				<h2>
					<g:message error="${err}" />
				</h2>
			</g:eachError>
		</ul>
	</g:hasErrors>
	<g:form url="[controller:'dashboard', action:'filters']" method="get" useToken="false" class="filters-box">
		<ul class="filters-list">
			<li>
				<g:selectFromList name="dataLocations" label="${message(code:'reports.dataLocation')}"
	            field="dataLocations" optionKey="id" multiple="true" value="${dataLocations*.id}"
	            ajaxLink="${createLink(controller: 'dataLocation', action:'getAjaxData')}"
	            values="${dataLocations.collect{it.names+' '+it.id+' ['+it.location.names+']'}}" />
			</li>
		</ul>
		<button type="submit">Filter</button>
		<a href="#" class="clear-form"><g:message code="default.link.clear.form.label" /></a>
	</g:form>
</div>