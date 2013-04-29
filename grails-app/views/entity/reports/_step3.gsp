<div class="dialog-form step-3" id='js-step-3'>
  <!-- Step 3 -->
%{--   <p>ReportType: ${reportType}</p>
  <p>ReportSubType: ${reportSubType}</p> --}%
  <h2>Choose the order<span class="right">Step <b>3</b> of <b>4</b></span></h2>
  <p>Type: Inventory > List of inventory</p>
  <g:formRemote name="formRemoteStep3" url="[action:'step4', params: params]" update="dialog-form"
    onSuccess="customizedlisting_init();">
    <fieldset>
      <ul>
        <li>
          <label for="report_type">Order by</label>
          <select><option>Please select</option></select>
        </li>
      </ul>
    </fieldset>
  </g:formRemote>
  <g:remoteLink class="ui-widget-previous left btn gray medium" action="step2" 
    update="dialog-form" params="${params}" onSuccess="customizedlisting_init();">
    Previous Step</g:remoteLink>
  <a href="#" class="ui-widget-next right btn" id='js-next-step-3'>Next step</a>
  <!-- end step 3 -->
</div>