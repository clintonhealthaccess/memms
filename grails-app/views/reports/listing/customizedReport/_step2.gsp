<%@ page import="org.chai.memms.util.Utils.ReportType" %>
<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<div class="dialog-form step-2" id='js-step-2'>
  <!-- Step 2 -->
  <h2><g:message code="reports.apply.filters"/><span class="right"><g:message code="reports.step" args="['2','4']"/></span></h2>
  <p>${message(code:'reports.type.label')}: <b>${message(code:'reports.type.'+reportType?.reportType)}</b> > ${message(code:'reports.subType.label')}: <b>${message(code:'reports.subType.'+reportSubType?.reportSubType)}</b></p>
  <g:formRemote name="formRemoteStep2Next" url="[action:'step3', params:step2Params]" update="dialog-form"
    onSuccess="customizedlisting_init(3)">
    <fieldset>
      <ul>
        <li>
          <g:selectFromList name="dataLocations" label="${message(code:'reports.dataLocation')}" field="dataLocations"
            optionKey="id" multiple="true" value="${dataLocations*.id}"
            ajaxLink="${createLink(controller: 'dataLocation', action:'getAjaxData')}"
            values="${dataLocations.collect{it.names+' '+it.id+' ['+it.location.names+']'}}" />
          <g:checkBox name="allDataLocations" class='js-select-all'/>
          <span><g:message code="reports.selectAll"/></span>
        </li>
        <g:if test="${[ReportType.INVENTORY,ReportType.CORRECTIVE,ReportType.PREVENTIVE].contains(reportType)}">
          <li>
            <g:selectFromList name="departments" label="${message(code:'reports.department')}" field="departments"
              optionKey="id" multiple="true" value="${departments*.id}"
              ajaxLink="${createLink(controller: 'department', action:'getAjaxData')}"
              values="${departments.collect{it.names+' ['+it.id+']'}}" />
            <g:checkBox name="allDepartments" class='js-select-all'/>
            <span><g:message code="reports.selectAll"/></span>
          </li>
          <li>
            <g:selectFromList name="equipmentTypes" label="${message(code:'reports.equipmentType')}"
              field="type" optionKey="id" multiple="true" value="${equipmentTypes*.id}"
              ajaxLink="${createLink(controller:'EquipmentType', action:'getAjaxData', params:[observation:'USEDINMEMMS'])}"
              values="${equipmentTypes.collect{it.names + ' ['+ it.code +']'}}"/>
            <g:checkBox name="allEquipmentTypes" class='js-select-all'/>
            <span><g:message code="reports.selectAll"/></span>
          </li>
          <li class="modular">
            %{-- TODO fix date + select box styles !!! --}%
            <label><g:message code="reports.cost"/>:</label>
            <input type="text" name="fromCost"/><span class="dash">-</span><input type="text" name="toCost"/>
            <select name="costCurrency">
              <g:each in="${currencies}" var="currencyEnum">
                <option value="${currencyEnum.key}">
                  ${message(code:'currency.'+currencyEnum.value)}
                </option>
              </g:each>
            </select>
          </li>
        </g:if>
        <g:if test="${[ReportType.SPAREPARTS].contains(reportType)}">
          <li>
            <!-- TODO sparePartTypes -->
            <g:selectFromList name="sparePartTypes" label="${message(code:'reports.sparePartType')}"
              field="type" optionKey="id" multiple="true" value="${sparePartTypes*.id}"
              ajaxLink="${createLink(controller:'SparePartType', action:'getAjaxData', params:[observation:'USEDINMEMMS'])}"
              values="${sparePartTypes.collect{it.names + ' ['+ it.code +']'}}"/>
            <g:checkBox name="allSparePartTypes" class='js-select-all'/>
            <span><g:message code="reports.selectAll"/></span>
          </li>
        </g:if>
      </ul>
      <g:if test="${[ReportSubType.INVENTORY,ReportSubType.WORKORDERS,ReportSubType.STATUSCHANGES,ReportSubType.STOCKOUT].contains(reportSubType)}">
        <g:render
            template="/reports/listing/customizedReport/${reportType.getReportType()}/${reportSubType.getReportSubType()}"/>
      </g:if>
    </fieldset>
  </g:formRemote>
  <g:formRemote name="formRemoteStep2Prev" url="[action:'step1', params:step2Model]" update="dialog-form"
    onSuccess="customizedlisting_init(1)">
  </g:formRemote>
  <a href="#" class="ui-widget-previous left btn gray medium" id='js-prev-step-2'><g:message code="reports.step.previous"/></a>
  <a href="#" class="ui-widget-next right btn" id='js-next-step-2'><g:message code="reports.step.next"/></a>
  <!-- end step 2 -->
</div>
