<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<li><g:checkBox name="correctiveOptions" value="serialNumber" /><span>Serial Number</span></li>
<li><g:checkBox name="correctiveOptions" value="equipmentType" /><span>Equipment Type</span></li>
<li><g:checkBox name="correctiveOptions" value="workOrderCode" /><span>Work Order Code</span></li>

<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
      <li><g:checkBox name="correctiveOptions" value="statusChanges" /><span>Status Changes</span></li>
</g:if>

<li><g:checkBox name="preventiveOptions" value="manufacturerName" checked="false" /><span>Manufacturer Name</span></li>
<li><g:checkBox name="preventiveOptions" value="model" checked="false" /><span>Model</span></li>
<li><g:checkBox name="preventiveOptions" value="location" checked="false" /><span>Location</span></li>
<li><g:checkBox name="preventiveOptions" value="workOrderStatus" checked="false" /><span>Work Order Status</span></li>
<li><g:checkBox name="preventiveOptions" value="scheduleNextInterventions" checked="false" /><span>Schedule Next Interventions</span></li>
<li><g:checkBox name="preventiveOptions" value="percentageInterventionsDone" checked="false" /><span>% Interventions Done</span></li>
<li><g:checkBox name="preventiveOptions" value="recurrencePeriod" checked="false" /><span>Recurrence Period</span></li>
<li><g:checkBox name="preventiveOptions" value="startDate" checked="false" /><span>Start Date</span></li>
<li><g:checkBox name="preventiveOptions" value="responsible" checked="false" /><span>Responsible</span></li>
<li><g:checkBox name="preventiveOptions" value="actionName" checked="false" /><span>Action Name</span></li>
<li><g:checkBox name="preventiveOptions" value="actionDescription" checked="false" /><span>Action Description</span></li>