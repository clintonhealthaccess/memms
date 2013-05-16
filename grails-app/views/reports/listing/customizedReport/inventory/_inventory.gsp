<%@ page import="org.chai.memms.inventory.EquipmentStatus.Status" %>
<ul>
  <li>
    %{-- TODO fix checkbox list styles !!! --}%
    <label for="equipmentStatus"><g:message code="reports.inventory.inventory.equipmentStatus"/>:</label>
    <g:each in="${Status.values() - Status.NONE}" var="statusEnum">
        <input name="equipmentStatus" type="checkbox" value="${statusEnum.key}"/>
        <label for="${statusEnum.key}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
    </g:each>
  </li>
  <li>
    <label for="acquisitionPeriod"><g:message code="reports.inventory.inventory.acquisitionPeriod"/>:</label>
    <input name="fromAcquisitionPeriod" class="js-date-picker date-picker idle-field" />
    <span class="dash">-</span>
    <input name="toAcquisitionPeriod" class="js-date-picker date-picker idle-field" />
    <g:checkBox name="noAcquisitionPeriod"/>
    <span><g:message code="reports.inventory.inventory.noAcquisitionPeriod"/></span>
  </li>
  <li>
    %{-- TODO fix checkbox styles !!! --}%
    <label for="obsolete"><g:message code="reports.inventory.inventory.obsolete" />:</label>
    <input name="obsolete" type="checkbox"/>
  </li>
  <li>
    %{-- TODO fix checkbox styles !!! --}%
    <label for="warranty"><g:message code="reports.inventory.warranty"/>:</label>
    <input name="warranty" type="checkbox"/>
  </li>
</ul>