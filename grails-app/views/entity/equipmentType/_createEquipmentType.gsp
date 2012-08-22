<%@ page import="org.chai.memms.equipment.EquipmentType.Observation" %>
<div  class="entity-form-container togglable">
	<div>
		<h3>
		<g:if test="${type.id != null}">
				<g:message code="default.edit.label" args="[message(code:'equipment.type.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'equipment.type.label')]" />
			</g:else>
		</h3>
		<g:locales/>
	</div>
	
	<g:form url="[controller:'equipmentType', action:'save', params:[targetURI: targetURI]]" useToken="true">
	
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${type}" field="code"/>
		<g:i18nInput name="names" label="${message(code:'entity.names.label')}" bean="${type}" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${type}" label="${message(code:'entity.descriptions.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
		<g:selectFromEnum name="observation" bean="${type}" values="${Observation.values()}" field="observation" label="${message(code:'entity.observation.label')}"/>
		<g:if test="${type.id != null}">
			<input type="hidden" name="id" value="${type.id}"></input>
		</g:if>
		<div>
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
</div>
