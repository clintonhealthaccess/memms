<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<li><g:checkBox name="inventoryOptions" value="location" /><span>Location</span></li>
<li><g:checkBox name="inventoryOptions" value="code" /><span>Location</span></li>
<li><g:checkBox name="inventoryOptions" value="serialNumber" /><span>Serial Number</span></li>
<li><g:checkBox name="inventoryOptions" value="equipmentType" /><span>Equipment Type</span></li>

<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
	<li><g:checkBox name="inventoryOptions" value="statusChanges" /><span>Status Changes</span></li>
</g:if>

<li><g:checkBox name="inventoryOptions" value="model" /><span>Model</span></li>
<li><g:checkBox name="inventoryOptions" value="status" /><span>Status</span></li>
<li><g:checkBox name="inventoryOptions" value="obsolete" /><span>Obsolete</span></li>
<li><g:checkBox name="inventoryOptions" value="manufacturer" /><span>Manufacturer</span></li>
<li><g:checkBox name="inventoryOptions" value="supplier" /><span>Supplier</span></li>
<li><g:checkBox name="inventoryOptions" value="purchaser" /><span>Purchaser</span></li>

<li><g:checkBox name="inventoryOptions" value="acquisitionDate" checked="false" /><span>Acquisition Date</span></li>
<li><g:checkBox name="inventoryOptions" value="cost" checked="false" /><span>Acquisition Cost</span></li>
<li><g:checkBox name="inventoryOptions" value="warrantyProvider" checked="false" /><span>Warranty Provider</span></li>
<li><g:checkBox name="inventoryOptions" value="warrantyPeriodRemaining" checked="false" /><span>Warranty Period Remaining</span></li>
<li><g:checkBox name="inventoryOptions" value="currentValue" checked="false" /><span>Current Value</span></li>