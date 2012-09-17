
<table class="items">
	<thead>
		<tr>
			<th/>
			<th><g:message code="user.username.label"/></th>
			<th><g:message code="user.email.label"/></th>
			<th><g:message code="user.permission.label"/></th>
			<th><g:message code="roles.label"/></th>
			<g:sortableColumn property="confirmed" params="[q:q]" title="${message(code: 'user.confirmed.label')}" />
			<g:sortableColumn property="active" params="[q:q]" title="${message(code: 'user.active.label')}" />
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="user">
			<tr>
				<td>
            		<ul class="horizontal">
		           		<li>
		           			<a href="${createLinkWithTargetURI(controller:user.class.simpleName, action:'edit', params:[id: user.id])}" class="edit-button"><g:message code="default.link.edit.label" /></a>
						</li>
		           		<li>
		           			<a href="${createLinkWithTargetURI(controller:'user', action:'delete', params:[id:user.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');" class="delete-button"><g:message code="default.link.delete.label" /></a>
						</li>
	           		</ul>
				</td>
				<td>${user.username}</td>					
  				<td>${user.email}</td>
  				<td><g:stripHtml field="${user.permissionString}" chars="30"/></td>
  				<td>${user.roles}</td>
  				<td>${user.confirmed?'\u2713':''}</td>
  				<td>${user.active?'\u2713':''}</td>
  				<td>
  					<g:if test="${user.canActivate()}">
  						<a href="${createLinkWithTargetURI(controller:'auth', action:'activate', params:[id:user.id,targetUri:'/user/list'])}">activate</a>
  					</g:if>
  				</td>
			</tr>
		</g:each>
	</tbody>
</table>