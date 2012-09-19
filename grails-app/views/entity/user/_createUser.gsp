<%@ page import="org.chai.memms.security.User.UserType" %>

<div>
	<div class="heading1-bar">
	  <g:locales/>
		<h1>
  		<g:if test="${user.id != null}">
				<g:message code="default.edit.label" args="[message(code:'user.label')]" />
			</g:if>
			<g:else>
				<g:message code="default.new.label" args="[message(code:'user.label')]" />
			</g:else>
		</h1>
	</div>
	
	<div class="main">
		<g:form url="[controller:'user', action:'save', params: [targetURI: targetURI]]" useToken="true" class="simple-list">
			<g:input name="username" label="${message(code:'user.username.label')}" bean="${user}" field="username"/>
			
			<g:input name="firstname" label="${message(code:'user.firstname.label')}" bean="${user}" field="firstname"/>
			<g:input name="lastname" label="${message(code:'user.lastname.label')}" bean="${user}" field="lastname"/>
			<g:input name="organisation" label="${message(code:'user.organisation.label')}" bean="${user}" field="organisation"/>
			<g:input name="email" label="${message(code:'user.email.label')}" bean="${user}" field="email"/>
			<g:input name="phoneNumber" label="${message(code:'user.phonenumber.label')}" bean="${user}" field="phoneNumber"/>
			<g:textarea name="permissionString"  label="${message(code:'user.permission.label')}" rows="5" bean="${user}" value="${user.permissionString}" field="permissionString" />
			
			<g:input name="password" label="${message(code:'user.password.label')}" type="password" bean="${cmd}" field="password"/>
			<g:input name="repeat" label="${message(code:'user.repeat.password.label')}" type="password" bean="${cmd}"  field="repeat"/>
			
			<g:selectFromEnum name="userType" bean="${user}" values="${UserType.values()}" field="userType" label="${message(code:'user.type.label')}"/>
			
			<g:selectFromList name="location.id" label="${message(code:'datalocation.label')}" bean="${user}" field="location" optionKey="id" multiple="false"
				ajaxLink="${createLink(controller:'location', action:'getAjaxData', params:[class: 'CalculationLocation'])}"
				from="${dataLocations}" value="${user.location?.id}" values="${dataLocations.collect{it.names}}" />
				
			<g:inputBox name="confirmed"  label="${message(code:'user.confirmed.label')}" bean="${user}" field="confirmed" value="${user.confirmed}" checked="${(user.confirmed)? true:false}"/>		
		
			<g:inputBox name="active"  label="${message(code:'user.active.label')}" bean="${user}" field="active" value="${user.active}" checked="${(user.active)? true:false}"/>		
		
			<g:selectFromList name="roles" label="${message(code:'roles.label')}" bean="${user}" field="roles" 
				from="${roles}" value="${user.roles*.id}" optionValue="name" optionKey="id" multiple="true"/>
			
			<g:if test="${user.id != null}">
				<input type="hidden" name="id" value="${user.id}"/>
			</g:if>
			<br/>
			<div class="buttons">
				<button type="submit"><g:message code="default.button.save.label"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
			</div>
		</g:form>
	</div>
</div>
