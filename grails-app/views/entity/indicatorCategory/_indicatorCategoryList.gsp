<table class="items">
  <thead>
    <tr>
      <th></th>
      <th><g:message code="indicatorCategory.code.label"/></th>
<th><g:message code="indicatorCategory.name.label"/></th>
<th><g:message code="indicatorCategory.redToYellowThreshold.label"/></th>
<th><g:message code="indicatorCategory.yellowToGreenThreshold.label"/></th>
</tr>
</thead>
<tbody>
<g:each in="${entities}" status="i" var="indicatorCategory">
  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
    <td>
      <ul class="horizontal">
        <li>
          <a href="${createLinkWithTargetURI(controller:'indicatorCategory', action:'edit', params:[id: indicatorCategory.id])}"  class="edit-button">
            <g:message code="default.link.edit.label" />
          </a>
        </li>
      </ul>
    </td>
    <td><g:stripHtml field="${indicatorCategory.code}" chars="25"/></td>
  <td>${indicatorCategory.names}</td>
  <td><g:formatNumber number="${indicatorCategory.redToYellowThreshold}" format="0%"/></td>
  <td><g:formatNumber number="${indicatorCategory.yellowToGreenThreshold}" format="0%"/></td>
  </tr>
</g:each>
</tbody>
</table>
<g:render template="/templates/pagination" />