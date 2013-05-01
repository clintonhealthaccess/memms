<div class="filters main">
  <h2><g:message code="notification.filter.label" /><a href="#" id="showhide" class="right"><g:message code="entity.show.hide.filter.label" /></a></h2>

	<g:hasErrors bean="${filterCmd}">
		<ul>
			<g:eachError var="err" bean="${filterCmd}">
				<h2><g:message error="${err}" /></h2>
			</g:eachError>
		</ul>
	</g:hasErrors>

	<g:form url="[controller:'notificationEquipment', action:'filter']" method="get" useToken="false" class="filters-box">
		<ul class="filters-list third">
      <li>
        <label><g:message code="notification.read.label" /></label>
        <select name="read">
          <option value=""><g:message code="default.please.select" /></option>
          <option value="true" ${filterCmd?.read?.equals("true")? 'selected' : ''}><g:message code="default.boolean.true" /></option>
          <option value="false" ${filterCmd?.read?.equals("false")? 'selected' : ''}><g:message code="default.boolean.false" /></option>
        </select>
      </li>
    </ul>
		<ul class="filters-list third">
      <li>
        <div class="half">
          <g:input name="from" dateClass="date-picker" label="${message(code:'notification.date.from.label')}" bean="${filterCmd}" field="from" value="${filterCmd?.from}"/>
        </div>
        <div class="half">
          <g:input name="to" dateClass="date-picker" label="${message(code:'notification.date.to.label')}" bean="${filterCmd}" field="to" value="${filterCmd?.to}"/>
        </div>
      </li>
		</ul>
    <div class="clear-left">
      <button type="submit"><g:message code="entity.filter.label" /></button>
      <a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
    </div>
  </g:form>
</div>
<g:if test="${params?.q}">
	<h2 class="filter-results">
		<g:message code="entity.filter.message.label" args="${[message(code: 'entity.notification.label'),params?.q]}" />
	</h2>
</g:if>
<script type="text/javascript">
	$(document).ready(function() {
		getDatePicker("${resource(dir:'images',file:'icon_calendar.png')}")
	});
</script>
