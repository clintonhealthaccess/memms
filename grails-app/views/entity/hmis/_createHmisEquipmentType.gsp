<div  class="entity-form-container togglable">
	<div class="heading1-bar">
		<g:locales/>
		<h1>
		  <g:if test="${type.id != null}">
				<g:message code="default.edit.label" args="[message(code:'hmis.equipment.type.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'hmis.equipment.type.label')]" />
			</g:else>
		</h1>
		
	</div>
	<div class="main">
  	<g:form url="[controller:'hmisEquipmentType', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
  	   <fieldset class="form-content">
          	<h4 class="section-title">	
	            <span class="question-default">
	            <img src="${resource(dir:'images/icons',file:'star_small.png')}">
	            </span>
	            <g:message code="prevention.section.process.information.label"/>
         	</h4> 
      		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${type}" field="code"/>
      		<g:i18nInput name="names" label="${message(code:'entity.names.label')}" bean="${type}" field="names"/>
      		<g:selectFromList 
      		name="equipmentTypes" label="${message(code:'equipment.type.label')}" bean="${type}" field="equipmentType" optionKey="id" multiple="true"
  			ajaxLink="${createLink(controller:'equipmentType', action:'getAjaxData', params: [observation: 'USEDINMEMMS',hmis:'true'])}"
  			from="${equipmentTypes}" value="${type.equipmentTypes?.id}" values="${equipmentTypes.collect{it.names}}" />	
       	</fieldset>
        <g:if test="${type.id != null}">
        <input type="hidden" name="id" value="${type.id}"></input>
      </g:if>
        <br />
  		<div class="buttons">
  			<button type="submit"><g:message code="default.button.save.label"/></button>
  			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
  		</div>
  	</g:form>
  </div>
</div>
<script type="text/javascript">

</script>