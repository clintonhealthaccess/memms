<%@ page import="org.chai.memms.maintenance.WorkOrder.Criticality"%>
<%@ page import="org.chai.memms.maintenance.WorkOrder.OrderStatus"%>
<%@ page import="java.util.Date" %>
<div class="filters main">
	<h2>
		Filter work orders<a href="#" class="right"><img
			src="${resource(dir:'images/icons',file:'icon_close_flash.png')}"
			alt="Section" /></a>
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

	<g:form url="[controller:'workOrder', action:'filter']" method="get"
		useToken="false" class="filters-box">
		<ul class="filters-list">
			<li><g:selectFromEnum name="criticality" values="${Criticality.values()}" field="criticality" label="${message(code:'work.order.criticality.label')}" bean="${filterCmd}"/></li>
			<li><g:selectFromEnum name="status" values="${OrderStatus.values()}" field="status" label="${message(code:'work.order.status.label')}" bean="${filterCmd}"/></li>
			<li><label><g:message code="work.order.assistance.request.label" /></label> 
			<select name="assistaceRequested">
					<option value=""> <g:message code="default.please.select" /> </option>
					<option value="true" ${filterCmd?.assistaceRequested?.equals("true")? 'selected' : ''}> <g:message code="default.boolean.true" /> </option>
					<option value="false" ${filterCmd?.assistaceRequested?.equals("false")? 'selected' : ''}> <g:message code="default.boolean.false" /> </option>
			</select></li>
			<li><g:input name="openOn" dateClass="date-picker" label="${message(code:'work.order.openOn.label')}" bean="${filterCmd}" field="openOn" value="${filterCmd?.openOn}"/></li>
			<li><g:input name="closedOn" dateClass="date-picker" label="${message(code:'work.order.closedOn.label')}" bean="${filterCmd}" field="closedOn" value="${filterCmd?.closedOn}"/></li>
		</ul>
		<button type="submit">Filter</button>
		<input type="hidden" name="dataLocation.id" value="${dataLocation?.id}" />
		<input type="hidden" name="equipment.id" value="${equipment?.id}" />
	</g:form>
</div>
<g:if test="${params?.q}">
	<h2 class="filter-results">
		Showing filtered list of equipment which contain search term
		${params?.q}
	</h2>
</g:if>