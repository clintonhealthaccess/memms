<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<div class="dialog-form step-1" id='js-step-1'>
  <!-- Step 1 -->
  <h2><g:message code="reports.select.customized.report"/><span class="right"><g:message code="reports.step" args="['1','4']"/></span></h2>
  <g:formRemote name="formRemoteStep1Next" url="[action:'step2', params: step1Params]" update="dialog-form" 
    onSuccess="customizedlisting_init(2)">
    <fieldset>
      <ul>
        <li>
          <div id="custom-report-type">
            <label for="reportType"><g:message code="reports.type.label"/></label>
            <select name="reportType" class="js-custom-report-type"
              onchange="${remoteFunction(action: 'customizedReportSubType', update: 'custom-report-subtype', params: '\'reportType=\' + this.value')}">
              <option value="${ReportType.INVENTORY}" ${reportType == ReportType.INVENTORY?'selected':''}>
                <g:message code="reports.type.${ReportType.INVENTORY.reportType}"/></option>
              <option value="${ReportType.CORRECTIVE}" ${reportType == ReportType.CORRECTIVE?'selected':''}>
                <g:message code="reports.type.${ReportType.CORRECTIVE.reportType}"/></option>
              <option value="${ReportType.PREVENTIVE}" ${reportType == ReportType.PREVENTIVE?'selected':''}>
                <g:message code="reports.type.${ReportType.PREVENTIVE.reportType}"/></option>
              <option value="${ReportType.SPAREPARTS}" ${reportType == ReportType.SPAREPARTS?'selected':''}>
                <g:message code="reports.type.${ReportType.SPAREPARTS.reportType}"/></option>
            </select>
          </div>
        </li>
        <li>
          <div id="custom-report-subtype">
            <g:render template="/reports/listing/customizedReport/customizedReportSubType"/>
          </div>
        </li>
      </ul>
    </fieldset>
    <a href="#" class="ui-widget-next right btn" id='js-next-step-1'><g:message code="reports.step.next"/></a>
  </g:formRemote>
  <!-- end step 1 -->
</div>