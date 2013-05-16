<%@ page import="org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart" %>
<ul>
  <li>
    <label for="statusChanges"><g:message code="reports.statusChanges"/>:</label>
    <select name="statusChanges" class="js-custom-report-subtype">      
      <g:set var="newPendingOrderMap" 
        value="${[[StatusOfSparePart.NONE]:[StatusOfSparePart.PENDINGORDER]]}"/>
      <option value="${newPendingOrderMap}">
        <g:message code="New pending order"/>
      </option>
      <g:set var="pendingOrderArrivedMap"
        value="${[[StatusOfSparePart.PENDINGORDER]:[StatusOfSparePart.INSTOCK]]}" performed="false"/>
      <option value="${pendingOrderArrivedMap}">
        <g:message code="Pending order that arrived"/>
      </option>
      <g:set var="sparePartsAssociatedToEquipmentMap"
        value="${[(StatusOfSparePart.values()-[StatusOfSparePart.OPERATIONAL,StatusOfSparePart.DISPOSED]):[StatusOfSparePart.OPERATIONAL]]}"/>
      <option value="${sparePartsAssociatedToEquipmentMap}">
        <g:message code="Spare parts associated to equipment"/>
      </option>
      <g:set var="disposedSparePartsMap"
        value="${[(StatusOfSparePart.values()-StatusOfSparePart.DISPOSED):[StatusOfSparePart.OPERATIONAL]]}"/>
      <option value="${disposedSparePartsMap}">
        <g:message code="Disposed spare parts"/>
      </option>
    </select>
  </li>
  <g:render template="/reports/listing/customizedReport/statusChangesPeriod"/>
</ul>