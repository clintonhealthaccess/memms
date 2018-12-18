<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.spare.part.SparePart.SparePartStatus" %>
<%@ page import="org.chai.memms.spare.part.SparePart.SparePartPurchasedBy" %>
<%@ page import="org.chai.memms.spare.part.SparePart.StockLocation" %>
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
        	<g:selectFromList name="type.id" label="${message(code:'spare.part.type.label')}" bean="${sparePart}" field="type" optionKey="id" multiple="false" ajaxLink="${createLink(controller:'sparePartType', action:'getAjaxData', params: [type:'TYPE'])}" from="${types}" value="${sparePart?.type?.id}" values="${types.collect{it.names}}" />
      		<g:i18nTextarea name="descriptions" bean="${sparePart}" label="${message(code:'entity.comments.label')}" field="descriptions" height="150" width="300" maxHeight="150" />
      	  <g:selectFromEnum name="status" bean="${sparePart}" values="${SparePartStatus.values()}" field="status" label="${message(code:'spare.part.status.label')}"/>
          <g:input name="orderedQuantity" label="${message(code:'spare.part.ordered.quantity')}" bean="${sparePart}" field="orderedQuantity" dateClass="numbers-only disabled-on-edit ordered-quantity"/>
          <g:input name="receivedQuantity" label="${message(code:'spare.part.received.quantity')}" bean="${sparePart}" field="receivedQuantity" dateClass="numbers-only disabled-on-edit received-quantity"/>
          <g:input name="inStockQuantity" label="${message(code:'spare.part.in.stock.quantity')}"  bean="${sparePart}" field="inStockQuantity" dateClass="numbers-only disabled-on-edit in-stock-quantity"/>
        </fieldset>
     	  <div id="form-aside-type" class="form-aside">
      	  <g:if test="${sparePart?.type != null}">
      	 	  <g:render template="/templates/sparePartFormSide" model="['type':sparePart?.type,'cssClass':'current','field':'type' ]" />
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
    		<g:input name="shelf" label="${message(code:'spare.part.shelf.label')}" bean="${sparePart}" field="shelf"/>	
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
  			ajaxLink="${createLink(controller:'provider', action:'getAjaxData', params: [type:'BOTH'])}"
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
    sparePartQuantityHandler("${sparePart?.id}","${message(code:'default.edit.label',args:[''])}","${message(code:'spare.part.warn.update.quantity.label')}");
    
    if($("select[name=status]").val()=="PENDINGORDER")  $(".received-quantity, .in-stock-quantity").parents("div.row").hide()
    $("select[name=status]").on("change",function(e){
      if($(this).val()=="PENDINGORDER") $(".received-quantity, .in-stock-quantity").parents("div.row").slideUp("slow")
      else $(".received-quantity, .in-stock-quantity").parents("div.row").slideDown("slow")
    });

	});
</script>
