<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<ul class="onecol-checkboxes left">
	<li><g:checkBox name="reportTypeOptions" value="manufacturer" checked="false" /><span>${message(code:'listing.report.equipment.manufacturer.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="supplier" checked="false" /><span>${message(code:'listing.report.spare.part.supplier.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="purchaser" checked="false" /><span>${message(code:'listing.report.spare.part.purchaser.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="cost" checked="false" /><span>${message(code:'listing.report.spare.part.cost.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="deliveryDate" checked="false" /><span>${message(code:'listing.report.spare.part.delivery.date.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="discontinuedDate" checked="false" /><span>${message(code:'listing.report.spare.part.discontinued.date.label')}</span></li>
</ul>

<ul class="onecol-checkboxes right">
	<li><g:checkBox name="reportTypeOptions" value="sparePartType" /><span>${message(code:'listing.report.spare.part.type.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="locationOfStock" /><span>${message(code:'listing.report.spare.part.stock.location.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="purchaseDate" /><span>${message(code:'listing.report.spare.part.purchase.date.label')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="currentStatus" /><span>${message(code:'listing.report.spare.part.current.status.label')}</span></li>
	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
	  <li><g:checkBox name="reportTypeOptions" value="statusChanges" /><span>${message(code:'listing.report.spare.part.status.changes.label')}</span></li>
	</g:if>
	<li><g:checkBox name="reportTypeOptions" value="initialQuantity" /><span>${message(code:'listing.report.spare.part.initial.quantity')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="quantityInStock" /><span>${message(code:'listing.report.spare.part.in.stock.quantity')}</span></li>
	<li><g:checkBox name="reportTypeOptions" value="usedQuantity" /><span>${message(code:'listing.report.spare.part.used.quantity')}</span></li>
	<g:if test="${reportSubType == ReportSubType.STOCKOUT}">
		<li><g:checkBox name="reportTypeOptions" value="forecastedStockOut" /><span>${message(code:'listing.report.spare.part.forecasted.stock.out.label')}</span></li>
	</g:if>
	<g:if test="${reportSubType == ReportSubType.USERATE}">
	    <li><g:checkBox name="reportTypeOptions" value="useRate" /><span>${message(code:'listing.report.spare.part.use.rate.label')}</span></li>
	</g:if>
</ul>
