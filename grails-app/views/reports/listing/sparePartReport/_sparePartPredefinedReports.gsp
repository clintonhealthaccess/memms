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
			<a href="${createLink(params:[reportType: ReportType.SPAREPARTS, reportSubType:ReportSubType.INVENTORY])}"
				class="tooltip" title="${message(code:'default.all.spare.parts.label')}">
				<g:message code="default.all.spare.parts.label"/>
			</a>
			</li>
			<li>
				<a href="${createLink(controller: 'listing', action:'pendingOrderSparePartsListing')}"
					class="tooltip" title="${message(code:'default.pending.order.spare.parts.label')}">
					<g:message code="default.pending.order.spare.parts.label"/>
				</a>
			</li>
    </ul>
  </div>
  <a class='v-tabs-dynav-scroll-left' href='#' id='js-scroll-left'></a>
</div>