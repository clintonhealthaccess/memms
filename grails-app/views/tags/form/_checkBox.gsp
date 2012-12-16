<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label for="${name}">${label} :</label>
	<span id="checkbox-${name}" class="add-equipment-form">
		<g:checkBox name="${name}" checked="${checked}" />
	</span>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>