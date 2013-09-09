<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<ul id='js-top-tabs' class="v-tabs-nav left">

  <li><a class="${reportType == ReportType.CORRECTIVE?'active':''}" id="#js-corrective"
        href="${createLink(params:[reportType:ReportType.CORRECTIVE])}">
    ${message(code:'reports.type.corrective')}</a></li>

  <li><a class="${reportType == ReportType.PREVENTIVE?'active':''}" id="#js-preventive"
        href="${createLink(params:[reportType:ReportType.PREVENTIVE])}">
    ${message(code:'reports.type.preventive')}</a></li>
 
  <li><a class="${reportType == ReportType.EQUIPMENT?'active':''}" id="#js-management"
        href="${createLink(params:[reportType:ReportType.EQUIPMENT])}">
    ${message(code:'reports.type.equipment.management')}</a></li>

  <li><a class="${reportType == ReportType.SPAREPARTS?'active':''}" id="#js-spare-parts"
        href="${createLink(params:[reportType:ReportType.SPAREPARTS])}">
    ${message(code:'reports.type.spareParts')}</a></li>

    <li><a class="${reportType == ReportType.MEMMS?'active':''}" id="#js-monitoring"
          href="${createLink(params:[reportType:ReportType.MEMMS])}">
      ${message(code:'reports.type.memms.monitoring')}</a></li>

</ul>