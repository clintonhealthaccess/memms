<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<li><g:checkBox name="correctiveOptions" value="serialNumber" /><span>Serial Number</span></li>
<li><g:checkBox name="correctiveOptions" value="equipmentType" /><span>Equipment Type</span></li>
<li><g:checkBox name="correctiveOptions" value="workOrderCode" /><span>Work Order Code</span></li>

<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
      <li><g:checkBox name="correctiveOptions" value="statusChanges" /><span>Status Changes</span></li>
</g:if>

<li><g:checkBox name="correctiveOptions" value="manufacturerName" checked="false" /><span>Manufacturer Name</span></li>
<li><g:checkBox name="correctiveOptions" value="model" checked="false" /><span>Model</span></li>
<li><g:checkBox name="correctiveOptions" value="location" checked="false" /><span>Location</span></li>
<li><g:checkBox name="correctiveOptions" value="workOrderStatus" checked="false" /><span>Work Order Status</span></li>
<li><g:checkBox name="correctiveOptions" value="workOrderTravelTime" checked="false" /><span>Work Order Travel Time</span></li>
<li><g:checkBox name="correctiveOptions" value="workOrderWorkTime" checked="false" /><span>Work Order Work Time</span></li>
<li><g:checkBox name="correctiveOptions" value="workOrderEstimatedCost" checked="false" /><span>Work Order Estimated Cost</span></li>
<li><g:checkBox name="correctiveOptions" value="departmentRequestedOrder" checked="false" /><span>Department Requested Order</span></li>
<li><g:checkBox name="correctiveOptions" value="reasonsEquipmentFailure" checked="false" /><span>Reasons Equipment Failure</span></li>
<li><g:checkBox name="correctiveOptions" value="listPerformedActions" checked="false" /><span>List Performed Actions</span></li>
<li><g:checkBox name="correctiveOptions" value="descriptionOfProblem" checked="false" /><span>Description of Problem</span></li>
<li><g:checkBox name="correctiveOptions" value="dateOfEvent" checked="false" /><span>Date of Event</span></li>