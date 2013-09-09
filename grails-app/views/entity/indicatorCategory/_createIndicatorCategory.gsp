<div  class="entity-form-container togglable">
  <div class="heading1-bar">
    <g:locales/>
    <h1>
      <g:if test="${indicatorCategory.id != null}">
        <g:message code="default.edit.label" args="[message(code:'indicatorCategory.label')]" />
      </g:if>
      <g:else>
        <g:message code="default.new.label" args="[message(code:'indicatorCategory.label')]" />
      </g:else>
    </h1>
  </div>
  <div class="main">
    <g:form url="[controller:'indicatorCategory', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
      <g:input name="code" label="${message(code:'indicatorCategory.code.label')}" bean="${indicatorCategory}" field="code" width="100"/>
      <g:i18nInput name="names" label="${message(code:'indicatorCategory.name.label')}" bean="${indicatorCategory}" field="names" width="100"/>
      <g:input name="redToYellowThreshold" label="${message(code:'indicatorCategory.redToYellowThreshold.label')}" bean="${indicatorCategory}" field="redToYellowThreshold" width="10"/>
      <g:input name="yellowToGreenThreshold" label="${message(code:'indicatorCategory.yellowToGreenThreshold.label')}" bean="${indicatorCategory}" field="yellowToGreenThreshold" width="10"/>
      <g:if test="${indicatorCategory.id != null}">
        <input type="hidden" name="id" value="${indicatorCategory.id}"></input>
      </g:if>
      <br />
      <div class="buttons">
        <button type="submit"><g:message code="default.button.save.label"/></button>
        <a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
      </div>
    </g:form>
  </div>
</div>