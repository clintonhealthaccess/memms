<%@ page import="org.chai.memms.security.UserType" %>

<div >
	
	<div >
		<h3 >
		<g:if test="${user.id != null}">
				<g:message code="default.edit.label" args="[message(code:'user.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'user.label')]" />
			</g:else>
		</h3>
		<g:locales/>
	</div>
	<div >
		<g:form url="[controller:'user', action:'save', params: [targetURI: targetURI]]" useToken="true">			
			<g:input name="username" label="${message(code:'user.username.label')}" bean="${user}" field="username"/>
			
			<g:input name="firstname" label="${message(code:'user.firstname.label')}" bean="${user}" field="firstname"/>
			<g:input name="lastname" label="${message(code:'user.lastname.label')}" bean="${user}" field="lastname"/>
			<g:input name="organisation" label="${message(code:'user.organisation.label')}" bean="${user}" field="organisation"/>
			<g:input name="email" label="${message(code:'user.email.label')}" bean="${user}" field="email"/>
			<g:input name="phoneNumber" label="${message(code:'user.phonenumber.label')}" bean="${user}" field="phoneNumber"/>
			<g:input name="permissionString" label="${message(code:'user.permission.label')}" bean="${user}" field="permissionString"/>
			<g:input name="password" label="${message(code:'user.password.label')}" type="password" bean="${cmd}" field="password"/>
			<g:input name="repeat" label="${message(code:'user.repeat.password.label')}" type="password" bean="${cmd}"  field="repeat"/>
			
			<g:selectFromEnum name="userType" bean="${user}" values="${UserType.values()}" field="userType" label="${message(code:'user.type.label')}"/>
			
			<g:selectFromList name="location.id" label="${message(code:'datalocation.label')}" bean="${user}" field="location" optionKey="id" multiple="false"
				ajaxLink="${createLink(controller:'location', action:'getAjaxData', params:[class: 'CalculationLocation'])}"
				from="${dataLocations}" value="${user.location?.id}" values="${dataLocations.collect{it.names}}" />
						
			<div>
				<label><g:message code="user.confirmed.label"/></label>
				<g:checkBox name="confirmed" value="${user.confirmed}" />
			</div>
			
			<div>
				<label><g:message code="user.active.label"/></label>
				<g:checkBox name="active" value="${user.active}" />
			</div>
			
			<g:selectFromList name="roles" label="${message(code:'roles.label')}" bean="${user}" field="roles" 
				from="${roles}" value="${user.roles*.id}" optionValue="name" optionKey="id" multiple="true"/>
			
			<g:if test="${user.id != null}">
				<input type="hidden" name="id" value="${user.id}"/>
			</g:if>
			
			<div>
				<button type="submit"><g:message code="default.button.save.label"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
			</div>
		</g:form>
	</div>
</div>
