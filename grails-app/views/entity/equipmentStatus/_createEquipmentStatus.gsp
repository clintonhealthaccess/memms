<%@ page import="org.chai.memms.equipment.EquipmentStatus.Status" %>
<%@ page import="org.chai.memms.util.Utils" %>
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
			<label><g:message code="equipment.serial.number.label"/>:</label>${status.equipment.serialNumber}
		</div>
		<g:selectFromEnum name="status" bean="${status}" values="${Status.values()}" field="status" label="${message(code:'equipment.status.label')}"/>
   		<g:inputDate name="dateOfEvent" precision="minute" default="none" dateFormatLabel = "${message(code:'entity.date.format.time.label')}" value="${status.dateOfEvent}" id="date-of-event" label="${message(code:'equipment.status.date.of.event.label')}" bean="${status}" field="dateOfEvent"/>
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
    			<th>${message(code:'equipment.status.label')}</th>
    			<th>${message(code:'equipment.status.date.of.event.label')}</th>
    			<th>${message(code:'equipment.status.recordedon.label')}</th>
    			<th>${message(code:'equipment.status.current.label')}</th>
    		</tr>
    		<g:each in="${equipment.status.sort{a,b -> (a.current > b.current) ? -1 : 1}}" status="i" var="status">
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
		    			<td>${Utils.formatDate(status?.dateOfEvent)}</td>
		    			<td>${Utils.formatDate(status?.statusChangeDate)}</td>
		    			<td>${(status.current)? '\u2713':'X'}</td>
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

