<table class="items">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.names.label"/></th>
			<g:sortableColumn property="username" title="${message(code: 'user.username.label')}" params="[q:q]" />
			<g:sortableColumn property="email" title="${message(code: 'user.email.label')}" params="[q:q]" />
			<g:sortableColumn property="location" title="${message(code: 'location.label')}" params="[q:q]" />
			<th><g:message code="user.permission.label"/></th>
			<th><g:message code="roles.label"/></th>
			<g:sortableColumn property="confirmed" title="${message(code: 'user.confirmed.label')}" params="[q:q]"  />
			<g:sortableColumn property="active" title="${message(code: 'user.active.label')}" params="[q:q]"  />
			<th/>
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
	           		</ul>
				</td>
				<td>${user.names}</td>
				<td>${user.username}</td>					
  				<td>${user.email}</td>
  				<td>${user.location?.names}</td>
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
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />