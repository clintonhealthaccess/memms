<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible" %>
<div class="form-section">
  	<fieldset class="form-content">
    	<h4 class="section-title">
        <span class="question-default">
          <img src="${resource(dir:'images/icons',file:'star_small.png')}">
        </span>
        <g:message code="order.section.basic.information.label"/>
      </h4> 

    	<g:selectFromList name="equipment.id" label="${message(code:'equipment.label')}" bean="${order}" field="equipment" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'equipmentView', action:'getAjaxData')}"
			from="${equipments}" value="${order?.equipment?.id}" values="${equipments.collect{it.code}}" />	
  		
      <g:if test="${order.id != null}">
        <input type="hidden" name="id" value="${order.id}"/>
	  		<div class="row">
		  		 <label class="top"><g:message code="preventive.order.added.by.label"/> :</label>
		  		 ${order.addedBy.names} <g:message code="default.on.label"/> : ${Utils.formatDateWithTime(order.dateCreated)}
	  		</div>
	  		<div class="row">
		  		 <label class="top"><g:message code="preventive.order.last.modified.by.label"/> :</label>
		  		 ${order.lastModifiedBy?.names} <g:message code="default.on.label"/> : ${Utils.formatDateWithTime(order?.lastUpdated)}
	  		</div>
  		</g:if>	

		<g:i18nInput name="names" label="${message(code:'entity.names.label')}" bean="${order}" field="names"/>

   		<g:textarea name="description" rows="12" width="380" label="${message(code:'entity.description.label')}"  bean="${order}" field="description" value="${order.description}"/>
      <g:if test="${order.id != null}">
   		   <g:selectFromEnum name="status" bean="${order}" values="${PreventiveOrderStatus.values()}" field="status"  label="${message(code:'entity.status.label')}"/>
      </g:if>
   		<g:selectFromEnum name="preventionResponsible" bean="${order}" values="${PreventionResponsible.values()}" field="preventionResponsible"  label="${message(code:'preventive.order.prevention.responsible.label')}"/>
      
   		 <g:selectFromList name="technicianInCharge.id" label="${message(code:'entity.name.label')}" bean="${order}" field="technicianInCharge" optionKey="id" multiple="false" ajaxLink="${createLink(controller:'user', action:'getAjaxData')}" from="${technicians}" value="${order?.technicianInCharge?.id}" values="${technicians.collect{it?.names}}" />
    </fieldset>
    <div id="form-aside-equipment" class="form-aside">
      <g:if test="${order.id != null || order?.equipment!=null}">
           <g:render template="/templates/equipmentFormSide" model="['equipment':order?.equipment,'cssClass':'current','field':'equipment' ]" />
      </g:if>
    </div>
</div>	
  	 
  	