<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<ul id='js-top-tabs' class="v-tabs-nav left">
  <li><a class="${reportType == ReportType.INVENTORY?'active':''}" id="#js-inventory"
        href="${createLink(params:[reportType: ReportType.INVENTORY, reportSubType:ReportSubType.INVENTORY])}">
    ${message(code:'reports.type.inventory')}</a></li>
  <li><a class="${reportType == ReportType.CORRECTIVE?'active':''}" id="#js-corrective"
        href="${createLink(params:[reportType: ReportType.CORRECTIVE, reportSubType:ReportSubType.WORKORDERS])}">
    ${message(code:'reports.type.corrective')}</a></li>
  <li><a class="${reportType == ReportType.PREVENTIVE?'active':''}" id="#js-preventive"
        href="${createLink(params:[reportType: ReportType.PREVENTIVE, reportSubType:ReportSubType.WORKORDERS])}">
    ${message(code:'reports.type.preventive')}</a></li>
  <li><a class="${reportType == ReportType.SPAREPARTS?'active':''}" id="#js-spare-parts"
        href="${createLink(params:[reportType: ReportType.SPAREPARTS, reportSubType:ReportSubType.INVENTORY])}">
    ${message(code:'reports.type.spareParts')}</a></li>
</ul>