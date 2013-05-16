<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<label for="reportSubType"><g:message code="reports.subType.label"/></label>
<select name="reportSubType" class="js-custom-report-subtype">
  <g:if test="${[ReportType.INVENTORY,ReportType.SPAREPARTS].contains(reportType)}">
    <option value="${ReportSubType.INVENTORY}" ${reportSubType == ReportSubType.INVENTORY?'selected':''}>
      <g:message code="reports.subType.${ReportSubType.INVENTORY.reportSubType}"/>
    </option>
  </g:if>
  <g:if test="${[ReportType.CORRECTIVE,ReportType.PREVENTIVE].contains(reportType)}">
    <option value="${ReportSubType.WORKORDERS}" ${reportSubType == ReportSubType.WORKORDERS?'selected':''}>
      <g:message code="reports.subType.${ReportSubType.WORKORDERS.reportSubType}"/>
    </option>
  </g:if>
  <g:if test="${[ReportType.INVENTORY,ReportType.CORRECTIVE,ReportType.PREVENTIVE,ReportType.SPAREPARTS].contains(reportType)}">
    <option value="${ReportSubType.STATUSCHANGES}" ${reportSubType == ReportSubType.STATUSCHANGES?'selected':''}>
      <g:message code="reports.subType.${ReportSubType.STATUSCHANGES.reportSubType}"/>
    </option>
  </g:if>
  <g:if test="${[ReportType.SPAREPARTS].contains(reportType)}">
    <option value="${ReportSubType.USERATE}" ${reportSubType == ReportSubType.USERATE?'selected':''}>
      <g:message code="reports.subType.${ReportSubType.USERATE.reportSubType}"/>
    </option>
  </g:if>
  <g:if test="${[ReportType.SPAREPARTS].contains(reportType)}">
    <option value="${ReportSubType.STOCKOUT}" ${reportSubType == ReportSubType.STOCKOUT?'selected':''}>
      <g:message code="reports.subType.${ReportSubType.STOCKOUT.reportSubType}"/>
    </option>
  </g:if>
</select>