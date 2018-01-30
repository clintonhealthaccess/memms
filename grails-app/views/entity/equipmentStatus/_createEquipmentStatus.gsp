<%@ page import="org.chai.memms.inventory.EquipmentStatus.Status" %>
<%@ page import="org.chai.memms.util.Utils" %>
<div  class="entity-form-container togglable">
<div class="heading1-bar">
	<h1>
		<g:message code="default.new.label" args="[message(code:'equipment.status.label')]"/>
	</h1>
	<g:locales/>
</div>

<div class="main">	
	<g:form url="[controller:'equipmentStatus', action:'save', params:[targetURI: targetURI]]" useToken="true" class="simple-list">
		<div class="row">
			<input type="hidden" name="equipment.id" value="${status.equipment.id}"/>
			<label><g:message code="entity.code.label"/>:</label>${status.equipment.code}
		</div>
		<div class="row">
			<label><g:message code="equipment.serial.number.label"/>:</label>${status.equipment.serialNumber}
		</div>
		<g:selectFromEnum name="status" bean="${status}" values="${Status.values()}" field="status" label="${message(code:'equipment.status.label')}"/>
		<g:input name="dateOfEvent" dateClass="date-picker" label="${message(code:'equipment.status.date.of.event.label')}" bean="${status}" field="dateOfEvent" value="${(status.id!=null)?:Utils.formatDate(now)}"/>
    	<g:i18nTextarea name="reasons" bean="${status}" label="${message(code:'equipment.status.reason')}" field="reasons" height="150" width="300" maxHeight="150" />
		
		<g:if test="${status.id != null}">
			<input type="hidden" name="id" value="${status.id}"></input>
		</g:if>
		<br/>
		<div class="buttons">
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
	<g:if test="${status.equipment!=null}">
    	<table class="items">
    		<tr>
    			<th></th>
    			<th><g:message code="equipment.status.label"/></th>
    			<th><g:message code="equipment.previous.status.label"/></th>
    			<th><g:message code="equipment.status.date.of.event.label"/></th>
    			<th><g:message code="equipment.status.recordedon.label"/></th>
    			<th><g:message code="equipment.status.changed.by.label"/></th>
    			<th><g:message code="equipment.status.current.label"/></th>
    		</tr>
    		<g:each in="${equipment.status.sort{a,b -> (a.dateCreated > b.dateCreated) ? -1 : 1}}" status="i" var="status">
	    		<g:if test="${i+1<numberOfStatusToDisplay}">
		    		<tr>
		    			<td>
			    		<ul>
							<li>
								<a href="${createLinkWithTargetURI(controller:'equipmentStatus', action:'delete', params:[id: status.id,'equipment.id': equipment?.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
							</li>
						</ul>
		    			</td>
		    			<td>${message(code: status?.status?.messageCode+'.'+status?.status?.name)}</td>
		    			 <td>${status?.previousStatus != null && status?.previousStatus != status?.status? message(code: status?.previousStatus?.messageCode+'.'+status?.previousStatus?.name):''}</td>
		    			<td>${Utils.formatDate(status?.dateOfEvent)}</td>
		    			<td>${Utils.formatDateWithTime(status?.dateCreated)}</td>
		    			<td>${status?.changedBy?.names}</td>
		    			<td>${(status==equipment.timeBasedStatus)? '\u2713':''}</td>
		    		</tr>
		    	</g:if>
    		</g:each>
    	</table>
    	<br/>
    	<a href="${createLinkWithTargetURI(controller:'equipmentStatus', action:'list', params:['equipment.id': equipment?.id])}">
  	    		<g:message code="equipment.see.all.status.label" default="See all status"/>
  	    	</a>
   	</g:if>
</div>
</div>
<script type="text/javascript">
	$(document).ready(function() {		
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
	});
</script>
