<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<input type="hidden" name="${name}" value="struct" />
	<label>${message(code:label)}</label>
	<div  class="time-date">
		<input type="text" class="idle-field ${dateClass}" id="${name +'_date'}" readonly="readonly" name="${name +'_date'}" value="${date}"/>
		<label for="${name +'time'}">${message(code:'entity.at.label')}</label>
	</div>
	<div class="time-date">
		<input type="text" class="idle-field ${timeClass}" id="${name +'_time'}" readonly="readonly" name="${name +'_time'}" value="${time}"/>
	</div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>
