<div class="row ${hasErrors(bean:bean,field:field, 'errors')}">
	<g:each in="${locales}" var="locale" status="i">
		<div class="toggle-entry ${i!=0?'hidden':''}" data-toggle="${locale}">
			<g:set var="fieldLang" value="${field+'_'+locale}"/>
				<label for="${name+'_'+locale}">${label} (${locale})</label>
				<textarea type="${type}" class="idle-field in-input" name="${name+'_'+locale}" rows="${rows}">${fieldLang.tokenize('.').inject(bean) {v, k -> v."$k"}}</textarea>
		</div>
	</g:each>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}"/></div>
</div>