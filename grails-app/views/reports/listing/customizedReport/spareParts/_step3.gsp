<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<ul class="onecol-checkboxes left">
	<li><g:checkBox name="spartPartsOptions" value="sparePartType" /><span>Spare Part Type</span></li>
	<li><g:checkBox name="spartPartsOptions" value="locationOfStock" /><span>Location of Stock</span></li>
	<li><g:checkBox name="spartPartsOptions" value="quantityInStock" /><span>Quantity in Stock</span></li>

	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
	  <li><g:checkBox name="inventoryOptions" value="statusChanges" /><span>Status Changes</span></li>
	</g:if>
</ul>
<ul class="onecol-checkboxes right">
	<g:if test="${[ReportSubType.INVENTORY,ReportSubType.STATUSCHANGES].contains(reportSubType)}">
	<li><g:checkBox name="spartPartsOptions" value="status" checked="false" /><span>Status</span></li>
	<li><g:checkBox name="spartPartsOptions" value="model" checked="false" /><span>Model</span></li>
	<li><g:checkBox name="spartPartsOptions" value="manufacturer" checked="false" /><span>Manufacturer</span></li>
	<li><g:checkBox name="spartPartsOptions" value="equipmentCode" checked="false" /><span>Equipment Code Associated With Spare Part</span></li>
	<li><g:checkBox name="spartPartsOptions" value="cost" checked="false" /><span>Cost</span></li>
	<li><g:checkBox name="spartPartsOptions" value="warrantyPeriodRemaining" checked="false" /><span>Warranty Period Remaining</span></li>
	<li><g:checkBox name="spartPartsOptions" value="discontinuedDate" checked="false" /><span>Discontinued Date</span></li>
	</g:if>

	<g:if test="${reportSubType == ReportSubType.STOCKOUT}">
		<li><g:checkBox name="spartPartsOptions" value="forecastedStockOut" /><span>Forecasted Stock Out</span></li>
	</g:if>

<g:if test="${[ReportSubType.INVENTORY,ReportSubType.STATUSCHANGES].contains(reportSubType)}">
    <li><g:checkBox name="spartPartsOptions" value="status" /><span>Status</span></li>
    <li><g:checkBox name="spartPartsOptions" value="model" /><span>Model</span></li>
    <li><g:checkBox name="spartPartsOptions" value="manufacturer" /><span>Manufacturer</span></li>
    <li><g:checkBox name="spartPartsOptions" value="equipmentCode" /><span>Equipment Code Associated With Spare Part</span></li>
    <li><g:checkBox name="spartPartsOptions" value="cost" /><span>Cost</span></li>
    <li><g:checkBox name="spartPartsOptions" value="warrantyPeriodRemaining" checked="false" /><span>Warranty Period Remaining</span></li>
    <li><g:checkBox name="spartPartsOptions" value="discontinuedDate" checked="false" /><span>Discontinued Date</span></li>
</g:if>

<g:if test="${reportSubType == ReportSubType.STOCKOUT}">
    <li><g:checkBox name="spartPartsOptions" value="forecastedStockOut" /><span>Forecasted Stock Out</span></li>
</g:if>

<g:if test="${reportSubType == ReportSubType.USERATE}">
    <li><g:checkBox name="spartPartsOptions" value="useRate" /><span>Use Rate</span></li>
</g:if>
</ul>
