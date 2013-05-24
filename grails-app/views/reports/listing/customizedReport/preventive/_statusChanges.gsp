<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatusChange" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible" %>
<ul>
  <li>
    %{-- TODO fix checkbox list styles !!! --}%
    <label for="statusChanges"><g:message code="reports.statusChanges"/>:</label>
    <g:each in="${PreventiveOrderStatusChange.values()}" var="statusEnum">
        <input name="statusChanges" type="checkbox" value="${statusEnum}"/>
        <label for="${statusEnum}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
    </g:each>
  </li>
  <g:render template="/reports/listing/customizedReport/statusChangesPeriod"/>
  <li>
    %{-- TODO fix checkbox list styles !!! --}%
    <label for="doneByWho"><g:message code="reports.preventive.statusChanges.doneByWho"/>:</label>
    <g:each in="${PreventionResponsible.values() - PreventionResponsible.NONE}" var="statusEnum">
        <input name="doneByWho" type="checkbox" value="${statusEnum.key}"/>
        <label for="${statusEnum.key}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
    </g:each>
  </li>
</ul>