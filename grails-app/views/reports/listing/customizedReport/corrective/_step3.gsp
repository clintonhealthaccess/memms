<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<ul class="onecol-checkboxes left">
	<li><g:checkBox name="reportTypeOptions" value="travelTime" checked="false" /><span>${message(code:'listing.report.corrective.travel.time.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="workTime" checked="false" /><span>${message(code:'listing.report.corrective.work.time.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="estimatedCost" checked="false" /><span>${message(code:'listing.report.corrective.estimated.cost.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="departmentRequestedOrder" checked="false" />
		<span>${message(code:'listing.report.corrective.department.order.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="reasonsEquipmentFailure" checked="false" />
		<span>${message(code:'listing.report.corrective.failure.reason.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="listPerformedActions" checked="false" />
		<span>${message(code:'listing.report.corrective.performed.actions.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="description" checked="false" /><span>${message(code:'listing.report.corrective.problem.description.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="dateOfEvent" checked="false" /><span>${message(code:'listing.report.corrective.event.date.label')}</span></li>
</ul>

<ul class="onecol-checkboxes right">
	<li><g:checkBox name="reportTypeOptions" value="location" /><span>${message(code:'listing.report.corrective.location.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="code" /><span>${message(code:'listing.report.corrective.code.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="serialNumber" /><span>${message(code:'listing.report.corrective.serial.number.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="equipmentType" /><span>${message(code:'listing.report.corrective.type.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="model" /><span>${message(code:'listing.report.corrective.model.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="manufacturer" /><span>${message(code:'listing.report.corrective.manufacturer.label')}</span></li>
	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
		<li><g:checkBox name="reportTypeOptions" value="previousStatus" /><span>${message(code:'listing.report.equipment.previous.status.label')}</span></li>
	</g:if>
	<li><g:checkBox name="reportTypeOptions" value="currentStatus" /><span>${message(code:'listing.report.equipment.current.status.label')}</span></li>
	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
		<li><g:checkBox name="reportTypeOptions" value="statusChanges" /><span>${message(code:'reports.statusChange')}</span></li>
	</g:if>
</ul>