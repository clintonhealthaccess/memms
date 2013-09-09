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
			<a href="${createLink(params:[reportType: ReportType.CORRECTIVE, reportSubType:ReportSubType.WORKORDERS])}"
				class="tooltip" title="${message(code:'default.all.work.order.label')}">
				<g:message code="default.all.work.order.label"/>
			</a>
  		</li>
  		<li>
  			<a href="${createLink(controller: 'listing', action:'lastMonthWorkOrders')}"
  				class="tooltip" title="${message(code:'default.work.order.last.month.label')}">
  				<g:message code="default.work.order.last.month.label"/>
  			</a>
  		</li>
  		<li>
  			<a href="${createLink(controller: 'listing', action:'workOrdersEscalatedToMMC')}"
  				class="tooltip" title="${message(code:'default.work.order.escalated.to.mmc.label')}">
  				<g:message code="default.work.order.escalated.to.mmc.label"/>
  			</a>
  		</li>
  		<li>
  			<a href="${createLink(controller: 'listing', action:'lastYearClosedWorkOrders')}"
          class="tooltip" title="${message(code:'default.work.order.closed.last.year.label')}">
  				<g:message code="default.work.order.closed.last.year.label"/>
  			</a>
  		</li>
  	</ul>

  </div>
  <a class='v-tabs-dynav-scroll-left' href='#' id='js-scroll-left'></a>
</div>