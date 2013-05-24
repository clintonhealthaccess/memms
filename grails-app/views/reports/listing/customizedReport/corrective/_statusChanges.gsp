<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.WorkOrderStatusChange" %>
<ul>
  <li>
    %{-- TODO fix checkbox list styles !!! --}%
    <label for="statusChanges"><g:message code="reports.statusChanges"/>:</label>
    <g:each in="${WorkOrderStatusChange.values()}" var="statusEnum">
        <input name="statusChanges" type="checkbox" value="${statusEnum}"/>
        <label for="${statusEnum}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
    </g:each>
  </li>
  <g:render template="/reports/listing/customizedReport/statusChangesPeriod"/>
  <li>
    %{-- TODO fix checkbox styles !!! --}%
    <label for="warranty"><g:message code="reports.inventory.warranty"/>:</label>
    <input name="warranty" type="checkbox"/>
  </li>
</ul>