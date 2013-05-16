<%@ page import="org.chai.memms.inventory.EquipmentStatus.Status" %>
<ul>
  <li>
    <label for="statusChanges"><g:message code="reports.statusChanges"/>:</label>
    <select name="statusChanges" class="js-custom-report-subtype">
      <g:set var="newEquipmentMap" 
        value="${[[Status.NONE]:(Status.values()-Status.NONE)]}"/>
      <option value="${newEquipmentMap}">
        <g:message code="reports.inventory.statusChanges.newEquipment"/>
      </option>
      <g:set var="disposedEquipmentMap"
        value="${[(Status.values()-[Status.FORDISPOSAL,Status.DISPOSED]):[Status.FORDISPOSAL,Status.DISPOSED]]}"/>
      <option value="${disposedEquipmentMap}">
        <g:message code="reports.inventory.statusChanges.disposedEquipment"/>
      </option>
      <g:set var="equipmentFromStockToOperationalMap"
        value="${[[Status.INSTOCK]:[Status.OPERATIONAL,Status.PARTIALLYOPERATIONAL]]}"/>
      <option value="${equipmentFromStockToOperationalMap}">
        <g:message code="reports.inventory.statusChanges.equipmentFromStockToOperational"/>
      </option>
      <g:set var="equipmentForMaintenanceMap"
        value="${[(Status.values()-Status.UNDERMAINTENANCE):[Status.UNDERMAINTENANCE]]}"/>
      <option value="${equipmentForMaintenanceMap}">
        <g:message code="reports.inventory.statusChanges.equipmentForMaintenance"/>
      </option>
    </select>
  </li>
  <g:render template="/reports/listing/customizedReport/statusChangesPeriod"/>
</ul>