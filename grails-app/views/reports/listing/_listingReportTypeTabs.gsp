<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<ul id='js-top-tabs' class="v-tabs-nav left">
  <li><a class="${reportType == ReportType.INVENTORY?'active':''}" id="#js-inventory"
        href="${createLink(controller:'listing', action:'generalEquipmentsListing')}">
    ${message(code:'reports.type.inventory')}</a></li>
  <li><a class="${reportType == ReportType.CORRECTIVE?'active':''}" id="#js-corrective"
        href="${createLink(controller:'listing', action:'generalWorkOrdersListing')}">
    ${message(code:'reports.type.corrective')}</a></li>
  <li><a class="${reportType == ReportType.PREVENTIVE?'active':''}" id="#js-preventive"
        href="${createLink(controller:'listing', action:'generalPreventiveOrdersListing')}">
    ${message(code:'reports.type.preventive')}</a></li>
  <li><a class="${reportType == ReportType.SPAREPARTS?'active':''}" id="#js-spare-parts"
        href="${createLink(controller:'listing', action:'generalSparePartsListing')}">
    ${message(code:'reports.type.spareParts')}</a></li>
</ul>