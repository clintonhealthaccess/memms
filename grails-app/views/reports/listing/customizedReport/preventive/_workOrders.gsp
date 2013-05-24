<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible" %>
<ul>
  <g:set var="statusValues" value="${PreventiveOrderStatus.values()}"/>
  <g:render template="/reports/listing/customizedReport/workOrderStatusAndPeriod" model="[statusValues:statusValues]" />
  <li>
    %{-- TODO fix checkbox list styles !!! --}%
    <label for="whoIsResponsible"><g:message code="reports.preventive.workOrders.whoIsResponsible"/>:</label>
    <g:each in="${PreventionResponsible.values() - PreventionResponsible.NONE}" var="statusEnum">
        <input name="whoIsResponsible" type="checkbox" value="${statusEnum.key}"/>
        <label for="${statusEnum.key}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
    </g:each>
  </li>
</ul>