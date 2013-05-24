<%@ page import="org.chai.memms.inventory.EquipmentStatus.EquipmentStatusChange" %>
<ul>
  <li>
    %{-- TODO fix checkbox list styles !!! --}%
    <label for="statusChanges"><g:message code="reports.statusChanges"/>:</label>
    <g:each in="${EquipmentStatusChange.values()}" var="statusEnum">
        <input name="statusChanges" type="checkbox" value="${statusEnum}"/>
        <label for="${statusEnum}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
    </g:each>
  </li>
  <g:render template="/reports/listing/customizedReport/statusChangesPeriod"/>
</ul>