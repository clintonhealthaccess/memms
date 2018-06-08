<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.inventory.EquipmentStatus.Status" %>
<%@ page import="org.chai.memms.inventory.Equipment.PurchasedBy" %>
<%@ page import="org.chai.memms.inventory.Equipment.Donor" %>
<div  class="entity-form-container togglable">
  <div class="heading1-bar">
		<h1>
			<g:if test="${equipment.id != null}">
				<g:message code="default.edit.label" args="[message(code:'equipment.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'equipment.label')]" />
			</g:else>
		</h1>
		<g:locales/>
	</div>
	
	<div class="main">
  	<g:form url="[controller:'equipment', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
  	
  	  <div class="form-section">
      	<fieldset class="form-content">
        	<h4 class="section-title">
            <span class="question-default">
              <img src="${resource(dir:'images/icons',file:'star_small.png')}">
            </span>
            <g:message code="equipment.section.basic.information.label" default="Basic Information"/>
          </h4>  			  		
      		<div class="row">
      		<!-- To be viewed if there is no ? sign because in spare part this is not working unless you put ? after dataLocation property --> 
    			  <input type="hidden" name="dataLocation.id" value="${equipment.dataLocation.id}" />
    			  <label><g:message code="datalocation.label"/>:</label> ${equipment.dataLocation.names}
    		  </div>
    		  				
        	<g:selectFromList name="type.id" label="${message(code:'equipment.type.label')}" bean="${equipment}" field="type" optionKey="id" multiple="false"
    			ajaxLink="${createLink(controller:'equipmentType', action:'getAjaxData', params: [observation:'USEDINMEMMS'])}"
    			from="${types}" value="${equipment?.type?.id}" values="${types.collect{it.names}}" />
    		<g:selectFromList name="manufacturer.id" label="${message(code:'provider.type.manufacturer')}" bean="${equipment}" field="manufacturer" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'MANUFACTURER'])}"
  			from="${manufacturers}" value="${equipment?.manufacturer?.id}" values="${manufacturers.collect{it.contact?.contactName}}" />	
  			<g:input name="model" label="${message(code:'equipment.model.label')}" bean="${equipment}" field="model"/>
  			<g:inputDate name="manufactureDate"  precision="month" label="${message(code:'equipment.manufacture.date.label')}" value="${equipment?.manufactureDate}" bean="${equipment}" field="manufactureDate"/>
     	
    		<g:input name="serialNumber" label="${message(code:'equipment.serial.number.label')}" bean="${equipment}" field="serialNumber"/>
    		<g:input name="oldTagNumber" label="${message(code:'equipment.old.tag.number.label')}" bean="${equipment}" field="oldTagNumber"/>	
      		<g:inputYearMonth name="expectedLifeTime" field="expectedLifeTime" years="${equipment.expectedLifeTime?.years}" months="${equipment.expectedLifeTime?.months}" label='entity.expectedLifeTime.label' bean="${equipment}"/>
      		<g:i18nTextarea name="descriptions" bean="${equipment}" label="${message(code:'entity.descriptions.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
            <g:input name="installationDate" dateClass="date-picker" label="${message(code:'equipment.installation.date.label')}" bean="${equipment}" field="installationDate"/>
           </fieldset>
           	<div id="form-aside-type" class="form-aside">
      	  		<g:if test="${equipment?.type != null}">
      	 	  	<g:render template="/templates/typeFormSide" model="['type':equipment?.type,'cssClass':'current','field':'type' ]" />
          		</g:if>
      		</div>
			<div id="form-aside-manufacturer" class="form-aside">
    	   		<g:if test="${equipment?.manufacturer != null}">
	    	 	 	<g:render template="/templates/providerFormSide" model="['provider':equipment?.manufacturer,'type':equipment?.manufacturer?.type,'label':'provider.manufacturer.details','cssClass':'current','field':'manufacturer' ]" />
         		</g:if>
         		<g:else>
            		<g:each in="${manufacturers}" var="provider">
                		<g:render template="/templates/providerFormSide" model="['provider':provider,'type':provider.type,'label':'provider.manufacturer.details','cssClass':'form-aside-hidden','field':'manufacturer' ]" />
            		</g:each>
				</g:else> 
			</div>
      	</div>
        <div class="form-section">
      	<fieldset class="form-content">
        	<h4 class="section-title">
            <span class="question-default">
              <img src="${resource(dir:'images/icons',file:'star_small.png')}">
            </span>
            <g:message code="equipment.section.location.information.label" default="Location of the equipment"/>
          </h4>		
      		<g:selectFromList name="department.id" label="${message(code:'department.label')}" bean="${equipment}" field="department" optionKey="id" multiple="false"
    			ajaxLink="${createLink(controller:'department', action:'getAjaxData')}"
    			from="${departments}" value="${equipment?.department?.id}" values="${departments.collect{it.names}}" />
      		<g:input name="room" label="${message(code:'equipment.room.label')}" bean="${equipment}" field="room"/>
      	</fieldset>
      	</div>
     
      <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
            <img src="${resource(dir:'images/icons',file:'star_small.png')}" alt="Section"/>
          </span>
          <g:message code="equipment.section.status.information.label" default="Status Information"/> 
        </h4>
      	<g:if test="${equipment.id == null}">
      			<g:selectFromEnum name="status" bean="${cmd}" values="${Status.values()}" field="status" label="${message(code:'equipment.status.label')}"/>
      			<g:input name="dateOfEvent" dateClass="date-picker" label="${message(code:'equipment.status.date.of.event.label')}" bean="${cmd}" value="${Utils.formatDate(now)}" field="dateOfEvent"/>
      			<div class="status-information">
	    			<g:input name="disposalRefNumber"  label="${message(code:'equipment.disposal.ref.number.label')}" bean="${cmd}" field="disposalRefNumber"/>
    			</div>
      			<g:inputBox name="obsolete"  label="${message(code:'equipment.obsolete.label')}" bean="${equipment}" field="obsolete" value="${equipment.obsolete}" checked="${(equipment.obsolete)? true:false}"/>
      	</g:if>
      	<g:if test="${equipment?.status!=null}">
      	 	<g:inputBox name="obsolete"  label="${message(code:'equipment.obsolete.label')}" bean="${equipment}" field="obsolete" value="${equipment.obsolete}" checked="${(equipment.obsolete)? true:false}"/>
	    	<table class="items">
	    		<tr>
	    			<th></th>
	    			<th>${message(code:'equipment.status.label')}</th>
            		<th>${message(code:'equipment.previous.status.label')}</th>
	    			<th>${message(code:'equipment.status.date.of.event.label')}</th>
	    			<th>${message(code:'equipment.status.recordedon.label')}</th>
	    			<th>${message(code:'equipment.disposal.ref.number.label')}</th>
	    			<th>${message(code:'equipment.status.changed.by.label')}</th>
	    			<th>${message(code:'equipment.status.current.label')}</th>
	    		</tr>
	    		<g:each in="${equipment.status.sort{a,b -> (a.dateCreated > b.dateCreated) ? -1 : 1}}" status="i" var="status">
		    		<g:if test="${i+1<numberOfStatusToDisplay}">
			    		<tr>
			    			<td>
				    		<ul>
								<li>
									<a href="${createLinkWithTargetURI(controller:'equipmentStatus', action:'delete', params:[id: status.id,'equipment': equipment?.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
								</li>
							</ul>
			    			</td>
			    			<td>${message(code: status?.status?.messageCode+'.'+status?.status?.name)}</td>
                			<td>${status?.previousStatus != null && status?.previousStatus != status?.status? message(code: status?.previousStatus?.messageCode+'.'+status?.previousStatus?.name):''}</td>
			    			<td>${Utils.formatDate(status?.dateOfEvent)}</td>
			    			<td>${Utils.formatDateWithTime(status?.dateCreated)}</td>
			    			<td>${status?.disposalRefNumber}</td>
			    			<td>${status?.changedBy?.names}</td>
			    			<td>${(status==equipment.timeBasedStatus)? '\u2713':''}</td>
			    		</tr>
		    		</g:if>
	    		</g:each>
	    	</table>
	    	<br />
	  	    	<a href="${createLinkWithTargetURI(controller:'equipmentStatus', action:'create', params:['equipment.id': equipment?.id])}" class="next medium gray">
	  	    		<g:message code="equipment.change.status.label" default="Change Status"/>
	  	    	</a>
	  	    	<a href="${createLinkWithTargetURI(controller:'equipmentStatus', action:'list', params:['equipment.id': equipment?.id])}">
	  	    		<g:message code="equipment.see.all.status.label" default="See all status"/>
	  	    	</a>
  	    	<br />
     		</g:if>
     	</fieldset>
      </div> 
      <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
           <img src="${resource(dir:'images/icons',file:'star_small.png')}">
          </span>
          <g:message code="equipment.section.supplier.information.label" default="Supplier Information"/>
        </h4>
      	<g:selectFromList name="supplier.id" label="${message(code:'provider.type.supplier')}" bean="${equipment}" field="supplier" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'SUPPLIER'])}"
  			from="${suppliers}" value="${equipment?.supplier?.id}" values="${suppliers.collect{it.contact?.contactName}}" />		
    		<g:selectFromEnum name="purchaser" bean="${equipment}" values="${PurchasedBy.values()}" field="purchaser" label="${message(code:'equipment.purchaser.label')}"/>
    		<div class="donor-information">
	    		<g:selectFromEnum name="donor" bean="${equipment}" values="${Donor.values()}" field="donor" label="${message(code:'equipment.donor.label')}"/>
	    		<g:input name="donorName"  label="${message(code:'equipment.donor.name.label')}" bean="${equipment}" field="donorName"/>
    		</div>
    		<g:input name="contractNumber" label="${message(code:'equipment.contract.number.label')}" bean="${equipment}" field="contractNumber"/>	
    		<g:currency costName="purchaseCost" id="purchase-cost" costLabel="${message(code:'equipment.purchase.cost.label')}" bean="${equipment}" costField="purchaseCost"  currencyName="currency" values="${currencies}" currencyField="currency" currencyLabel="${message(code:'equipment.currency.label')}"/>
     		<g:input name="purchaseDate" dateClass="date-picker" label="${message(code:'equipment.purchase.date.label')}" bean="${equipment}" field="purchaseDate"/>
     	</fieldset>
     	 <div id="form-aside-supplier" class="form-aside">
       		<g:if test="${equipment?.supplier != null}">
       	  		<g:render template="/templates/providerFormSide" model="['provider':equipment?.supplier,'type':equipment?.supplier?.type,'label':'provider.supplier.details','cssClass':'current','field':'supplier']" />
         	</g:if>
          <g:else>
            <g:each in="${suppliers}" var="provider">
                <g:render template="/templates/providerFormSide" model="['provider':provider,'type':provider.type,'label':'provider.supplier.details','cssClass':'form-aside-hidden','field':'supplier' ]" />
            </g:each>
         </g:else> 
       	</div>
      </div>
      <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
            <img src="${resource(dir:'images/icons',file:'star_small.png')}" alt="Section"/>
          </span>
          <g:message code="equipment.section.warranty.information.label" default="Warranty Information"/>
        </h4>
		<div class="error-list"><g:renderErrors bean="${equipment}" field="warranty"/></div>
		
        <g:inputBox name="warranty.sameAsSupplier"  label="${message(code:'equipment.same.as.supplier.label')}" bean="${equipment}" field="warranty.sameAsSupplier" checked="${(equipment.warranty?.sameAsSupplier)? true:false}"/>
      	<g:input name="warranty.startDate" dateClass="date-picker" label="${message(code:'warranty.start.date.label')}" bean="${equipment}" field="warranty.startDate"/>
    	<g:inputYearMonth name="warrantyPeriod" field="warrantyPeriod" years="${equipment.warrantyPeriod?.years}" months="${equipment.warrantyPeriod?.months}" bean="${equipment}" label='equipment.warranty.period.label'/>
      	<g:address  bean="${equipment}" warranty="true" field="warranty.contact"/>
     	<g:i18nTextarea name="warranty.descriptions" bean="${equipment}" label="${message(code:'warranty.descriptions.label')}" field="warranty.descriptions" height="150" width="300" maxHeight="150" />	 			
  		</fieldset> 
      </div>
      <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
            <img src="${resource(dir:'images/icons',file:'star_small.png')}" alt="Section"/>
          </span>
          <g:message code="equipment.section.service.provider.information.label" default="Service Provider Information"/>
        </h4>
        <g:selectFromList name="serviceProvider.id" label="${message(code:'provider.type.serviceProvider')}" bean="${equipment}" field="serviceProvider" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'SERVICEPROVIDER'])}"
  			from="${serviceProviders}" value="${equipment.serviceProvider?.id}" values="${serviceProviders.collect{it.contact?.contactName}}" />
  			<g:input name="serviceContractStartDate" dateClass="date-picker" label="${message(code:'equipment.provider.startDate.label')}" bean="${equipment}" field="serviceContractStartDate"/>
  			<g:inputYearMonth name="serviceContractPeriod" field="serviceContractPeriod" years="${equipment.serviceContractPeriod?.years}" months="${equipment.serviceContractPeriod?.months}" bean="${equipment}" label='equipment.provider.period.label'/>
  		</fieldset> 
  		<div id="form-aside-serviceProvider" class="form-aside">
  	    	<g:if test="${equipment?.serviceProvider != null}">
  	    	 	  <g:render template="/templates/providerFormSide" model="['provider':equipment.serviceProvider,'type':equipment.serviceProvider?.type,'label':'provider.service.provider.details','cssClass':'current','field':'serviceProvider' ]" />
  	       </g:if>
           <g:else>
            <g:each in="${serviceProviders}" var="provider">
                <g:render template="/templates/providerFormSide" model="['provider':provider,'type':provider.type,'label':'provider.serviceProvider.details','cssClass':'form-aside-hidden','field':'serviceProvider' ]" />
            </g:each>
          </g:else> 
       	</div>
      </div>
  		<g:if test="${equipment.id != null}">
  			<input type="hidden" name="id" value="${equipment.id}"/>
  		</g:if>
  		
  		<div class="buttons">
  			<button type="submit"><g:message code="default.button.save.label"/></button>
  			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
  		</div>
  	</g:form>
  </div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		numberOnlyField();
		getToHide("${message(code:'equipment.purchase.cost.label')}","${message(code:'equipment.estimated.cost.label')}");
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}");

		//status==forDisposal
		if($("select[name=status]").val()!="FORDISPOSAL") $(".status-information").hide()
		$("select[name=status]").change(function(e){
			if($(this).val()=="FORDISPOSAL"){
				$(".status-information").slideDown()
			}else{
				$(".status-information").slideUp()
			}
		})
	});
</script>
