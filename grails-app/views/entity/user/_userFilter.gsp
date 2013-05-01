<%@ page import="org.chai.memms.security.User" %>
<%@ page import="org.chai.memms.security.User.UserType" %>
<div class="filters main">
		  <h2><g:message code="user.filter.label" /><a href="#" id="showhide" class="right"><g:message code="entity.show.hide.filter.label" /></a></h2>
			<g:hasErrors bean="${filterCmd}">
				<ul>
					<g:eachError var="err" bean="${filterCmd}">
						<h2><g:message error="${err}" /></h2>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:form url="[controller:'user', action:'filter']" method="get" useToken="false" class="filters-box">
        <ul class="filters-list third">
          <li>
            <g:selectFromList name="location.id" label="${message(code:'location.label')}" bean="${filterCmd}" field="location" 
              optionKey="id" multiple="false" from="${locations}" value="${filterCmd?.location?.id}" 
              ajaxLink="${createLink(controller:'location', action:'getAjaxData', params:[class: 'CalculationLocation'])}" values="${locations.collect{it.name}}"/>
          </li>
          <li>
              <label><g:message code="user.active.label" /></label>
              <select name="active">
                <option value=""><g:message code="default.please.select" /></option>
                <option value="true" ${filterCmd?.active?.equals("true")? 'selected' : ''} ><g:message code="active.boolean.true" /></option>
                <option value="false" ${filterCmd?.active?.equals("false")? 'selected' : ''}><g:message code="active.boolean.false" /></option>
              </select>
          </li>
        </ul>
        <ul class="filters-list third">
          <li>
            <g:selectFromEnum name="userType" values="${UserType.values()}" field="userType" label="${message(code:'user.type.label')}" />
          </li>
          <li>
            <label><g:message code="user.confirmed.label" /></label>
            <select name="confirmed">
              <option value=""><g:message code="default.please.select" /></option>
              <option value="true" ${filterCmd?.confirmed?.equals("true")? 'selected' : ''} ><g:message code="confirmed.boolean.true" /></option>
              <option value="false" ${filterCmd?.confirmed?.equals("false")? 'selected' : ''}><g:message code="confirmed.boolean.false" /></option>
            </select>
          </li>
        </ul>
        <ul class="filters-list third">
          <li>
            <g:selectFromList name="role.id" label="${message(code:'roles.label')}" bean="${filterCmd}" field="role" 
              optionKey="id" multiple="false" from="${roles}" value="${filterCmd?.role?.id}" values="${roles.collect{it.name}}"/>
            </li>
        </ul>
        <div class='clear-left'>
          <button type="submit"><g:message code="entity.filter.label" /></button>
          <a href="#" class="clear-form"><g:message code="default.link.clear.form.label"/></a>
        </div>
		  </g:form>
</div>
<g:if test="${params?.q}">
	<h2 class="filter-results">
		<g:message code="entity.filter.message.label" args="${[message(code: 'user.label'),params?.q]}" />
	</h2>
</g:if>

