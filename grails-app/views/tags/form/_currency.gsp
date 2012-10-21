<div class="rows-wrapper can-be-hidden" id="purchase-cost">
	<div class="row ${hasErrors(bean:bean,field:costField,'errors')}">
		<label for="${costName}">${costLabel}</label>
		<input type="text" class="idle-field" name="${costName}" value="${fieldValue(bean:bean,field:costField)}" ${active} ${readonly?'readonly="readonly"':''}/>
		<div class="error-list"><g:renderErrors bean="${bean}" field="${costField}" /></div>
	</div>
	<div class="row ${hasErrors(bean:bean,field:currencyField,'errors')}">
		<label for="${currencyName}">${currencyLabel}</label>
		<select name="${currencyName}">
			<g:each in="${values}" var="enume">
				<option value="${enume.key}" ${enume.key+''==fieldValue(bean:bean, field:currencyField)+''?'selected="selected"':''}>
					${message(code:'currency.'+enume.value)}
				</option>
			</g:each>
		</select>
		<div class="error-list"><g:renderErrors bean="${bean}" field="${currencyField}"/></div>
	</div>
</div>
