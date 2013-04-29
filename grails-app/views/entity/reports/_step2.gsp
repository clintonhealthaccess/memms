<div class="dialog-form step-2" id='js-step-2'>
  <!-- Step 2 -->
%{--   <p>ReportType: ${reportType}</p>
  <p>ReportSubType: ${reportSubType}</p> --}%
  <h2>Apply filters<span class="right">Step <b>2</b> of <b>4</b></span></h2>
  <p>Type: Inventory > List of inventory</p>
  <g:formRemote name="formRemoteStep2" url="[action:'step3', params: params]" update="dialog-form"
    onSuccess="customizedlisting_init();">
    <fieldset>
      <ul>
        <li>
          <label>Facility</label>
          <select multiple style="width:384px;" class="chzn-select">
            <option>Please select</option>
            <option>Please select</option>
            <option>Please select</option>
            <option>Please select</option>
            <option>Please select</option>
          </select>
          <input type='checkbox' class='js-select-all'><span>Select all</span>
        </li>
        <li>
          <label>Department</label>
          <select multiple style="width:384px;" class="chzn-select"><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option></select>
        <input type='checkbox' class='js-select-all'><span>Select all</span>
        </li>
        <li>
          <label>Equipment type</label>
          <select style="width:384px;" class="chzn-select"><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option></select>
      </li>
        <li>
          <label>Equipment status</label>
          <select style="width:384px;" class="chzn-select"><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option></select>
          <input type="checkbox" class="no-margin"><span>Show only obsolete equipment</span>
        </li>
        <li>
          <label for="report_type">Acquisition period</label>
          <input class="js-date-picker date-picker idle-field" /> <span class="dash">-</span> <input class="js-date-picker date-picker idle-field" />
        </li>
        <li>
          <label for="report_type">Which cost</label>
          <input class="js-date-picker date-picker idle-field" /> <span class="dash">-</span> <input class="js-date-picker date-picker idle-field" />
          <input type="checkbox"><span>Include equipment without cost</span>
        </li>
        <li>
          <label>Warranty</label>
          <input type="checkbox"><span>Under warranty</span>
        </li>
      </ul>
    </fieldset>
    <g:remoteLink class="ui-widget-previous left btn gray medium" action="step1" 
      update="dialog-form" params="${params}" onSuccess="customizedlisting_init();">
      Previous Step</g:remoteLink>
    <a href="#" class="ui-widget-next right btn" id='js-next-step-2'>Next step</a>
  </g:formRemote>
  <!-- end step 2 -->
</div>