<div class="entity-form-container togglable">

	<div>
		<h3>
			<g:if test="${location.id != null}">
				<g:message code="default.edit.label"
					args="[message(code:'datalocation.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label"
					args="[message(code:'datalocation.label')]" />
			</g:else>
		</h3>
		<g:locales/>
	</div>
	
	<g:form url="[controller:'dataLocation', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nTextarea name="names" bean="${location}" value="${location?.names}" label="${message(code:'entity.name.label')}" field="names" height="150" width="300" maxHeight="150" />
		
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${location}" field="code"/>
		
		<g:selectFromList name="type.id" label="${message(code:'datalocation.type.label')}" bean="${location}" field="type" 
			from="${types}" value="${location.type?.id}" values="${types.collect{it.names}}" optionKey="id"/>

		<g:selectFromList name="location.id" label="${message(code:'location.label')}" bean="${location}" field="location" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'location', action:'getAjaxData', params: [class: 'Location'])}"
			from="${locations}" value="${location.location?.id}" values="${locations.collect{it.names}}" />

		<g:if test="${location.id != null}">
			<input type="hidden" name="id" value="${location.id}"></input>
		</g:if>
		<div>
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
</div>
