<div  class="entity-form-container togglable">
  <div class="heading1-bar">
    <g:locales/>
    <h1>
      <g:if test="${userDefinedVariable.id != null}">
        <g:message code="default.edit.label" args="[message(code:'userDefinedVariable.label')]" />
      </g:if>
      <g:else>
        <g:message code="default.new.label" args="[message(code:'userDefinedVariable.label')]" />
      </g:else>
    </h1>
  </div>
  <div class="main">
    <g:form url="[controller:'userDefinedVariable', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
      <g:input name="code" label="${message(code:'userDefinedVariable.code.label')}" bean="${userDefinedVariable}" field="code" width="100"/>
      <g:i18nInput name="names" label="${message(code:'userDefinedVariable.name.label')}" bean="${userDefinedVariable}" field="names" width="100"/>
      <g:input name="currentValue" label="${message(code:'userDefinedVariable.currentValue.label')}" bean="${userDefinedVariable}" field="currentValue" width="10"/>
      <g:if test="${userDefinedVariable.id != null}">
        <input type="hidden" name="id" value="${userDefinedVariable.id}"></input>
      </g:if>
      <br />
      <div class="buttons">
        <button type="submit"><g:message code="default.button.save.label"/></button>
        <a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
      </div>
    </g:form>
  </div>
</div>