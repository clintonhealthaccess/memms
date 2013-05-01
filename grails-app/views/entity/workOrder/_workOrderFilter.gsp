<%@ page import="org.chai.memms.corrective.maintenance.WorkOrder.Criticality"%>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus"%>
<%@ page import="java.util.Date" %>
<div class="filters main">
	<h2><g:message code="work.order.filter.label" /><a href="#" id="showhide" class="right"><g:message code="entity.show.hide.filter.label" /></a></h2>
	<g:hasErrors bean="${filterCmd}">
		<ul>
			<g:eachError var="err" bean="${filterCmd}">
				<h2>
					<g:message error="${err}" />
				</h2>
			</g:eachError>
		</ul>
	</g:hasErrors>

	<g:form url="[controller:'workOrderView', action:'filter']" method="get"
		useToken="false" class="filters-box">
		<ul class="filters-list third">
          <li><g:selectFromEnum name="criticality" values="${Criticality.values()}" field="criticality" 
          		label="${message(code:'work.order.criticality.label')}" bean="${filterCmd}"/></li>
        </ul>
		<ul class="filters-list third">
	      <li><g:selectFromEnum name="currentStatus" values="${OrderStatus.values()}" field="currentStatus" 
	      		label="${message(code:'entity.status.label')}" bean="${filterCmd}"/></li>
	      <li>
	        <div class="half">
	          <g:input name="openOn" dateClass="date-picker" label="${message(code:'order.open.on.label')}" 
	          	bean="${filterCmd}" field="openOn" value="${filterCmd?.openOn}"/>
	        </div>
	        <div class="half">
	          <g:input name="closedOn" dateClass="date-picker" label="${message(code:'order.closed.on.label')}" 
	          	bean="${filterCmd}" field="closedOn" value="${filterCmd?.closedOn}"/>
	        </div>
	      </li>
		</ul>
        <div class='clear-left'>
          <input type="hidden" name="dataLocation.id" value="${dataLocation?.id}" />
          <input type="hidden" name="equipment.id" value="${equipment?.id}" />
          <button type="submit"><g:message code="entity.filter.label" /></button>
          <a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
        </div>
	</g:form>
</div>
<g:if test="${params?.q}">
	<h2 class="filter-results">
		<g:message code="entity.filter.message.label" args="${[message(code: 'entity.work.order.label'),params?.q]}" />
	</h2>
</g:if>
