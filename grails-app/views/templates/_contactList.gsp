<table class="items">
	<thead>
		<tr>
			<th><g:message code="entity.names.label"/></th>
			<th><g:message code="user.phonenumber.label"/></th>
			<th><g:message code="user.email.label"/></th>
			<th><g:message code="user.organisation.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="user">
			<tr>
				<td>${user.names}</td>
				<td>${user.phoneNumber}</td>				
  				<td>${user.email}</td>
  				<td>${user.organisation}</td>
			</tr>
		</g:each>
	</tbody>
</table>