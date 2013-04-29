<div class="dialog-form step-4" id='js-step-4'>
  <!-- Step 4 -->
%{--   <p>ReportType: ${reportType}</p>
  <p>ReportSubType: ${reportSubType}</p> --}%
  <h2>Select information to present<span class="right">Step <b>4</b> of <b>4</b></span></h2>
  <p>Type: Inventory > List of inventory</p>
    <g:formRemote name="formRemoteStep4" url="[action:'customizedListing', params: params]"
    onSuccess="customizedlisting_init();">
    <fieldset>
      <ul class="twocol-checkboxes">
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
        <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
      </ul>
    </fieldset>
  </g:formRemote>
  <g:remoteLink class="ui-widget-previous left btn gray medium" action="step3" 
    update="dialog-form" params="${params}" onSuccess="customizedlisting_init();">
    Previous Step</g:remoteLink>
  <a href="#" class="ui-widget-next right btn" id='js-next-step-4'>Submit</a>
  <!-- end step 4 -->
</div>