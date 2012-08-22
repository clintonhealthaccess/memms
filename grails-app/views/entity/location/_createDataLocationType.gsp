<div class="entity-form-container togglable">

	<div>
		<h3>
		<g:if test="${dataLocationType.id != null}">
				<g:message code="default.edit.label" args="[message(code:'datalocation.type.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'datalocation.type.label')]" />
			</g:else>
		</h3>
		<g:locales/>
	</div>
	
	<g:form url="[controller:'dataLocationType', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nTextarea name="names" bean="${dataLocationType}" value="${dataLocationType?.names}" label="${message(code:'entity.name.label')}" field="names" height="150" width="300" maxHeight="150" />
		
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${dataLocationType}" field="code"/>
		
		<g:if test="${dataLocationType.id != null}">
			<input type="hidden" name="id" value="${dataLocationType.id}"></input>
		</g:if>
		<div>
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
</div>
