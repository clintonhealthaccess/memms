<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.maintenance.WorkOrderStatus.OrderStatus" %>
<%@ page import="org.chai.memms.maintenance.WorkOrder.Criticality" %>
<%@ page import="org.chai.memms.maintenance.WorkOrder.FailureReason" %>
<r:require modules="tipsy"/>
<div  class="entity-form-container togglable">
    <div class="heading1-bar">
		<h1>
			<g:if test="${order.id != null}">
				<g:message code="default.edit.label" args="[message(code:'work.order.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'work.order.label')]" />
			</g:else>
		</h1>
		<g:locales/>
	</div>
	<div class="main">
  	<g:form url="[controller:'workOrder', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
  	  <div class="form-section">
    	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
            <img src="${resource(dir:'images/icons',file:'star_small.png')}">
          </span>
          <g:message code="work.order.section.basic.information.label"/>
        </h4> 
      	<g:selectFromList name="equipment.id" readonly="${(closed)? true:false}" label="${message(code:'equipment.label')}" bean="${order}" field="equipment.id" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'equipment', action:'getAjaxData')}"
  			from="${equipments}" value="${order?.equipment?.id}" values="${equipments.collect{it.code}}" />	
  		<g:if test="${order.id != null}">
	  		<div class="row">
		  		 <label><g:message code="work.order.reported.by.label"/> :</label>
		  		 ${order.addedBy.firstname} ${order.addedBy.lastname}  - ${Utils.formatDateWithTime(order?.openOn)}
	  		</div>
	  		<div class="row">
		  		 <label><g:message code="work.order.last.modified.by.label"/> :</label>
		  		 ${order.lastModifiedBy?.firstname} ${order.lastModifiedBy?.lastname} - ${Utils.formatDateWithTime(order?.lastModifiedOn)}
	  		</div>
  		</g:if>						
   		<g:textarea name="description" rows="12" width="380" label="${message(code:'entity.description.label')}" readonly="${(closed)? true:false}" bean="${order}" field="description" value="${order.description}"/>
   		<g:selectFromEnum name="criticality" bean="${order}" values="${Criticality.values()}" field="criticality" readonly="${(closed)? true:false}" label="${message(code:'work.order.criticality.label')}"/>
   		<g:if test="${order.id != null}">
   			<g:selectFromEnum name="currentStatus" bean="${order}" values="${OrderStatus.values()}" field="currentStatus" label="${message(code:'work.order.status.label')}"/>
   			<table class="items">
	    		<tr>
	    			<th>${message(code:'work.order.status.label')}</th>
	    			<th>${message(code:'work.order.status.changed.on.label')}</th>
	    			<th>${message(code:'work.order.status.changed.by.label')}</th>
	    			<th>${message(code:'work.order.status.escalation.label')}</th>
	    		</tr>
	    		<g:each in="${order.status.sort{a,b -> (a.changeOn > b.changeOn) ? -1 : 1}}" status="i" var="status">
			    		<tr>
			    			<td>${message(code: status?.status?.messageCode+'.'+status?.status?.name)}</td>
			    			<td>${Utils.formatDate(status?.changeOn)}</td>
			    			<td>${status.changedBy.firstname} ${status.changedBy.lastname}</td>
			    			<td>${(status.escalation)? '\u2713':''}</td>
			    		</tr>
	    		</g:each>
	    	</table>
   		</g:if>
    	</fieldset>	
   		<div id="form-aside-equipment" class="form-aside">
    	  <g:if test="${order.id != null || order?.equipment!=null}">
    	 	 <g:render template="/templates/equipmentFormSide" model="['equipment':order?.equipment,'cssClass':'current','field':'equipment' ]" />
          </g:if>
      	</div>
      </div> 
      	<g:if test="${order.id != null}">
	      <div class="form-section">
	    	<fieldset>
	      	<h4 class="section-title">
	          <span class="question-default">
	            <img src="${resource(dir:'images/icons',file:'star_small.png')}">
	          </span>
	          <g:message code="work.order.section.maintenance.information.label"/>
	        </h4>
        	<div class="row maintenance-process">
        		<g:render template="/templates/processes" model="['processes':order.actions,'processType':'action','label':'work.order.performed.action.label','readonly':closed]" /> 
        		<g:render template="/templates/processes" model="['processes':order.materials,'processType':'material','label':'work.order.materials.used.label','readonly':closed]" /> 
        	</div>
        	<div class="form-content">
        		<g:input name="workTime" label="${message(code:'work.order.work.time.label')}" bean="${order}" field="workTime"/>
        		<g:input name="travelTime" label="${message(code:'work.order.travel.time.label')}" bean="${order}" field="travelTime"/>
	        	<g:currency costName="estimatedCost" id="estimated-cost" costLabel="${message(code:'work.order.estimated.cost.label')}" bean="${order}" costField="estimatedCost"  currencyName="currency" values="${currencies}" currencyField="currency" currencyLabel="${message(code:'work.order.currency.label')}"/>
			</div>
	        </fieldset>
	      </div>
	      <div class="form-section">
	    	<fieldset class="form-content">
	      	<h4 class="section-title">
	          <span class="question-default">
	            <img src="${resource(dir:'images/icons',file:'star_small.png')}">
	          </span>
	          <g:message code="work.order.section.failure.reason.information.label"/>
	        </h4>
        	<g:selectFromEnum name="failureReason" bean="${order}" values="${FailureReason.values()}" field="failureReason" readonly="${(closed)? true:false}" label="${message(code:'work.order.failure.reason.label')}"/>
	        <g:i18nTextarea name="failureReasonDetails" bean="${order}" label="${message(code:'work.order.failure.reason.details.label')}" field="failureReasonDetails" height="150" width="300" maxHeight="150" />
	        <g:i18nTextarea name="testResultsDescriptions" bean="${order}" label="${message(code:'work.order.test.results.descriptions.label')}" field="testResultsDescriptions" height="150" width="300" maxHeight="150" />        	
	        </fieldset>
	      </div>
	      <div class="form-section">
	    	<fieldset class="form-content">
	      	<h4 class="section-title">
	          <span class="question-default">
	            <img src="${resource(dir:'images/icons',file:'star_small.png')}">
	          </span>
	          <g:message code="work.order.section.people.involved.label"/>
	        </h4>
	        <g:selectFromList name="fixedBy.id" readonly="${(closed)? true:false}" label="${message(code:'work.order.equipment.fixed.by.label')}" bean="${order}" field="fixedBy" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'user', action:'getAjaxData')}"
  			from="${technicians}" value="${order?.fixedBy?.id}" values="${technicians.collect{it.firstname + " " + it.lastname}}" />	
  			<g:selectFromList name="receivedBy.id" readonly="${(closed)? true:false}" label="${message(code:'work.order.equipment.received.by.label')}" bean="${order}" field="receivedBy" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'user', action:'getAjaxData')}"
  			from="${technicians}" value="${order?.receivedBy?.id}" values="${technicians.collect{it.firstname + " " + it.lastname}}" />	
  			<g:input name="returnedOn" dateClass="date-picker" label="${message(code:'work.order.returned.on.label')}" bean="${order}" field="returnedOn"/>
  			<g:input name="returnedTo" label="${message(code:'work.order.returned.to.label')}" bean="${order}" field="returnedTo"/>
	        </fieldset>
	      </div>
      	</g:if>
      	<g:if test="${order.id != null}">
			<input type="hidden" name="id" value="${order.id}"/>
	  	</g:if>
  		<div class="buttons">
  			<button type="submit"><g:message code="default.button.save.label"/></button>
  			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
  		</div>  
  	</g:form>
  </div>
  
  <g:if test="${order.id != null}">
    <div class="heading1-bar">
  		<h1><g:message code="work.order.comment.label.alt" args="${['s']}"/></h1>
  	</div>
    <div class="main">
  	
	  	<div class="comment-section">
	  		<div class="comment-field">
		  		<label><g:message code="work.order.comment.label" args="${['']}"/></label>
		  		<input type="hidden" name="order" value="${order.id}"/>
		  		<textarea name="content" class="idle-field" id="comment-content" rows="2" cols="90"></textarea>
	  		</div>
	  		<div class="comment-button">
	  			<button class="medium" id="add-comment"><g:message code="work.order.comment.label.alt" args="${['']}"/></button>
	  			<span class="ajax-error"><g:message code="entity.error.updating.try.again"/></span>
	  			<img src="${resource(dir:'images',file:'spinner.gif')}" class="ajax-spinner"/>
	  		</div> 
	  		 <g:render template="/templates/comments" model="['order':order,'closed':closed]" /> 
	  	</div>
    </div>
  </g:if>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		addProcess("${createLink(controller:'workOrder',action: 'addProcess')}","${order.id}")
		removeProcess("${createLink(controller:'workOrder',action: 'removeProcess')}")
		addComment("${createLink(controller:'workOrder',action: 'addComment')}","${order.id}")
		removeComment("${createLink(controller:'workOrder',action: 'removeComment')}")
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
	});
</script>

