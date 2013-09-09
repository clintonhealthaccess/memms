<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<ul class="onecol-checkboxes left">
	<li><g:checkBox name="reportTypeOptions" value="obsolete" checked="false" /><span>${message(code:'listing.report.equipment.obsolete.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="manufacturer" checked="false" /><span>${message(code:'listing.report.equipment.manufacturer.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="supplier" checked="false" /><span>${message(code:'listing.report.equipment.supplier.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="purchaser" checked="false" /><span>${message(code:'listing.report.equipment.purchaser.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="acquisitionDate" checked="false" /><span>${message(code:'listing.report.equipment.aquisition.date.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="cost" checked="false" /><span>${message(code:'listing.report.equipment.aquisition.cost.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="warrantyProvider" checked="false" /><span>${message(code:'listing.report.equipment.warranty.provider.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="warrantyPeriodRemaining" checked="false" />
		<span>${message(code:'listing.report.equipment.warranty.period.remaining.label')}</span></li>
</ul>

<ul class="onecol-checkboxes right">
	<li><g:checkBox name="reportTypeOptions" value="location" /><span>${message(code:'listing.report.equipment.location.label')} </span></li>
	<li><g:checkBox name="reportTypeOptions" value="code" /><span>${message(code:'listing.report.equipment.code.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="serialNumber" /><span>${message(code:'listing.report.equipment.serial.number.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="equipmentType" /><span>${message(code:'listing.report.equipment.type.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="model" /><span>${message(code:'listing.report.equipment.model.label')}</span></li>
	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
		<li><g:checkBox name="reportTypeOptions" value="previousStatus" /><span>${message(code:'listing.report.equipment.previous.status.label')}</span></li>
	</g:if>
	<li><g:checkBox name="reportTypeOptions" value="currentStatus" /><span>${message(code:'listing.report.equipment.current.status.label')}</span></li>
	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
		<li><g:checkBox name="reportTypeOptions" value="statusChanges" /><span>${message(code:'reports.statusChange')}</span></li>
	</g:if>
	<li><g:checkBox name="reportTypeOptions" value="currentValue" /><span>${message(code:'listing.report.equipment.current.value.label')}</span></li>
</ul>