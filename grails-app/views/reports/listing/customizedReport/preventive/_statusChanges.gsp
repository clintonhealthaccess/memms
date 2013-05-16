<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible" %>
<ul>
  <li>
    <label for="statusChanges"><g:message code="reports.statusChanges"/>:</label>
    <select name="statusChanges" class="js-custom-report-subtype">      
      <g:set var="preventiveMaintenancePerformedMap" 
        value="${[[PreventiveOrderStatus.OPEN]:[(PreventiveOrderStatus.OPEN):true]]}"/>
      <option value="${preventiveMaintenancePerformedMap}">
        <g:message code="reports.preventive.statusChanges.preventiveMaintenancePerformed"/>
      </option>
      <g:set var="preventiveMaintenanceNotPerformedMap"
        value="${[[PreventiveOrderStatus.OPEN]:[(PreventiveOrderStatus.OPEN):false]]}"/>
      <option value="${preventiveMaintenanceNotPerformedMap}">
        <g:message code="reports.preventive.statusChanges.preventiveMaintenanceNotPerformed"/>
      </option>
      <g:set var="closedScheduledPreventiveMaintenanceMap"
        value="${[[PreventiveOrderStatus.OPEN]:[PreventiveOrderStatus.CLOSED]]}"/>
      <option value="${closedScheduledPreventiveMaintenanceMap}">
        <g:message code="reports.preventive.statusChanges.closedScheduledPreventiveMaintenance"/>
      </option>
    </select>
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