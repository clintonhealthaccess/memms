<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<input type="hidden" name="${name}" value="struct" />
	<label>${message(code:label)}</label>
	<div  class="period-month-year">
		<input type="text" class="idle-field numbers-only ${dateClass}" id="${name +'_hours'}" maxlength="${maxlength}" name="${name +'_hours'}" value="${hours}"/>
		<label for="${name +'_hours'}">${message(code:'entity.hours.label')}</label>
	</div>
	<div class="period-month-year">
		<input type="text" class="idle-field numbers-only ${dateClass}" id="${name +'_minutes'}" maxlength="${maxlength}" name="${name +'_minutes'}" value="${minutes}"/>
		<label for="${name +'_minutes'}">${message(code:'entity.minutes.label')}</label>
	</div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>
