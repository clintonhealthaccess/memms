<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label for="${name}">${label}:</label>
	<input type="${type}" class="idle-field ${dateClass}" name="${name}" value="${fieldValue(bean:bean,field:field)}" ${active} ${readonly?'readonly="readonly"':''}/><label class="has-helper"></label>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>