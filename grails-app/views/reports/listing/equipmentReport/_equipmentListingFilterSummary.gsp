<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.chai.memms.inventory.EquipmentStatus.Status" %>
<%@ page import="org.chai.memms.inventory.EquipmentStatus.EquipmentStatusChange" %>
<%@ page import="org.chai.memms.util.Utils" %>
<div class="v-tabs-criteria">

  %{-- customized but unsaved listing --}%
  <g:if test="${customizedReportName != null && !customizedReportName.empty}">
    <h1>${customizedReportName}</h1>
    <a class="btn right gray medium push-r-15 inline-save save-custom-report" href="${createLink(action:'saveCustomizedReport', params: params)}">Save</a>
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

        <g:if test="${reportSubType == ReportSubType.INVENTORY}">
          <a href="#"><g:message code="reports.inventory.inventory.equipmentStatus"/> = 
            <g:if test="${equipmentStatus == null || equipmentStatus.empty}">
              ${message(code:'reports.filters.all')},
            </g:if>
            <g:else>
              <g:each in="${Status.values() - Status.NONE}" var="statusEnum">
                <g:if test="${equipmentStatus?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.inventory.inventory.acquisitionPeriod"/> =   
             <g:if test="${fromAcquisitionPeriod!=null || toAcquisitionPeriod!=null}">
              ${Utils.formatDate(fromAcquisitionPeriod)?:message(code:'reports.filters.none')} - 
              ${Utils.formatDate(toAcquisitionPeriod)?:message(code:'reports.filters.none')}</a>,
            </g:if>
            <g:else>
              <g:message code="reports.filters.all"/>,
            </g:else>
          <a href="#"><g:message code="reports.inventory.inventory.noAcquisitionPeriod.label"/> = 
            ${noAcquisitionPeriod?'&radic;':message(code:'reports.filters.none')}</a>,          
          <a href="#"><g:message code="reports.inventory.inventory.obsolete.label" /> = 
            ${obsolete?'&radic;':message(code:'reports.filters.none')}</a>,
          <a href="#"><g:message code="reports.inventory.warranty.label"/> = 
            ${warranty?'&radic;':message(code:'reports.filters.none')}</a>
        </g:if>

        <g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
          <a href="#"><g:message code="reports.statusChanges"/> = 
            <g:if test="${statusChanges == null || statusChanges.empty}">
              ${message(code:'reports.filters.all')},
            </g:if>
            <g:else>
              <g:each in="${EquipmentStatusChange.values()}" var="statusEnum">
                <g:if test="${statusChanges?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.statusChangesPeriod"/> =   
            <g:if test="${fromStatusChangesPeriod!=null || toStatusChangesPeriod!=null}">
              ${Utils.formatDate(fromStatusChangesPeriod)?:message(code:'reports.filters.none')} - 
              ${Utils.formatDate(toStatusChangesPeriod)?:message(code:'reports.filters.none')}
            </g:if>
            <g:else>
              <g:message code="reports.filters.all"/>,
            </g:else>
          </a>
        </g:if>
      </li>
    </g:if>
  </ul>
</div>