<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible" %>
<ul>
  <g:set var="statusValues" value="${PreventiveOrderStatus.values()}"/>
  <g:render template="/reports/listing/customizedReport/workOrderStatusAndPeriod" model="[statusValues:statusValues]" />
  <li>
    <label for="whoIsResponsible"><g:message code="reports.preventive.workOrders.whoIsResponsible"/></label>
    <ul class="checkbox-list">
      <g:each in="${PreventionResponsible.values() - PreventionResponsible.NONE}" var="statusEnum">
        <li>
          <input name="whoIsResponsible" type="checkbox" value="${statusEnum.key}"/>
          <label for="${statusEnum.key}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
        </li>
      </g:each>
    </ul>
  </li>
</ul>