<label for="report_type">Report Subtype</label>
<select name="reportSubType" class="js-custom-report-subtype">
  <option value="0">Please select</option>
  <g:if test="${['inventory','spareParts'].contains(reportType)}">
    <option value="inventory" ${reportSubType == 'inventory'?'selected':''}>
      List of Inventory</option>
  </g:if>
  <g:if test="${['corrective','preventive'].contains(reportType)}">
    <option value="workdOrders" ${reportSubType == 'workOrders'?'selected':''}>
      List of Work Orders</option>
  </g:if>
  <g:if test="${['inventory','corrective','preventive','spareParts'].contains(reportType)}">
    <option value="statusChanges" ${reportSubType == 'statusChanges'?'selected':''}>
      List of Status Changes</option>
  </g:if>
  <g:if test="${['spareParts'].contains(reportType)}">
    <option value="useRate" ${reportSubType == 'useRate'?'selected':''}>
      Spare Part Use Rate</option>
  </g:if>
  <g:if test="${['spareParts'].contains(reportType)}">
    <option value="stockOut" ${reportSubType == 'stockOut'?'selected':''}>
      Forecast Stock Out</option>
  </g:if>
</select>