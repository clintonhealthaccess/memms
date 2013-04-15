<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart" %>
<%@ page import="org.chai.memms.spare.part.SparePart.SparePartPurchasedBy" %>
<%@ page import="org.chai.memms.spare.part.SparePart.StockLocation" %>
<%@ page import="org.chai.memms.inventory.Equipment" %>
<div  class="entity-form-container togglable">
  <div class="heading1-bar">
		<h1>
			<g:if test="${sparePart.id != null}">
				<g:message code="default.edit.label" args="[message(code:'spare.part.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'spare.part.label')]" />
			</g:else>
		</h1>
		<g:locales/>
	</div>
	
	<div class="main">
  	<g:form url="[controller:'sparePart', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
  	
  	  <div class="form-section">
      	<fieldset class="form-content">
        	<h4 class="section-title">
            <span class="question-default">
              <img src="${resource(dir:'images/icons',file:'star_small.png')}">
            </span>
            <g:message code="spare.part.section.basic.information.label" default="Basic Information"/>
          </h4>
          		  			
        	<g:selectFromList name="type.id" label="${message(code:'spare.part.type.label')}" bean="${sparePart}" field="type" optionKey="id" multiple="false"
    			ajaxLink="${createLink(controller:'sparePartType', action:'getAjaxData', params: [type:'TYPE'])}"
    			from="${types}" value="${sparePart?.type?.id}" values="${types.collect{it.names}}" />
    			
    		<g:input name="serialNumber" label="${message(code:'spare.part.serial.number.label')}" bean="${sparePart}" field="serialNumber"/>
      		<g:input name="manufactureDate" dateClass="date-picker" label="${message(code:'spare.part.manufacture.date.label')}" bean="${sparePart}" field="manufactureDate"/>
      		<g:i18nInput name="names" bean="${sparePart}" label="${message(code:'spare.part.names.label')}" field="names" />
      		<g:i18nTextarea name="descriptions" bean="${sparePart}" label="${message(code:'entity.comments.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
     
      	</fieldset>
      	
     	<div id="form-aside-type" class="form-aside">
      	  <g:if test="${sparePart?.type != null}">
      	 	  <g:render template="/templates/sparePartFormSide" model="['type':sparePart?.type,'cssClass':'current','field':'type' ]" />
          </g:if>
        </div>
      </div>
      <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
            <img src="${resource(dir:'images/icons',file:'star_small.png')}" alt="Section"/>
          </span>
          <g:message code="spare.part.section.status.information.label" default="Status Information"/> 
        </h4>
      	<g:if test="${sparePart.id == null}">
      			<g:selectFromEnum name="statusOfSparePart" bean="${cmd}" values="${StatusOfSparePart.values()}" field="statusOfSparePart" label="${message(code:'spare.part.status.label')}"/>
      			<div class="equipment-information">
      			<g:selectFromList name="usedOnEquipment.id" label="${message(code:'spare.part.used.on.equipment.label')}" bean="${sparePart}" field="usedOnEquipment" optionKey="id" multiple="false"
    			ajaxLink="${createLink(controller:'equipmentView', action:'getAjaxData', params: [type:'EQUIPMENT'])}"
    			from="${equipments}" value="${sparePart?.usedOnEquipment?.id}" values="${equipments.collect{it.type.names}} ${equipments.collect{it.serialNumber}} ${equipments.collect{it.dataLocation.names}}" />
    			</div>
      			<g:input name="dateOfEvent" dateClass="date-picker" label="${message(code:'spare.part.status.date.of.event.label')}" bean="${cmd}" field="dateOfEvent" value="${(sparePart.id!=null)?:Utils.formatDate(now)}"/>
      	</g:if>
      	<g:if test="${sparePart?.status!=null}">
	    	<table class="items">
	    		<tr>
	    			<th></th>
	    			<th>${message(code:'spare.part.status.label')}</th>
	    			<th>${message(code:'spare.part.status.date.of.event.label')}</th>
	    			<th>${message(code:'spare.part.status.recordedon.label')}</th>
	    			<th>${message(code:'spare.part.status.current.label')}</th>
	    		</tr>
	    		<g:each in="${sparePart.status.sort{a,b -> (a.dateCreated > b.dateCreated) ? -1 : 1}}" status="i" var="status">
		    		
			    		<tr>
			    			<td>
				    		<ul>
								<li>
									<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'delete', params:[id: status.id,'sparePart': sparePart?.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
								</li>
							</ul>
			    			</td>
			    			<td>${message(code: status?.statusOfSparePart?.messageCode+'.'+status?.statusOfSparePart?.name)}</td>
			    			<td>${Utils.formatDate(status?.dateOfEvent)}</td>
			    			<td>${Utils.formatDateWithTime(status?.dateCreated)}</td>
			    			<td>${(status==sparePart.timeBasedStatus)? '\u2713':''}</td>
			    		</tr>
		    		
	    		</g:each>
	    	</table>
	    	<br />
	  	    	<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'create', params:['sparePart.id': sparePart?.id])}" class="next medium gray">
	  	    		<g:message code="spare.part.change.status.label" default="Change Status"/>
	  	    	</a>
	  	    	<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'list', params:['sparePart.id': sparePart?.id])}">
	  	    		<g:message code="spare.part.see.all.status.label" default="See all status"/>
	  	    	</a>
  	    	<br />
     		</g:if>
     	</fieldset>
     	<div id="form-aside-type" class="form-aside">
      	  <g:if test="${sparePart?.usedOnEquipment != null}">
      	 	  <g:render template="/templates/assignedEquipmentFormSide" model="['equipment':sparePart?.usedOnEquipment,'cssClass':'current','field':'usedOnEquipment' ]" />
          </g:if>
        </div>
      </div>
     <div class="form-section-to-hide">
     <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
           <img src="${resource(dir:'images/icons',file:'star_small.png')}">
          </span>
          <g:message code="spare.part.section.location.information.label" default="Location Information"/>
        </h4>
       <g:selectFromEnum name="stockLocation" bean="${sparePart}" values="${StockLocation.values()}" field="stockLocation" label="${message(code:'spare.part.stockLocation.label')}"/>
      		
      		<div class="facility-information">
      		<g:selectFromList name="dataLocation.id" label="${message(code:'spare.part.dataLocation.label')}" bean="${sparePart}" field="dataLocation" optionKey="id" multiple="false"
    			ajaxLink="${createLink(controller:'dataLocation', action:'getAjaxData', params: [class:'dataLocation'])}"
    			from="${dataLocations}" value="${sparePart?.dataLocation?.id}" values="${dataLocations.collect{it.names}}" />
    		</div>
    		<g:input name="room" label="${message(code:'spare.part.room.label')}" bean="${sparePart}" field="room"/>
    		<g:input name="shelve" label="${message(code:'spare.part.shelve.label')}" bean="${sparePart}" field="shelve"/>	
      		</fieldset>
     	 <div id="form-aside-dataLocation" class="form-aside">
     		<g:if test="${sparePart?.dataLocation != null}">
     	  		 <g:render template="/templates/dataLocationFormSide" model="['dataLocation':sparePart?.dataLocation,'cssClass':'current','field':'dataLocation' ]" />
       		</g:if>
       	</div>
      </div>
      </div> 
      <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
           <img src="${resource(dir:'images/icons',file:'star_small.png')}">
          </span>
          <g:message code="spare.part.section.supplier.information.label" default="Supplier Information"/>
        </h4>
        <g:selectFromList name="supplier.id" label="${message(code:'provider.type.supplier')}" bean="${sparePart}" field="supplier" optionKey="id" multiple="false"
  			ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'SUPPLIER'])}"
  			from="${suppliers}" value="${sparePart?.supplier?.id}" values="${suppliers.collect{it.contact?.contactName}}" />		
  			<g:input name="purchaseDate" dateClass="date-picker" label="${message(code:'spare.part.purchase.date.label')}" bean="${sparePart}" field="purchaseDate"/>
    		<g:selectFromEnum name="sparePartPurchasedBy" bean="${sparePart}" values="${SparePartPurchasedBy.values()}" field="sparePartPurchasedBy" label="${message(code:'spare.part.sparePartPurchasedBy.label')}"/>
    		<g:if test="${sparePart?.sparePartPurchasedBy != SparePartPurchasedBy.BYPARTNER}">
    			<g:currency costName="purchaseCost" id="purchase-cost" costLabel="${message(code:'spare.part.purchase.cost.label')}" bean="${sparePart}" costField="purchaseCost"  currencyName="currency" values="${currencies}" currencyField="currency" currencyLabel="${message(code:'spare.part.currency.label')}"/>
     		</g:if>
     		<g:else>
     		<g:currency costName="purchaseCost" id="purchase-cost" costLabel="${message(code:'spare.part.estimated.purchase.cost.label')}" bean="${sparePart}" costField="purchaseCost"  currencyName="currency" values="${currencies}" currencyField="currency" currencyLabel="${message(code:'spare.part.estmated.currency.label')}"/>
     		</g:else>
     	</fieldset>
     	 <div id="form-aside-supplier" class="form-aside">
     		<g:if test="${sparePart?.supplier != null}">
     	  		<g:render template="/templates/providerFormSide" model="['provider':sparePart?.supplier,'type':sparePart?.supplier?.type,'label':'provider.supplier.details','cssClass':'current','field':'supplier']" />
       		</g:if>
       	</div>
      </div>
      
      <div class="form-section">
      	<fieldset class="form-content">
      	<h4 class="section-title">
          <span class="question-default">
            <img src="${resource(dir:'images/icons',file:'star_small.png')}" alt="Section"/>
          </span>
          <g:message code="spare.part.section.warranty.information.label" default="Warranty Information"/>
        </h4>
		<g:inputBox name="sameAsManufacturer"  label="${message(code:'spare.part.same.as.manufacturer.label')}" bean="${sparePart}" field="sameAsManufacturer" checked="${(sparePart?.sameAsManufacturer)? true:false}"/>
      	<g:input name="warranty.startDate" dateClass="date-picker" label="${message(code:'warranty.start.date.label')}" bean="${sparePart}" field="warranty.startDate"/>
    	<g:inputYearMonth name="warrantyPeriod" field="warrantyPeriod" years="${sparePart.warrantyPeriod?.years}" months="${sparePart.warrantyPeriod?.months}" bean="${sparePart}" label='spare.part.warranty.period.label'/>
      
      	<g:address  bean="${sparePart}" warranty="true" field="warranty.contact"/>
     	<g:i18nTextarea name="warranty.descriptions" bean="${sparePart}" label="${message(code:'warranty.descriptions.label')}" field="warranty.descriptions" height="150" width="300" maxHeight="150" />	 			
  		
  		</fieldset> 
      </div>
  		<g:if test="${sparePart.id != null}">
  			<input type="hidden" name="id" value="${sparePart.id}"/>
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
		getToHide("${message(code:'spare.part.purchase.cost.label')}","${message(code:'spare.part.estimated.cost.label')}");
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}");
	});
</script>
