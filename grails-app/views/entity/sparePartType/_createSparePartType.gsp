
<div class="entity-form-container togglable">
	<div class="heading1-bar">
		<g:locales />
		<h1>
			<g:if test="${type.id != null}">
				<g:message code="default.edit.label"
					args="[message(code:'spare.part.type.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label"
					args="[message(code:'spare.part.type.label')]" />
			</g:else>
		</h1>
	</div>
	<div class="main">
		<g:form url="[controller:'sparePartType', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
			<g:input name="code" label="${message(code:'entity.code.label')}" bean="${type}" field="code" />
			<g:i18nInput name="names" label="${message(code:'entity.name.label')}" bean="${type}" field="names" />
			<g:input name="partNumber" label="${message(code:'entity.part.number.label')}" bean="${type}" field="partNumber" />
				
			<g:input name="discontinuedDate" dateClass="date-picker" label="${message(code:'entity.discontinued.date.label')}" bean="${type}" field="discontinuedDate" />
				
				
			<g:selectFromList name="manufacturer.id" label="${message(code:'provider.type.manufacturer')}" bean="${type}" field="manufacturer" optionKey="id" multiple="false" ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'MANUFACTURER'])}" from="${manufacturers}" value="${type.manufacturer?.id}" values="${manufacturers.collect{it.contact?.contactName}}" />
			<g:input name="model" label="${message(code:'entity.model.label')}" bean="${type}" field="model" />
			<g:selectFromList name="compatibleEquipmentTypes" label="${message(code:'equipmentType.compatible.equipment.types.label')}" bean="${type}" field="compatibleEquipmentTypes" optionKey="id" multiple="true" ajaxLink="${createLink(controller:'equipmentType', action:'getAjaxData',params:['observation':'USEDINMEMMS'])}" from="${compatibleEquipmentTypes}" value="${type.compatibleEquipmentTypes*.id}" values="${compatibleEquipmentTypes.collect{it.names}}" />
				
			<g:selectFromList name="vendors" label="${message(code:'provider.vendors.label')}" bean="${type}" field="vendors" optionKey="id" multiple="true" ajaxLink="${createLink(controller:'provider', action:'getAjaxData',params:['type':'BOTH'])}" from="${vendors}" value="${type.vendors*.id}" values="${vendors.collect{it.contact.contactName}}" />

			<g:i18nTextarea name="descriptions" bean="${type}" label="${message(code:'entity.comment.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
				
			<g:if test="${type.id != null}">
				<input type="hidden" name="id" value="${type.id}"></input>
			</g:if>
			<br />
			<div class="buttons">
				<button type="submit">
					<g:message code="default.button.save.label" />
				</button>
				<a href="${createLink(uri: targetURI)}">
				    <g:message code="default.link.cancel.label" /></a>
			</div>
		</g:form>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function() {		
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
	});
</script>

