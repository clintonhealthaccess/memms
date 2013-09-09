<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible" %>
<%@ page import="org.chai.memms.util.Utils" %>
<div class="v-tabs-criteria">

  %{-- customized but unsaved listing --}%
  <g:if test="${customizedReportName != null && !customizedReportName.empty}">
    <h1>${customizedReportName}</h1>
    <a href="${createLink(action:'saveCustomizedReport', params: params)}">Save</a>
  </g:if>
  %{-- customized saved listing --}%
  <g:elseif test="${savedReport != null}">
    <h1>${savedReport.reportName}</h1>
  </g:elseif>
  %{-- predefined listing --}%
  <g:else>
    <h1>${reportName}</h1>
  </g:else>

  <ul>
    <li>
       <span><g:message code="reports.type.label"/>:</span>
      <a href="#">${message(code:'reports.type.'+reportType?.reportType)}</a>
    </li>

    <li>
      <span><g:message code="reports.subType.label"/>:</span>
      <a href="#">${message(code:'reports.subType.'+reportSubType?.reportSubType)}</a>
    </li>
  </ul>
  
  <ul>
    <g:if test="${savedReport != null || (customizedReportName != null && !customizedReportName.empty)}">
      <li>
        <span>Report Filter Summary:</span>
        <a href="#"><g:message code="reports.dataLocation"/> = 
          ${dataLocations?.size()}</a>,
        <a href="#"><g:message code="reports.department"/> = 
          ${departments?.size()}</a>,
        <a href="#"><g:message code="reports.equipmentType"/> = 
          ${equipmentTypes?.size()}</a>,
        <a href="#"><g:message code="reports.cost"/> = 
          ${fromCost?:message(code:'reports.filters.none')} - 
          ${toCost?:message(code:'reports.filters.none')}</a>,
        <a href="#"><g:message code="reports.currency"/> = 
          ${costCurrency?:message(code:'reports.filters.none')}</a>,

        <g:if test="${reportSubType == ReportSubType.WORKORDERS}">
          <a href="#"><g:message code="reports.preventive.workOrders.whoIsResponsible"/> =   
            <g:if test="${whoIsResponsible == null || whoIsResponsible.empty}">
              ${message(code:'reports.filters.none')},
            </g:if>
            <g:else>
              <g:each in="${PreventionResponsible.values() - PreventionResponsible.NONE}" var="statusEnum">
                <g:if test="${whoIsResponsible?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.workOrderStatus"/> =   
            <g:if test="${workOrderStatus == null || workOrderStatus.empty}">
              ${message(code:'reports.filters.none')},
            </g:if>
            <g:else>
              <g:each in="${PreventiveOrderStatus.values()}" var="statusEnum">
                <g:if test="${workOrderStatus?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.workOrderPeriod"/> =   
            ${fromWorkOrderPeriod?:message(code:'reports.filters.none')} - 
            ${toWorkOrderPeriod?:message(code:'reports.filters.none')}</a>,        
        </g:if>
      </li>
    </g:if>
  </ul>
</div>