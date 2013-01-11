<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<input type="hidden" name="${name}" value="struct" />
	<label>${message(code:label)}</label>
	<div  class="period-month-year">
		<input type="text" class="idle-field numbers-only ${dateClass}" id="${name +'_years'}" maxlength="${maxlength}" name="${name +'_years'}" value="${years}" ${active} ${readonly?'readonly="readonly"':''}/>
		<label for="${name +'_years'}">${message(code:'entity.years.label')}</label>
	</div>
	<div class="period-month-year">
		<input type="text" class="idle-field numbers-only ${dateClass}" id="${name +'_months'}" maxlength="${maxlength}" name="${name +'_months'}" value="${months}" ${active} ${readonly?'readonly="readonly"':''}/>
		<label for="${name +'_months'}">${message(code:'entity.months.label')}</label>
	</div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>
