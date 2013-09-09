<table class="items">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" title="${message(code: 'entity.code.label', default: 'Code')}" params="[q:q]" />
			<g:sortableColumn property="contact.contactName" title="${message(code: 'entity.name.label', default: 'Name')}" params="[q:q]" />
			<g:sortableColumn property="type" title="${message(code: 'entity.type.label', default: 'Type')}" params="[q:q]" />
			<g:sortableColumn property="contact.email" title="${message(code: 'contact.email.label', default: 'Email')}" params="[q:q]" />
			<th><g:message code="contact.phone.label"/></th>
			<th><g:message code="contact.pobox.label"/></th>
			<g:sortableColumn property="contact.city" title="${message(code: 'contact.city.label', default: 'City')}" params="[q:q]" />
			<g:sortableColumn property="contact.country" title="${message(code: 'contact.country.label', default: 'Country')}" params="[q:q]" />
			<th><g:message code="contact.address.descriptions.label"/></th>
		</tr>
	</thead>
	<g:render template="/entity/provider/listBody" model="[entities: entities]"/>
</table>
<g:render template="/templates/pagination" model="[entities:entities, entityCount:entities.totalCount]" />