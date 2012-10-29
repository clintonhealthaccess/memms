<%@ page import="org.chai.task.Task.TaskStatus" %>
<%@ page import="org.chai.memms.security.User" %>
<table class="items spaced">
	<thead>
		<tr>
			<th/>
		    <th><g:message code="task.user.label"/></th>
			<th><g:message code="task.class.label"/></th>
			<th><g:message code="task.information.label"/></th>
			<th><g:message code="task.senttoqueue.label"/></th>
			<th><g:message code="task.numberoftries.label"/></th>
			<g:sortableColumn property="status" params="[q:q]" title="${message(code: 'task.status.label')}" />
			<g:sortableColumn property="added" params="[q:q]" title="${message(code: 'task.added.label')}" defaultOrder="desc" />
			<th><g:message code="task.progress.bar.label"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="task"> 
			<tr id="js_task-${task.id}" class="js_task-entry ${(i % 2) == 0 ? 'odd' : 'even'}" data-id="${task.id}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'task', action:'delete', params:[id: task.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td>${task.principal!=null?User.findByUuid(task.principal)?.username:''}</td>   				
				<td>${task.class.simpleName}</td>
				<td>${task.information}</td>
				<td>${task.sentToQueue}</td>
				<td>${task.numberOfTries}</td>
				<td class="js_task-status">${task.status}</td>
				<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${task.added}"/></td>
				<td><span class="js_progress-bar ${task.status != TaskStatus.IN_PROGRESS?'hidden':''}">${task.retrievePercentage()}</span></td>
				<td>
					<g:if test="${task.status == TaskStatus.COMPLETED && task.outputFilename != null}">
 					<a href="${createLink(controller:'task', action:'downloadOutput', params:[id: task.id])}"><g:message code="task.output.download.label"/></a>
			</g:if>
			<g:else>
				-
			</g:else>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
