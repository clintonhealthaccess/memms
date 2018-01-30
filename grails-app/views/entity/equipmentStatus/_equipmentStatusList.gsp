<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.inventory.EquipmentStatus.Status" %>
<table class="items">
	<thead>
		<tr>
			<th></th>
			<g:sortableColumn property="status" params="['equipment.id': equipment?.id]" title="${message(code: 'equipment.status.label')}" />
			<g:sortableColumn property="previousStatus" params="['equipment.id': equipment?.id]" title="${message(code: 'equipment.previous.status.label')}" />
			<g:sortableColumn property="dateOfEvent" params="['equipment.id': equipment?.id]" title="${message(code: 'equipment.status.date.of.event.label')}" />
			<g:sortableColumn property="dateCreated" params="['equipment.id': equipment?.id]" title="${message(code: 'equipment.status.recordedon.label')}" />
			<g:sortableColumn property="changedBy" params="['equipment.id': equipment?.id]" title="${message(code: 'equipment.status.changed.by.label')}" />
			<th><g:message code="equipment.status.current.label"/></th>
		</tr>
	</thead>
	<tbody>		
   		<g:each in="${entities}" status="i" var="status">
    		<tr  class="${(i % 2) == 0 ? 'odd' : 'even'}">
    			<td>
	    			<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'equipmentStatus', action:'delete', params:[id: status.id,equipment: status.equipment?.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button">
								<g:message code="default.link.delete.label" />
							</a>
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
   		</g:each>
	</tbody>
</table>