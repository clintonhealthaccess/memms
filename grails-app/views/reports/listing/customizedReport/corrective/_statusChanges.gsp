<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus" %>
<ul>
  <li>
    <label for="statusChanges"><g:message code="reports.statusChanges"/>:</label>
    <select name="statusChanges" class="js-custom-report-subtype">
      <g:set var="newOrderMap" 
        value="${[[OrderStatus.NONE]:[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC]]}"/>
      <option value="${newOrderMap}">
        <g:message code="reports.corrective.statusChanges.newOrder"/>
      </option>
      <g:set var="orderEscalatedToMmcMap"
        value="${[[OrderStatus.OPENATFOSA]:[OrderStatus.OPENATMMC]]}"/>
      <option value="${orderEscalatedToMmcMap}">
        <g:message code="reports.corrective.statusChanges.orderEscalatedToMmc"/>
      </option>
      <g:set var="equipmentReceivedFromMmcFixedMap"
        value="${[[OrderStatus.OPENATMMC]:[OrderStatus.CLOSEDFIXED]]}"/>
      <option value="${equipmentReceivedFromMmcFixedMap}">
        <g:message code="reports.corrective.statusChanges.equipmentReceivedFromMmcFixed"/>
      </option>
      <g:set var="equipmentReceivedFromMmcNotFixedMap"
        value="${[[OrderStatus.OPENATMMC]:[OrderStatus.OPENATFOSA]]}"/>
      <option value="${equipmentReceivedFromMmcNotFixedMap}">
        <g:message code="reports.corrective.statusChanges.equipmentReceivedFromMmcNotFixed"/>
      </option>
      <g:set var="closedOrderFixedMap"
        value="${[[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC]:[OrderStatus.CLOSEDFIXED]]}"/>
      <option value="${closedOrderFixedMap}">
        <g:message code="reports.corrective.statusChanges.closedOrderFixed"/>
      </option>
      <g:set var="closedOrderNotFixedMap"
        value="${[[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC]:[OrderStatus.CLOSEDFORDISPOSAL]]}"/>
      <option value="${closedOrderNotFixedMap}">
        <g:message code="reports.corrective.statusChanges.closedOrderNotFixed"/>
      </option>

    </select>
  </li>
  <g:render template="/reports/listing/customizedReport/statusChangesPeriod"/>
  <li>
    %{-- TODO fix checkbox styles !!! --}%
    <label for="warranty"><g:message code="reports.inventory.warranty"/>:</label>
    <input name="warranty" type="checkbox"/>
  </li>
</ul>