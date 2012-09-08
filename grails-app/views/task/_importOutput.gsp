<g:message code="import.result.message.saved.rows" />: ${errorManager.numberOfSavedRows}
<g:message code="import.result.message.unsaved.rows" />: ${errorManager.numberOfUnsavedRows}
<g:message code="import.result.message.error.saved.rows" />: ${errorManager.numberOfRowsSavedWithError}
<g:set var="j" value="${0}"/><g:each in="${errorManager.errors}" var="error" status="i"><g:if test="${error.lineNumber != null && j != error.lineNumber}"><g:set var="j" value="${error.lineNumber}"/>${'\n'}<g:message code="import.result.filename" />: ${error.fileName} <g:message code="import.result.line" />: ${error.lineNumber}</g:if> 
 - <g:message code="${error.messageCode}"/><g:if test="${error.header != null && !error.header?.empty}">, <g:message code="import.result.column" />: ${error.header}</g:if></g:each>
