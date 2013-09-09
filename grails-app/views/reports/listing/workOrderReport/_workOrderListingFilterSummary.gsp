<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus" %>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.WorkOrderStatusChange" %>
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
          <g:if test="${fromCost!=null || toCost!=null}">
            ${fromCost?:message(code:'reports.filters.none')} - 
            ${toCost?:message(code:'reports.filters.none')}</a>,
          </g:if>
          <g:else>
            <g:message code="reports.filters.all"/>,
          </g:else>
        <a href="#"><g:message code="reports.currency"/> = 
          ${costCurrency?:message(code:'reports.filters.all')}</a>,

        <g:if test="${reportSubType == ReportSubType.WORKORDERS}">
          <a href="#"><g:message code="reports.workOrderStatus"/> =   
            <g:if test="${workOrderStatus == null || workOrderStatus.empty}">
              ${message(code:'reports.filters.all')},
            </g:if>
            <g:else>
              <g:each in="${OrderStatus.values() - OrderStatus.NONE}" var="statusEnum">
                <g:if test="${workOrderStatus?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.workOrderPeriod"/> =
            <g:if test="${fromWorkOrderPeriod!=null || toWorkOrderPeriod!=null}">
              ${Utils.formatDate(fromWorkOrderPeriod)?:message(code:'reports.filters.none')} - 
              ${Utils.formatDate(toWorkOrderPeriod)?:message(code:'reports.filters.none')}</a>,
            </g:if>
            <g:else>
              <g:message code="reports.filters.all"/>,
            </g:else>
          <a href="#"><g:message code="reports.inventory.warranty.label"/> = 
            ${warranty?'&radic;':message(code:'reports.filters.none')}</a>
        </g:if>

        <g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
          <a href="#"><g:message code="reports.statusChanges"/> = 
            <g:if test="${statusChanges == null || statusChanges.empty}">
              ${message(code:'reports.filters.all')},
            </g:if>
            <g:else>
              <g:each in="${WorkOrderStatusChange.values()}" var="statusEnum">
                <g:if test="${statusChanges?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.statusChangesPeriod"/> = 
            <g:if test="${fromStatusChangesPeriod!=null || toStatusChangesPeriod!=null}">
              ${Utils.formatDate(fromStatusChangesPeriod)?:message(code:'reports.filters.none')} - 
              ${Utils.formatDate(toStatusChangesPeriod)?:message(code:'reports.filters.none')}</a>,
            </g:if>
            <g:else>
              <g:message code="reports.filters.all"/>,
            </g:else>
          <a href="#"><g:message code="reports.inventory.warranty.label"/> = 
            ${warranty?'&radic;':message(code:'reports.filters.none')}</a>
        </g:if>

      </li>
    </g:if>
  </ul>
</div>