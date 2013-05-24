<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<ul class="onecol-checkboxes left">
	<li><g:checkBox name="preventiveOptions" value="location" /><span>Location</span></li>
	<li><g:checkBox name="preventiveOptions" value="code" /><span>Code</span></li>
	<li><g:checkBox name="preventiveOptions" value="serialNumber" /><span>Serial Number</span></li>
	<li><g:checkBox name="preventiveOptions" value="equipmentType" /><span>Equipment Type</span></li>
	<li><g:checkBox name="preventiveOptions" value="model" /><span>Model</span></li>
	<li><g:checkBox name="preventiveOptions" value="manufacturer" /><span>Manufacturer</span></li>

	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
		<li><g:checkBox name="preventiveOptions" value="statusChanges" /><span>Status Changes</span></li>
	</g:if>
	<li><g:checkBox name="preventiveOptions" value="currentStatus" /><span>Current Status</span></li>
</ul>

<ul class="onecol-checkboxes right">
	<li><g:checkBox name="preventiveOptions" value="scheduleNextInterventions" checked="false" />
		<span>Schedule Next Interventions</span></li>
	<li><g:checkBox name="preventiveOptions" value="percentageInterventionsDone" checked="false" />
		<span>% Interventions Done</span></li>
	<li><g:checkBox name="preventiveOptions" value="recurrencePeriod" checked="false" /><span>Recurrence Period</span></li>
	<li><g:checkBox name="preventiveOptions" value="startDate" checked="false" /><span>Start Date</span></li>
	<li><g:checkBox name="preventiveOptions" value="responsible" checked="false" /><span>Responsible</span></li>
	<li><g:checkBox name="preventiveOptions" value="actionName" checked="false" /><span>Action Name</span></li>
	<li><g:checkBox name="preventiveOptions" value="actionDescription" checked="false" /><span>Action Description</span></li>
</ul>