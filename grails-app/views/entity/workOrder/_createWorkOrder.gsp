<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus" %>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrder.Criticality" %>
<%@ page import="org.chai.memms.corrective.maintenance.WorkOrder.FailureReason" %>
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
          <g:message code="order.section.basic.information.label"/>
        </h4> 
      	<g:selectFromList name="equipment.id" readonly="${(closed)? true:false}" label="${message(code:'equipment.label')}" bean="${order}" field="equipment" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'equipmentView', action:'getAjaxData',params: [dataLocation:(dataLocation)?dataLocation.id:order.equipment?.dataLocation?.id])}"
  			from="${equipments}" value="${order?.equipment?.id}" values="${equipments.collect{it.code}}" />
  		<g:if test="${order.id != null}">
	  		<div class="row">
		  		 <label class="top"><g:message code="work.order.reported.by.label"/> :</label>
		  		 ${order.addedBy.names}  - ${Utils.formatDateWithTime(order?.openOn)}
	  		</div>
	  		<div class="row">
		  		 <label class="top"><g:message code="work.order.last.modified.by.label"/> :</label>
		  		 ${order.lastModifiedBy?.names} - ${Utils.formatDateWithTime(order?.lastUpdated)}
	  		</div>
  		</g:if>						
   		<g:textarea name="description" rows="12" width="380" label="${message(code:'entity.description.label')}" readonly="${(closed)? true:false}" bean="${order}" field="description" value="${order.description}"/>
   		<g:selectFromEnum name="criticality" bean="${order}" values="${Criticality.values()}" field="criticality" readonly="${(closed)? true:false}" label="${message(code:'work.order.criticality.label')}"/>
   		<g:if test="${order.id != null}">
   			<g:selectFromEnum name="currentStatus" bean="${order}" values="${OrderStatus.values()}" field="currentStatus" label="${message(code:'entity.status.label')}"/>
   			<table class="items">
	    		<tr>
	    			<th>${message(code:'entity.status.label')}</th>
	    			<th>${message(code:'entity.previous.status.label')}</th>
	    			<th>${message(code:'work.order.status.changed.on.label')}</th>
	    			<th>${message(code:'work.order.status.changed.by.label')}</th>
	    			<th>${message(code:'work.order.status.escalation.label')}</th>
	    		</tr>
	    		<g:each in="${order.status.sort{a,b -> (a.dateCreated > b.dateCreated) ? -1 : 1}}" status="i" var="status">
			    		<tr>
			    			<td>${message(code: status?.status?.messageCode+'.'+status?.status?.name)}</td>
			    			<td>${status?.previousStatus != null && status?.previousStatus != status?.status? message(code: status?.previousStatus?.messageCode+'.'+status?.previousStatus?.name):''}</td>
			    			<td>${Utils.formatDate(status?.dateCreated)}</td>
			    			<td>${status.changedBy.names}</td>
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
        		<g:inputHourMinute name="workTime" field="workTime" hours="${order.workTime?.hours}" minutes="${order.workTime?.minutes}" label='work.order.work.time.label' bean="${order}"/>
        		<g:inputHourMinute name="travelTime" field="travelTime" hours="${order.travelTime?.hours}" minutes="${order.travelTime?.minutes}" label='work.order.travel.time.label' bean="${order}"/>
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
  			from="${technicians}" value="${order?.fixedBy?.id}" values="${technicians.collect{it.firstname + ' ' + it.lastname}}" />	
  			<g:selectFromList name="receivedBy.id" readonly="${(closed)? true:false}" label="${message(code:'work.order.equipment.received.by.label')}" bean="${order}" field="receivedBy" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'user', action:'getAjaxData')}"
  			from="${technicians}" value="${order?.receivedBy?.id}" values="${technicians.collect{it.firstname + ' ' + it.lastname}}" />	
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
<script type="text/javascript">
	$(document).ready(function() {
		numberOnlyField();
		addWorkOrderProcess("${createLink(controller:'workOrderView',action: 'addProcess')}","${order.id}");
		removeWorkOrderProcess("${createLink(controller:'workOrderView',action: 'removeProcess')}");
		addComment("${createLink(controller:'workOrderView',action: 'addComment')}","${order.id}");
		removeComment("${createLink(controller:'workOrderView',action: 'removeComment')}");
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}");
	});
</script>

