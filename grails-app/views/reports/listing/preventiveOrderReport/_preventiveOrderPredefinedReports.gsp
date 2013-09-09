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
				<a href="${createLink(params:[reportType: ReportType.PREVENTIVE, reportSubType:ReportSubType.WORKORDERS])}"
					class="tooltip" title="${message(code:'default.all.preventive.order.label')}">
					<g:message code="default.all.preventive.order.label"/>
				</a>
			</li>
			<li>
				<a href="${createLink(controller: 'listing', action:'equipmentsWithPreventionPlan')}"
					class="tooltip" title="${message(code:'default.equipments.with.prevention.label')}">
					<g:message code="default.equipments.with.prevention.label"/>
				</a>
			</li>
    </ul>
  </div>
  <a class='v-tabs-dynav-scroll-left' href='#' id='js-scroll-left'></a>
</div>