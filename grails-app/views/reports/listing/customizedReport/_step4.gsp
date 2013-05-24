<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<div class="dialog-form step-4" id='js-step-4'>
  <!-- Step 4 -->
  <h2>Select name for customized report<span class="right"><g:message code="reports.step" args="['4','4']"/></span></h2>
  <p>${message(code:'reports.type.label')}: <b>${message(code:'reports.type.'+reportType.reportType)}</b> > ${message(code:'reports.subType.label')}: <b>${message(code:'reports.subType.'+reportSubType.reportSubType)}</b></p>
  <g:form name="formStep4Next" url="[action:'customizedListing', params: step4Params]" update="dialog-form">
    <fieldset>
      <ul>
        <li>
          <label for="customizedReportName">Name of Report:</label>
          <input name="customizedReportName" type="text" />
        </li>
        <li>
          <label for="customizedReportSave">Save Report:</label>
          <input name="customizedReportSave" type="checkbox" />
        </li>
      </ul>
    </fieldset>
  </g:form>
  <g:formRemote name="formRemoteStep4Prev" url="[action:'step3', params:params]" update="dialog-form"
    onSuccess="customizedlisting_init(3)">
  </g:formRemote>
  <a href="#" class="ui-widget-previous left btn gray medium" id='js-prev-step-4'><g:message code="reports.step.previous"/></a>
  <a href="#" class="ui-widget-next right btn" id='js-next-step-4'><g:message code="reports.step.submit"/></a>
  <!-- end step 4 -->
</div>