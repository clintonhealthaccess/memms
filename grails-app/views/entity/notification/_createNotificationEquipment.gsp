<div  class="entity-form-container togglable">
	<div class="heading1-bar">
		<h1>
		  <g:if test="${notification?.id != null}">
				<g:message code="default.edit.label" args="[message(code:'notification.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'notification.label')]" />
			</g:else>
		</h1>
		
	</div>
	
	<div class="main">
  	<g:form url="[controller:'notificationEquipment', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
  		<g:textarea name="content"  label="${message(code:'notification.content.label')}" rows="12" width="400" bean="${notification}" value="${notification?.content}" field="content" />
  		<div class="error-list"><g:renderErrors bean="${cmd}" field="content" /></div>
  		<g:if test="${notification?.id != null}">
  			<input type="hidden" name="id" value="${notification.id}"></input>
  		</g:if>
  		<br />
  		<div class="buttons">
  			<button type="submit"><g:message code="default.button.save.label"/></button>
  			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
  		</div>
  	</g:form>
  </div>
</div>
