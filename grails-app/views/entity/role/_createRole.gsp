<div  class="entity-form-container togglable">
	<div class="heading1-bar">
		<g:locales/>
		<h1>
		  <g:if test="${role.id != null}">
				<g:message code="default.edit.label" args="[message(code:'role.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'role.label')]" />
			</g:else>
		</h1>
		
	</div>
	
	<div class="main">
  	<g:form url="[controller:'role', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
	
  		<g:input name="name" label="${message(code:'role.name.label')}" bean="${role}" field="name"/>
  		<g:textarea name="permissionString"  label="${message(code:'role.permissions.label')}" rows="5" bean="${role}" value="${role.permissionString}" field="permissionString" />
  		<g:if test="${role.id != null}">
  			<input type="hidden" name="id" value="${role.id}"></input>
  		</g:if>
  		<br />
  		<div class="buttons">
  			<button type="submit"><g:message code="default.button.save.label"/></button>
  			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
  		</div>
  	</g:form>
  </div>
</div>
