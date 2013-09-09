<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<div class="v-tabs-dynav-wrap">
  <a class='v-tabs-dynav-scroll-right' href='#' id='js-scroll-right'></a>
  <div class="v-tabs-dynav" id='js-slider-wrapper'>
    <ul>
      <g:each in="${savedReports}" var="savedReport" status="i">
        <li>
          <%
            savedReportParams = [:]
            savedReportParams.putAll params
            savedReportParams['savedReportId'] = savedReport.id+""
          %>
          <a href="${createLink(controller: 'listing', action:'savedCustomizedListing', params: savedReportParams)}" class="tooltip" title="${savedReport.reportName}">
            ${savedReport.reportName}
          </a>
          <a class="delete-node" href="${createLink(controller: 'listing', action:'deleteCustomizedReport', params: savedReportParams)}">X</a>
        </li>
      </g:each>
      <li>
        <a href="${createLink(params:[reportType: ReportType.INVENTORY, reportSubType:ReportSubType.INVENTORY])}"
          class="tooltip" title="${message(code:'default.all.equipments.label')}">
          <g:message code="default.all.equipments.label" />
        </a>
      </li>
      <li>
        <a href="${createLink(controller: 'listing', action:'obsoleteEquipments')}"
          class="tooltip" title="${message(code:'default.obsolete.label')}">
          <g:message code="default.obsolete.label" />
        </a>
      </li>
      <li>
        <a href="${createLink(controller: 'listing', action:'disposedEquipments')}"
          class="tooltip" title="${message(code:'default.disposed.label')}">
          <g:message code="default.disposed.label" />
        </a>
      </li>
      <li>
        <a href="${createLink(controller: 'listing', action:'underMaintenanceEquipments')}"
          class="tooltip" title="${message(code:'default.under.maintenance.label')}">
          <g:message code="default.under.maintenance.label" />
        </a>
      </li>
      <li>
        <a href="${createLink(controller: 'listing', action:'inStockEquipments')}"
          class="tooltip" title="${message(code:'default.in.stock.label')}">
          <g:message code="default.in.stock.label" />
        </a>
      </li>
    </ul>
  </div>
  <a class='v-tabs-dynav-scroll-left' href='#' id='js-scroll-left'></a>
</div>
