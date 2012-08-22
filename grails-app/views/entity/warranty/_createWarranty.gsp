<div  class="entity-form-container togglable">
	<div>
		<h3>
		<g:if test="${warranty.id != null}">
				<g:message code="default.edit.label" args="[message(code:'warranty.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'warranty.label')]" />
			</g:else>
		</h3>
		<g:locales/>
	</div>
	
	<g:form url="[controller:'department', action:'save', params:[targetURI: targetURI]]" useToken="true">
	
		<input type="hidden" name="warranty.id" value="${warranty?.contact?.id}"></input>
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${warranty}" field="code"/>
		
		<g:i18nInput name="names" label="${message(code:'entity.names.label')}" bean="${warranty}" field="names"/>
		
		<g:i18nTextarea name="descriptions" bean="${warranty}" label="${message(code:'entity.descriptions.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
		
		<fieldset>
	    	<legend>Contact Information:</legend>
	    	<g:input name="contact.contactName" label="${message(code:'entity.name.label')}" bean="${warranty.contact}" field="contactName"/>
	    	<g:input name="contact.email" label="${message(code:'entity.email.label')}" bean="${warranty.contact}" field="email"/>
	    	<g:input name="contact.phone" label="${message(code:'entity.phone.label')}" bean="${warranty.contact}" field="phone"/>
	    	<g:input name="contact.poBox" label="${message(code:'entity.address.label')}" bean="${warranty.contact}" field="poBox"/>
		    <g:i18nTextarea name="contact.addressDescriptions" bean="${warranty.contact}" label="${message(code:'entity.descriptions.label')}" field="addressDescriptions" height="150" width="300" maxHeight="150" />  	
   		</fieldset>
		
		<g:if test="${warranty.id != null}">
			<input type="hidden" name="id" value="${warranty.id}"></input>
		</g:if>
		<div>
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
</div>
