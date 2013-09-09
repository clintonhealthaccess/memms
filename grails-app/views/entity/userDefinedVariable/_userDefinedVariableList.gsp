<table class="items">
  <thead>
    <tr>
      <th></th>
      <th><g:message code="userDefinedVariable.code.label"/></th>
<th><g:message code="userDefinedVariable.name.label"/></th>
<th><g:message code="userDefinedVariable.currentValue.label"/></th>
</tr>
</thead>
<tbody>
<g:each in="${entities}" status="i" var="userDefinedVariable">
  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
    <td>
      <ul class="horizontal">
        <li>
          <a href="${createLinkWithTargetURI(controller:'userDefinedVariable', action:'edit', params:[id: userDefinedVariable.id])}"  class="edit-button">
            <g:message code="default.link.edit.label" />
          </a>
        </li>
      </ul>
    </td>
    <td><g:stripHtml field="${userDefinedVariable.code}" chars="25"/></td>
  <td><g:stripHtml field="${userDefinedVariable.names}" chars="50"/></td>
  <td>${userDefinedVariable.currentValue}</td>
  </tr>
</g:each>
</tbody>
</table>
<g:render template="/templates/pagination" />