<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<ul class="onecol-checkboxes left">
	<li><g:checkBox name="correctiveOptions" value="location" /><span>Location</span></li>
	<li><g:checkBox name="correctiveOptions" value="code" /><span>Code</span></li>
	<li><g:checkBox name="correctiveOptions" value="serialNumber" /><span>Serial Number</span></li>
	<li><g:checkBox name="correctiveOptions" value="equipmentType" /><span>Equipment Type</span></li>
	<li><g:checkBox name="correctiveOptions" value="model" /><span>Model</span></li>
	<li><g:checkBox name="correctiveOptions" value="manufacturer" /><span>Manufacturer</span></li>

	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
		<li><g:checkBox name="correctiveOptions" value="statusChanges" /><span>Status Changes</span></li>
	</g:if>
	<li><g:checkBox name="correctiveOptions" value="currentStatus" /><span>Current Status</span></li>
</ul>

<ul class="onecol-checkboxes right">
	<li><g:checkBox name="correctiveOptions" value="travelTime" checked="false" /><span>Travel Time</span></li>
	<li><g:checkBox name="correctiveOptions" value="workTime" checked="false" /><span>Work Time</span></li>
	<li><g:checkBox name="correctiveOptions" value="estimatedCost" checked="false" /><span>Estimated Cost</span></li>
	<li><g:checkBox name="correctiveOptions" value="departmentRequestedOrder" checked="false" />
		<span>Department Requested Order</span></li>
	<li><g:checkBox name="correctiveOptions" value="reasonsEquipmentFailure" checked="false" />
		<span>Reasons Equipment Failure</span></li>
	<li><g:checkBox name="correctiveOptions" value="listPerformedActions" checked="false" />
		<span>List Performed Actions</span></li>
	<li><g:checkBox name="correctiveOptions" value="description" checked="false" /><span>Description of Problem</span></li>
	<li><g:checkBox name="correctiveOptions" value="dateOfEvent" checked="false" /><span>Date of Event</span></li>
</ul>