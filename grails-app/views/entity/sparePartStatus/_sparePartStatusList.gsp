<%@ page import="org.chai.memms.util.Utils" %>
<%@ page import="org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart" %>
<table class="items">
	<thead>
		<tr>
			<th></th>
			<g:sortableColumn property="status" params="['sparePart.id': sparePart?.id]" title="${message(code: 'spare.part.status.label')}" />
			<g:sortableColumn property="dateOfEvent" params="['sparePart.id': sparePart?.id]" title="${message(code: 'spare.part.status.date.of.event.label')}" />
			<g:sortableColumn property="statusChangeDate" params="['sparePart.id': sparePart?.id]" title="${message(code: 'spare.part.status.recordedon.label')}" />
			<th><g:message code="spare.part.status.current.label"/></th>
		</tr>
	</thead>
	<tbody>		
   		<g:each in="${entities}" status="i" var="status">
    		<tr  class="${(i % 2) == 0 ? 'odd' : 'even'}">
    			<td>
	    			<ul class="horizontal">
						<li>
							<a href="${createLinkWithTargetURI(controller:'sparePartStatus', action:'delete', params:[id: status.id,sparePart: status.sparePart?.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
    			</td>
    			<td>${message(code: status?.statusOfSparePart?.messageCode+'.'+status?.statusOfSparePart?.name)}</td>
    			<td>${Utils.formatDate(status?.dateOfEvent)}</td>
    			<td>${Utils.formatDateWithTime(status?.dateCreated)}</td>
    			<td>${(status==sparePart.timeBasedStatus)? '\u2713':''}</td>
    		</tr>
   		</g:each>
	</tbody>
</table>