<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label for="${name}">${label}</label>
	<select name="${name}">
		<g:each in="${values}" var="enume">
			<option value="${enume.key}" ${enume.key+''==fieldValue(bean:bean, field:field+'.key')+''?'selected="selected"':''}>
				${enume.key}
			</option>
		</g:each>
	</select>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}"/></div>
</div>