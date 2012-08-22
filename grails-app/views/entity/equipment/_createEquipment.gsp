<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.equipment.EquipmentStatus" %>
<div  class="entity-form-container togglable">
	<div>
		<h3>
			<g:if test="${equipment.id != null}">
				<g:message code="default.edit.label" args="[message(code:'equipment.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'equipment.label')]" />
			</g:else>
		</h3>
		<g:locales/>
	</div>
	<g:form url="[controller:'equipment', action:'save', params:[targetURI: targetURI]]" useToken="true">
	<fieldset>
    	<legend>Basic Information:</legend>	
    	<g:selectFromList name="type.id" label="${message(code:'equipment.type.label')}" bean="${equipment}" field="type" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'equipmentType', action:'getAjaxData')}"
			from="${types}" value="${equipment?.type?.id}" values="${types.collect{it.names}}" />
		<g:input name="expectedLifeTime" label="${message(code:'equipment.expected.life.time.label')}" bean="${equipment}" field="expectedLifeTime"/>
			
		<g:input name="serialNumber" label="${message(code:'equipment.serial.number.label')}" bean="${equipment}" field="serialNumber"/>
		<g:input name="model" label="${message(code:'equipment.model.label')}" bean="${equipment}" field="model"/>
		<g:i18nTextarea name="descriptions" bean="${equipment}" label="${message(code:'entity.descriptions.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
		
		<g:selectFromList name="department.id" label="${message(code:'department.label')}" bean="${equipment}" field="department" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'department', action:'getAjaxData')}"
			from="${departments}" value="${equipment?.department?.id}" values="${departments.collect{it.names}}" />
			
		<g:input name="room" label="${message(code:'equipment.room.label')}" bean="${equipment}" field="room"/>
	</fieldset>	
	<fieldset>
    	<legend>Manufacture Information:</legend>
    	<g:selectFromList name="manufacture.id" label="${message(code:'provider.manufacture.label')}" bean="${equipment}" field="manufacture" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'MANUFACTURE'])}"
			from="${manufactures}" value="${equipment?.manufacture?.id}" values="${manufactures.collect{it.contact.contactName}}" />
			
			<g:inputDate name="manufactureDate" precision="day"  value="${equipment.manufactureDate}" id="manufacture-date" label="${message(code:'equipment.manufacture.date.label')}" bean="${equipment}" field="manufactureDate"/>
   	</fieldset>
	<fieldset>
    	<legend>Supply Information:</legend>
    	<g:selectFromList name="supplier.id" label="${message(code:'provider.supplier.label')}" bean="${equipment}" field="supplier" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'SUPPLIER'])}"
			from="${suppliers}" value="${equipment?.supplier?.id}" values="${suppliers.collect{it.contact.contactName}}" />		
		<g:inputDate name="purchaseDate" precision="day" id="purchase-date" value="${equipment.purchaseDate}" label="${message(code:'equipment.purchase.date.label')}" bean="${equipment}" field="purchaseDate"/>
		<g:input name="purchaseCost" label="${message(code:'equipment.purchase.cost.label')}" bean="${equipment}" field="purchaseCost"/>
    	
   	</fieldset>
	<fieldset>
    	<legend>Status Information:</legend>
    	<g:if test="${equipment.id == null}">
   			<g:selectFromEnum name="status" bean="${equipment}" values="${EquipmentStatus.Status.values()}" field="status" label="${message(code:'equipmentstatus.label')}"/>
    	</g:if>
    	<g:if test="${equipment?.status?.size() > 0}">
	    	<g:each in="${equipment?.status}" status="i" var="status">
		    	<table>
		    		<tr>
		    			<td>${status.value}</td>
		    			<td>${Utils.formatDate(status.statusChangeDate)}</td>
		    			<td>${status.current}</td>
		    		</tr>
		    	</table>
	    	</g:each>
   		</g:if>
   	</fieldset>
	<fieldset>
    	<legend>Warranty Information:</legend>	
    	<g:input name="warranty.contact.contactName" label="${message(code:'contact.name.label')}" bean="${equipment?.warranty?.contact}" field="contactName"/>
    	<g:input name="warranty.contact.email" label="${message(code:'contact.email.label')}" bean="${equipment?.warranty?.contact}" field="email"/>
    	<g:input name="warranty.contact.phone" label="${message(code:'contact.phone.label')}" bean="${equipment?.warranty?.contact}" field="phone"/>
    	<g:input name="warranty.contact.poBox" label="${message(code:'contact.address.label')}" bean="${equipment?.warranty?.contact}" field="poBox"/>
	    <g:i18nTextarea name="warranty.contact.addressDescriptions" bean="${equipment?.warranty?.contact}" label="${message(code:'contact.address.descriptions.label')}" field="addressDescriptions" height="150" width="300" maxHeight="150" />
   		<g:i18nTextarea name="warranty.descriptions" bean="${equipment.warranty}" label="${message(code:'entity.description.label')}" field="descriptions" height="150" width="300" maxHeight="150" />	 			
	</fieldset>   
		<g:if test="${equipment.id != null}">
			<input type="hidden" name="id" value="${equipment.id}"></input>
		</g:if>
		<div>
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
</div>