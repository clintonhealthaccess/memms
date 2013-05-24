<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<div class="dialog-form step-3" id='js-step-3'>
  <!-- Step 3 -->
  <h2><g:message code="reports.select.optional.information"/><span class="right"><g:message code="reports.step" args="['3','4']"/></span></h2>
  <p>${message(code:'reports.type.label')}: <b>${message(code:'reports.type.'+reportType.reportType)}</b> > ${message(code:'reports.subType.label')}: <b>${message(code:'reports.subType.'+reportSubType.reportSubType)}</b></p>
  <g:formRemote name="formRemoteStep3Next" url="[action:'step4', params: step3Params]" update="dialog-form"
    onSuccess="customizedlisting_init(4)">
    <fieldset>
      <g:if test="${reportType == ReportType.INVENTORY}">
        <g:render template="/reports/listing/customizedReport/inventory/step3"/>
      </g:if>      
      <g:if test="${reportType == ReportType.CORRECTIVE}">
        <g:render template="/reports/listing/customizedReport/corrective/step3"/>
      </g:if>
      <g:if test="${reportType == ReportType.PREVENTIVE}">
        <g:render template="/reports/listing/customizedReport/preventive/step3"/>
      </g:if>
      <g:if test="${reportType == ReportType.SPAREPARTS}">
        <g:render template="/reports/listing/customizedReport/spareParts/step3"/>
      </g:if>
    </fieldset>
  </g:formRemote>
  <g:formRemote name="formRemoteStep3Prev" url="[action:'step2', params:step3Model]" update="dialog-form"
    onSuccess="customizedlisting_init(2)">
  </g:formRemote>
  <a href="#" class="ui-widget-previous left btn gray medium" id='js-prev-step-3'><g:message code="reports.step.previous"/></a>
  <a href="#" class="ui-widget-next right btn" id='js-next-step-3'><g:message code="reports.step.next"/></a>
  <!-- end step 3 -->
</div>