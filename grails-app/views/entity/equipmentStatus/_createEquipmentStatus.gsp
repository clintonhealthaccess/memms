<%@ page import="org.chai.memms.equipment.EquipmentType.Status" %>
<div>
	<div>
		<h3>
			<g:message code="default.new.label" args="[message(code:'equipmentstatus.label')]"/>
		</h3>
		<g:locales/>
	</div>
	
	<g:form url="[controller:'equipmentStatus', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<input type="hidden" name="equipment.id" value="${status.equipment}"></input>
		<g:selectFromEnum name="status" bean="${status}" values="${Status.values()}" field="value" label="${message(code:'equipment.status.label')}"/>
   		<g:inputDate name="dateOfEvent" precision="day"  value="${Status.dateOfEvent}" id="date-of-event" label="${message(code:'entity.date.of.event.label')}" bean="${status}" field="dateOfEvent"/>
		<g:if test="${status.id != null}">
			<input type="hidden" name="id" value="${status.id}"></input>
		</g:if>
		<div>
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
</div>

