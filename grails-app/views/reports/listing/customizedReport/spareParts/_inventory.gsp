<%@ page import="org.chai.memms.spare.part.SparePart.SparePartStatus" %>
<%@ page import="org.chai.memms.util.Utils" %>
<ul>
  <li>
    <label for="sparePartStatus"><g:message code="reports.spareParts.inventory.sparePartStatus"/>:</label>
    <ul class="checkbox-list">
      <g:each in="${SparePartStatus.values() - SparePartStatus.NONE}" var="statusEnum">
        <li>
          <input name="sparePartStatus" type="checkbox" value="${statusEnum.key}"/>
          <label for="${statusEnum.key}">${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)}</label>
        </li>
      </g:each>
    </ul>
  </li>
  <li>
    <label for="acquisitionPeriod"><g:message code="reports.spareParts.inventory.acquisitionPeriod"/>:</label>
    <input name="fromAcquisitionPeriod" class="js-date-picker date-picker idle-field" />
    <span class="dash">-</span>
    <input name="toAcquisitionPeriod" class="js-date-picker date-picker idle-field" />
    <g:checkBox name="noAcquisitionPeriod" class='clear-left'/>
    <span><g:message code="reports.spareParts.inventory.noAcquisitionPeriod"/></span>
  </li>
</ul>