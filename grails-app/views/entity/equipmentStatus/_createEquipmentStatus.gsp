<%@ page import="org.chai.memms.equipment.EquipmentType.Status" %>
<div id="popup-contact">
	<a id="popup-contact-close">x</a>
	<div  class="entity-form-container togglable">
		<div>
			<h3>
			<g:if test="${status.id != null}">
				<g:message code="default.edit.label" args="[message(code:'model.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'model.label')]" />
			</g:else>
			</h3>
			<g:locales/>
		</div>
		
		<g:form url="[controller:'equipmentStatus', action:'save', params:[targetURI: targetURI]]" useToken="true">
			<input type="hidden" name="equipment" value="${status.equipment}"></input>
			<g:selectFromEnum name="status" bean="${status}" values="${Status.values()}" field="value" label="${message(code:'equipment.status.label')}"/>
			<g:if test="${status.id != null}">
				<input type="hidden" name="id" value="${status.id}"></input>
			</g:if>
			<div>
				<button type="submit"><g:message code="default.button.save.label"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
			</div>
		</g:form>
	</div>
</div>
<div id="background-popup"></div>