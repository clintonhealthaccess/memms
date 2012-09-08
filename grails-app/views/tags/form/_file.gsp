<ul class="horizontal row ${hasErrors(bean:bean, field:'file', 'errors')} ${hasErrors(bean:bean, field:'encoding', 'errors')} ${hasErrors(bean:bean, field:'delimiter', 'errors')}">
	<li>
		<label for="file">${message(code:'import.file.label')}</label>
		<input type="file" class="idle-field" name="file" value="${fieldValue(bean:bean,field:'file')}"></input>
	</li>
	
	<li>
		<label for="encoding">${message(code:'import.encoding.label')}</label>
		<select name="encoding">
			<g:each in="${availableCharsets}" var="charset">
				<option value="${charset}" ${bean==null?(requestCharset.equals(charset)?'selected="selected"':''):fieldValue(bean:bean,field:'encoding')}>${charset}</option>
			</g:each>
		</select>
	</li>
	<li>
		<label for="delimiter">${message(code:'import.delimiter.label')}</label>
		<input type="text" name="delimiter" value="${bean==null?delimiter:fieldValue(bean:bean,field:'delimiter')}" style="width: 30px"></input>
	</li>
	
	<div class="error-list"><g:renderErrors bean="${bean}" field="file" /></div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="inputFilename" /></div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="encoding" /></div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="delimiter" /></div>
	<div class="error-list"><g:renderErrors bean="${bean}" field="fileType" /></div>
</ul>
<script type="text/javascript">
	$(document).ready(function(){
		$("input[type=file]").attr("value", "")
	});
</script>