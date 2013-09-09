<div>
  <ul class="v-tabs-nested">

    <!-- highest -->
    <g:if test="${indicatorItem.highestComparisonValueItems != null && !indicatorItem.highestComparisonValueItems.isEmpty()}">
      <g:each in="${indicatorItem.highestComparisonValueItems}" var="compItem">
        <li class="v-tabs-row">
          <span class="v-tabs-name">${compItem.facility}</span>
          <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
        <g:if test="${compItem.unit=='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
        </g:if>
        <g:if test="${compItem.unit!='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="###,##0"/> ${compItem.unit}</span>
        </g:if>
        </li>
      </g:each>
      <li class="v-tabs-row">
        <span class="v-tabs-name"> ... </span>
      </li>
    </g:if>

    <!-- higher -->
    <g:if test="${indicatorItem.higherComparisonValueItems != null && !indicatorItem.higherComparisonValueItems.isEmpty()}">
      <g:each in="${indicatorItem.higherComparisonValueItems}" var="compItem">
        <li class="v-tabs-row">
          <span class="v-tabs-name">${compItem.facility}</span>
          <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
        <g:if test="${compItem.unit=='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
        </g:if>
        <g:if test="${compItem.unit!='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="###,##0"/> ${compItem.unit}</span>
        </g:if>
        </li>
      </g:each>
    </g:if>

    <!-- this -->
    <li class="v-tabs-row" style="font-size:15px;font-weight:bold;color:#258CD5">
      <span class="v-tabs-name">${indicatorItem.locationNames}</span>
      <span class="v-tabs-formula" style="background: ${indicatorItem.color}"  original-title="${indicatorItem.color}">${indicatorItem.color}</span>
      <g:if test="${indicatorItem.unit=='%'}">
        <span class="v-tabs-value"><g:formatNumber number="${indicatorItem.value}" format="0%"/></span>
      </g:if>
      <g:if test="${indicatorItem.unit!='%'}">
        <span class="v-tabs-value"><g:formatNumber number="${indicatorItem.value}" format="###,##0"/>${indicatorItem.unit}</span>
      </g:if>
    </li>

    <!-- lower -->
    <g:if test="${indicatorItem.lowerComparisonValueItems != null && !indicatorItem.lowerComparisonValueItems.isEmpty()}">
      <g:each in="${indicatorItem.lowerComparisonValueItems}" var="compItem">
        <li class="v-tabs-row">
          <span class="v-tabs-name">${compItem.facility}</span>
          <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
        <g:if test="${compItem.unit=='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
        </g:if>
        <g:if test="${compItem.unit!='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="###,##0"/> ${compItem.unit}</span>
        </g:if>
        </li>
      </g:each>
    </g:if>

    <!-- lowest -->
    <g:if test="${indicatorItem.lowestComparisonValueItems != null && !indicatorItem.lowestComparisonValueItems.isEmpty()}">
      <li class="v-tabs-row">
        <span class="v-tabs-name"> ... </span>
      </li>
      <g:each in="${indicatorItem.lowestComparisonValueItems}" var="compItem">
        <li class="v-tabs-row">
          <span class="v-tabs-name">${compItem.facility}</span>
          <span class="v-tabs-formula" style="background: ${compItem.color}"  original-title="${compItem.color}">${compItem.color}</span>
        <g:if test="${compItem.unit=='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="0%"/></span>
        </g:if>
        <g:if test="${compItem.unit!='%'}">
          <span class="v-tabs-value"><g:formatNumber number="${compItem.value}" format="###,##0"/> ${compItem.unit}</span>
        </g:if>
        </li>
      </g:each>
    </g:if>
  </ul>
</div>