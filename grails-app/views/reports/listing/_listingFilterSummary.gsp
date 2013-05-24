<div class="v-tabs-criteria">
  <ul class="left">
    <li>
      <span>Report Type:</span>
      <a href="#">${message(code:'reports.type.'+reportType?.reportType)}</a>
    </li>
    <li>
      <span>Report Subtype:</span>
      <a href="#">${message(code:'reports.subType.'+reportSubType?.reportSubType)}</a>
    </li>
    <g:if test="${customizedReportName != null && !customizedReportName.empty}">
      <li>
        <span>Report Name:</span>
        <a href="#">${customizedReportName}</a>
      </li>
    </g:if>
    <g:else>
      <li>
        <span>Report Name:</span>
        <a href="#">TODO ${actionName}</a>
      </li>
    </g:else>
%{--<li>
      <span>Filters:</span>
      <a href="#">There are <a href="#" id='js-filters-toggle' class="tooltip">133</a> filters applied</a>
    </li> --}%
  </ul>
</div>