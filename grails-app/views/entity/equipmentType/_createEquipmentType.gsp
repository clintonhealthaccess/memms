<%@ page import="org.chai.memms.inventory.EquipmentType.Observation" %>
<div  class="entity-form-container togglable">
	<div class="heading1-bar">
		<g:locales/>
		<h1>
		  <g:if test="${type.id != null}">
				<g:message code="default.edit.label" args="[message(code:'equipment.type.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'equipment.type.label')]" />
			</g:else>
		</h1>
		
	</div>
              
	
	<div class="main">
  	<g:form url="[controller:'equipmentType', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
  	   <fieldset class="form-content">
          <h4 class="section-title">
              <span class="question-default">
                <img src="${resource(dir:'images/icons',file:'star_small.png')}">
              </span>
              <g:message code="prevention.section.process.information.label"/>
          </h4> 
      		<g:input name="code" label="${message(code:'equipment.type.code.label')}" bean="${type}" field="code"/>
      		<g:i18nInput name="names" label="${message(code:'entity.names.label')}" bean="${type}" field="names"/>
      		<g:i18nTextarea name="descriptions" bean="${type}" label="${message(code:'entity.descriptions.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
      		<g:selectFromEnum name="observation" bean="${type}" values="${Observation.values()}" field="observation" label="${message(code:'entity.observation.label')}"/>
      		<g:inputYearMonth name="expectedLifeTime" field="expectedLifeTime" years="${type.expectedLifeTime?.years}" months="${type.expectedLifeTime?.months}" bean="${type}" label='entity.expectedLifeTime.label'/>
    		</fieldset> 
        <g:if test="${type.id != null}">
          <fieldset class="form-content">
                <h4 class="section-title">
                    <span class="question-default">
                      <img src="${resource(dir:'images/icons',file:'star_small.png')}">
                    </span>
                    <g:message code="preventive.section.action.information.label"/>
                </h4> 
              <div class="row maintenance-process">
                <g:render template="/templates/preventiveActions" model="['type':type,'label':'preventive.action.label','readonly':closed]" /> 
              </div>
          </fieldset>
        </g:if>
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
  $(document).ready(function() {
    addPreventiveAction("${createLink(controller:'equipmentType',action: 'addAction')}","${type.id}");
    removePreventiveAction("${createLink(controller:'equipmentType',action: 'removeAction')}");
  });
</script>


