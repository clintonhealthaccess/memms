<div class="rows-wrapper">
	<div class="row ${hasErrors(bean:bean,field:inputField,'errors')}">
		<label for="${inputField}">${label}</label>
		<input type="text" class="idle-field numbersOnly small-text-field" name="${inputField}" value="${fieldValue(bean:bean,field:inputField)}"/>
	</div>
	<div class="row ${hasErrors(bean:bean,field:selectField,'errors')}">
		<select name="${selectField}" ${readonly?'disabled="disabled"':''}>
			<g:each in="${selectFieldValues}" var="enume">
				<option value="${enume.key}" ${enume.key+''==fieldValue(bean:bean, field:selectField+'.key')+''?'selected="selected"':''}>
					${message(code: enume?.messageCode+'.'+enume?.name)}
				</option>
			</g:each>
		</select>
		<div class="error-list"><g:renderErrors bean="${bean}" field="${inputFieldName}"/></div>
		<div class="error-list"><g:renderErrors bean="${bean}" field="${selectField}" /></div>
	</div>
	
</div>
