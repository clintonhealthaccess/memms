<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="register.title"/></title>
	</head>
	<body>
		<h2 class="login-heading"><g:message code="register.header.label"/></h2>
		<div class="form-box register">
			<g:form action="sendRegistration" class="nice-form login-form">
	    
  			<table class="listing">
  				<tbody>
  				  
  					<tr>
  					  <td>
  						  <label><g:message code="register.firstname.label"/></label>
    						<input type="text" name="firstname" value="${register?.firstname}" />
    						<div class="error-list"><g:renderErrors bean="${register}" field="firstname" /></div>
    					</td>
  					  <td>
  						  <label><g:message code="register.lastname.label"/></label>
    						<input type="text" name="lastname" value="${register?.lastname}" />
    						<div class="error-list"><g:renderErrors bean="${register}" field="lastname" /></div>
    					</td>
    				</tr>
				
  					<tr>
  					  <td>
  						  <label><g:message code="register.organisation.label"/></label>
    						<input type="text" name="organisation" value="${register?.organisation}" />
    						<div class="error-list"><g:renderErrors bean="${register}" field="organisation" />
    					</td>
  					  <td>
  						  <label><g:message code="register.email.label"/></label>
    						<input type="text" name="email" value="${register?.email}" />
    						<div class="error-list"><g:renderErrors bean="${register}" field="email" /></div>
    					</td>
    				</tr>
					
  					<tr>
  					  <td>
  						  <label><g:message code="register.phonenumber.label"/></label>
    						<input type="text" name="phoneNumber" value="${register?.phoneNumber}" />
    						<div class="error-list"><g:renderErrors bean="${register}" field="phoneNumber" /></div>
    					</td>
  						<td>
  						  <label><g:message code="register.language.label"/></label>
  							<g:select name="defaultLanguage" from="${languages}" value="${register?.defaultLanguage}" optionValue="displayLanguage"/>
  							<div class="error-list"><g:renderErrors bean="${register}" field="defaultLanguage" /></div>
  						</td>
  					</tr>
					
  					<tr>
  					  <td>
  						  <label><g:message code="register.password.label"/></label>
    						<input type="password" name="password" value="" />
    						<div class="error-list"><g:renderErrors bean="${register}" field="password" /></div>
    					</td>
  					  <td>
  						  <label><g:message code="register.repeat.label"/></label>
    						<input type="password" name="repeat" value="" />
    						<div class="error-list"><g:renderErrors bean="${register}" field="repeat" /></div>
    					</td>
    				</tr>
  					<tr>
  					  <td colspan="2">
  					    <br />
  						  <button type="submit">${message(code:'register.register.label')}</button>
    					</td>
    				</tr>
    				
  				</tbody>
  			</table>
  			
  		</g:form>
  	</div>
	</body>
</html>
