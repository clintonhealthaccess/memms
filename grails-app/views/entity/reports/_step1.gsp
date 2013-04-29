<div class="dialog-form step-1" id='js-step-1'>
  <!-- Step 1 -->
%{--   <p>ReportType: ${reportType}</p>
  <p>ReportSubType: ${reportSubType}</p> --}%
  <h2>Select the type of customized report<span class="right">Step <b>1</b> of <b>4</b></span></h2>
  <g:formRemote name="formRemoteStep1" url="[action:'step2']" update="dialog-form" 
    onSuccess="customizedlisting_init();">
    <fieldset>
      <ul>
        <li>
          <div id="custom-report-type">
            <label for="report_type">Report Type</label>
            <select name="reportType" class="js-custom-report-type" 
              onchange="${remoteFunction(action: 'customizedReportSubType', update: 'custom-report-subtype', params: '\'reportType=\' + this.value')}">
              <option value="0">Please select</option>
              <option value="inventory" ${reportType == 'inventory'?'selected':''}>
                <g:message code="header.navigation.inventory"/></option>
              <option value="corrective" ${reportType == 'corrective'?'selected':''}>
                <g:message code="header.navigation.corrective.maintenance"/></option>
              <option value="preventive" ${reportType == 'preventive'?'selected':''}>
                <g:message code="header.navigation.preventive.maintenance"/></option>
              <option value="spareParts" ${reportType == 'spareParts'?'selected':''}>
                <g:message code="header.navigation.sparePart"/></option>
            </select>
          </div>
        </li>
        <li>
          <div id="custom-report-subtype">
            <g:render template="/entity/reports/customizedReportSubType" />
          </div>
        </li>
      </ul>
    </fieldset>
    <a href="#" class="ui-widget-next right btn" id='js-next-step-1'>Next step</a>
  </g:formRemote>
  <!-- end step 1 -->
</div>