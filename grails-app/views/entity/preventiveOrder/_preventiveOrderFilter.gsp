<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType"%>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus"%>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible"%>
<%@ page import="java.util.Date" %>
<div class="filters main">
  <h2><g:message code="preventive.order.filter.label" /><a href="#" id="showhide" class="right"><g:message code="entity.show.hide.filter.label" /></a></h2>
  <g:hasErrors bean="${filterCmd}">
    <ul>
      <g:eachError var="err" bean="${filterCmd}">
        <h2>
          <g:message error="${err}" />
        </h2>
      </g:eachError>
    </ul>
  </g:hasErrors>

  <g:form url="[controller:'preventiveOrderView', action:'filter']" method="get"
    useToken="false" class="filters-box">
    <ul class="filters-list third">
      <li><g:selectFromEnum name="type" values="${PreventiveOrderType.values()}" field="type" label="${message(code:'preventive.order.type.label')}" bean="${filterCmd}"/></li>
      <li>
        <div class="half">
          <g:input name="openOn" dateClass="date-picker" label="${message(code:'order.open.on.label')}" bean="${filterCmd}" field="openOn" value="${filterCmd?.openOn}"/>
        </div>
        <div class="half">
          <g:input name="closedOn" dateClass="date-picker" label="${message(code:'order.closed.on.label')}" bean="${filterCmd}" field="closedOn" value="${filterCmd?.closedOn}"/>
        </div>
      </li>
    </ul>
    <ul class="filters-list third">
      <li><g:selectFromEnum name="status" values="${PreventiveOrderStatus.values()}" field="currentStatus" label="${message(code:'entity.status.label')}" bean="${filterCmd}"/></li>
    </ul>
    <ul class="filters-list third">
      <li><g:selectFromEnum name="preventionResponsible" values="${PreventionResponsible.values()}" field="currentStatus" label="${message(code:'preventive.order.prevention.responsible.label')}" bean="${filterCmd}"/></li>
    </ul>
    <div class="clear-left">
      <button type="submit"><g:message code="entity.filter.label" /></button>
      <a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
    </div>
    <input type="hidden" name="dataLocation.id" value="${dataLocation?.id}" />
    <input type="hidden" name="equipment.id" value="${equipment?.id}" />
  </g:form>
</div>
<g:if test="${params?.q}">
  <h2 class="filter-results">
    <g:message code="entity.filter.message.label" args="${[message(code: 'entity.preventive.order.label'),params?.q]}" />
  </h2>
</g:if>

<div class="filters main projection">
  <a class="next medium gray" href="${createLinkWithTargetURI(controller:'durationBasedOrder',action:'calendar',params:['dataLocation.id': dataLocation?.id])}">
    <g:message code="preventive.order.view.mapper.label"/>
  </a>
    <p class="question-help">
      <g:message code="preventive.order.view.mapper.tip.label"/>
    </p>
</div>

