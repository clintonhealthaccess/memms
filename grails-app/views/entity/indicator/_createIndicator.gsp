<%@ page import="org.chai.memms.reports.dashboard.Indicator.HistoricalPeriod" %>
<%@ page import="org.chai.memms.reports.dashboard.IndicatorCategory" %>
<div  class="entity-form-container togglable">
  <div class="heading1-bar">
    <g:locales/>
    <h1>
      <g:if test="${indicator.id != null}">
        <g:message code="default.edit.label" args="[message(code:'indicator.label')]" />
      </g:if>
      <g:else>
        <g:message code="default.new.label" args="[message(code:'indicator.label')]" />
      </g:else>
    </h1>
  </div>
  <div class="main">
    <g:form url="[controller:'indicator', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
      <g:selectFromList name="category.id" label="${message(code:'indicator.category.label')}" bean="${indicator}" field="category" optionKey="id" multiple="false"
                        from="${IndicatorCategory.list()}" value="${indicator?.category?.id}" optionValue="names" />
      <g:input name="code" label="${message(code:'indicator.code.label')}" bean="${indicator}" field="code"/>
      <g:i18nInput name="names" label="${message(code:'indicator.name.label')}" bean="${indicator}" field="names" />
      <g:i18nTextarea  name="descriptions" label="${message(code:'indicator.description.label')}" bean="${indicator}" field="descriptions" height="200" width="500" maxHeight="300" />
      <g:i18nTextarea name="formulas" label="${message(code:'indicator.formula.label')}" bean="${indicator}" field="formulas" height="200" width="500" maxHeight="300" />
      <g:input name="unit" label="${message(code:'indicator.unit.label')}" bean="${indicator}" field="unit" width="20"/>
      <g:input name="redToYellowThreshold" label="${message(code:'indicator.redToYellowThreshold.label')}" bean="${indicator}" field="redToYellowThreshold"/>
      <g:input name="yellowToGreenThreshold" label="${message(code:'indicator.yellowToGreenThreshold.label')}" bean="${indicator}" field="yellowToGreenThreshold"/>
      <g:selectFromEnum name="historicalPeriod" bean="${indicator}" values="${HistoricalPeriod.values()}" value="${indicator.historicalPeriod}" field="historicalPeriod" label="${message(code:'indicator.historicalPeriod.label')}"/>
      <g:input name="historyItems" label="${message(code:'indicator.historyItems.label')}" bean="${indicator}" field="historyItems"/>
      <g:textarea name="queryScript" label="${message(code:'indicator.queryScript.label')}" bean="${indicator}" field="queryScript" height="200" width="500" maxHeight="300" value="${indicator.queryScript}" />
      <g:i18nInput name="groupNames" label="${message(code:'indicator.groupName.label')}" bean="${indicator}" field="groupNames" width="100"/>
      <g:textarea name="groupQueryScript" label="${message(code:'indicator.groupQueryScript.label')}" bean="${indicator}" field="groupQueryScript" height="200" width="500" maxHeight="300" value="${indicator.groupQueryScript}" />
      <g:inputBox name="sqlQuery"  label="${message(code:'indicator.sqlQuery.label')}" bean="${indicator}" field="sqlQuery" value="${indicator.sqlQuery}" checked="${(indicator.sqlQuery)? true:false}"/>
      <g:inputBox name="active"  label="${message(code:'indicator.active.label')}" bean="${indicator}" field="active" value="${indicator.active}" checked="${(indicator.active)? true:false}"/>
      <g:if test="${indicator.id != null}">
        <input type="hidden" name="id" value="${indicator.id}"></input>
      </g:if>
      <br />
      <div class="buttons">
        <button type="submit"><g:message code="default.button.save.label"/></button>
        <a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
      </div>
    </g:form>
  </div>
</div>