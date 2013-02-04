<div class="row ${hasErrors(bean:bean,field:field, 'errors')}">
	<g:each in="${locales}" var="locale" status="i">
		<div class="toggle-entry ${i!=0?'hidden':''}" data-toggle="${locale}">
			<label for="${name}.${locale}">${label} (${locale}) :</label>
			<g:set var="fieldLang" value="${field+'_'+locale}"/>	
			<textarea type="${type}" class="idle-field in-input" name="${name+'_'+locale}" rows="${rows}">${fieldValue(bean:bean,field:field)}</textarea>	
		</div>
	</g:each>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}"/></div>
</div>