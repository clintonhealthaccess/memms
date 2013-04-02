<%@ page import="org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart" %>
<%@ page import="org.chai.memms.inventory.Equipment" %>
<%@ page import="org.chai.memms.util.Utils" %>
<div  class="entity-form-container togglable">
<div class="heading1-bar">
	<h1>
		<g:message code="default.new.label" args="[message(code:'spare.part.status.of.spare.part.label')]"/>
	</h1>
	<g:locales/>
</div>

<div class="main">
 <div class="form-section">
	<fieldset class="form-content">	
	<g:form url="[controller:'sparePartStatus', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">	
		<div class="row">
			<input type="hidden" name="sparePart.id" value="${status.sparePart.id}"/>
			<label><g:message code="entity.code.label"/>:</label>${status.sparePart.code}
		</div>
		<div class="row">
			<label><g:message code="spare.part.serial.number.label"/>:</label>${status.sparePart.serialNumber}
		</div>
		<g:selectFromEnum name="statusOfSparePart" bean="${statusOfSparePart}" values="${StatusOfSparePart.values()}" field="statusOfSparePart" label="${message(code:'spare.part.status.of.spare.part.label')}"/>
		<div class="equipment-information">
      		<g:selectFromList name="usedOnEquipment.id" label="${message(code:'spare.part.used.on.equipment.label')}" bean="${sparePart}" field="usedOnEquipment" optionKey="id" multiple="false"
    		ajaxLink="${createLink(controller:'equipmentView', action:'getAjaxData', params: [type:'EQUIPMENT'])}"
    		from="${equipments}" value="${sparePart?.usedOnEquipment?.id}" values="${equipments.collect{it.descriptions}}" />
    	</div>
		<g:input name="dateOfEvent" dateClass="date-picker" label="${message(code:'spare.part.status.of.spare.part.date.of.event.label')}" bean="${statusOfSparePart}" field="dateOfEvent" value="${(status.id!=null)?:Utils.formatDate(now)}"/>
    	<g:i18nTextarea name="reasons" bean="${statusOfSparePart}" label="${message(code:'spare.part.status.of.spare.part.reason')}" field="reasons" height="150" width="300" maxHeight="150" />
		
		<g:if test="${status.id != null}">
			<input type="hidden" name="id" value="${status.id}"></input>
		</g:if>
		<br/>
		<div class="buttons">
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>	
	</g:form>
	</fieldset>
	<div id="form-aside-supplier" class="form-aside">
     	<g:if test="${sparePart?.supplier != null}">
     	  	<g:render template="/templates/providerFormSide" model="['provider':sparePart?.supplier,'type':sparePart?.supplier?.type,'label':'provider.supplier.details','cssClass':'current','field':'supplier']" />
       	</g:if>
    </div>
    <div id="equipment-information" class="form-aside">
      	<g:if test="${sparePart?.usedOnEquipment != null}">
      	 	<g:render template="/templates/assignedEquipmentFormSide" model="['equipment':sparePart?.usedOnEquipment,'cssClass':'current','field':'usedOnEquipment' ]" />
        </g:if>
    </div>
   </div>
      
	<g:if test="${status.sparePart!=null}">
    	<table class="items">
    		<tr>
    			<th></th>
    			<th><g:message code="spare.part.status.of.spare.part.label"/></th>
    			<th><g:message code="spare.part.status.of.spare.part.date.of.event.label"/></th>
    			<th><g:message code="spare.part.status.of.spare.part.recordedon.label"/></th>
    			<th><g:message code="spare.part.status.of.spare.part.current.label"/></th>
    		</tr>
    		<g:each in="${sparePart.status.sort{a,b -> (a.dateOfEvent > b.dateOfEvent) ? -1 : 1}}" status="i" var="status">
	    		
		    		<tr>
		    			<td>
			    		<ul>
							<li>
								<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'delete', params:[id: status.id,'sparePart.id': sparePart?.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
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
    	<br/>
    	<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'list', params:['sparePart.id': sparePart?.id])}">
  	    		<g:message code="spare.part.see.all.status.of.spare.part.label" default="See all status of spare part"/>
  	    	</a>
   	</g:if>
</div>
</div>
<script type="text/javascript">
	$(document).ready(function() {		
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
		getToHide();
	});
</script>
