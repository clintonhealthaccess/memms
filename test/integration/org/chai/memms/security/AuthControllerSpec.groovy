package org.chai.memms.security

import javax.servlet.ServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.util.WebUtils;
import org.chai.memms.IntegrationTests;

class AuthControllerSpec extends IntegrationTests {

	def authController
	
	
	def "users get redirected to correct page after signin"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', new Sha256Hash('1234').toString(), true, true)
		setupSecurityManager(user)
		
		when:
		authController.params.username = 'test@test.com'
		authController.params.password = '1234'
		authController.params.targetUri = '/user/list'
		authController.signIn()
		
		then:
		authController.response.redirectedUrl == '/user/list' 	
	}

//	def "users that have default language set get redirected to correct page after signin"() {
//		setup:
//		authController = new AuthController()
//		def user = newUser('test@test.com', new Sha256Hash('1234').toString(), true, true)
//		user.defaultLanguage = 'fr'
//		setupSecurityManager(user)
//		
//		when:
//		authController.params.username = 'test@test.com'
//		authController.params.password = '1234'
//		authController.params.targetUri = '/user/list'
//		authController.signIn()
//		
//		then:
//		authController.response.redirectedUrl == '/user/list?lang=fr'
//	}
//	
//	def "users that have default language set get redirected to correct page after signin - with language already set"() {
//		setup:
//		authController = new AuthController()
//		def user = newUser('test@test.com', new Sha256Hash('1234').toString(), true, true)
//		user.defaultLanguage = 'fr'
//		setupSecurityManager(user)
//		
//		when:
//		authController.params.username = 'test@test.com'
//		authController.params.password = '1234'
//		authController.params.targetUri = '/user/list?lang=de&test=test'
//		authController.signIn()
//		
//		then:
//		authController.response.redirectedUrl == '/user/list?lang=fr&test=test'
//	}
//	
	def "register with wrong email address"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.params.email = '123'
		authController.sendRegistration()
		
		then:
		authController.modelAndView.model.register.email == '123'
		authController.modelAndView.model.register.errors.hasFieldErrors('email') == true
	}
	
	def "register with correct email address"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.params.email = 'test@test.com'
		authController.params.firstname = 'first'
		authController.params.lastname = 'last'
		authController.params.organisation = 'org'
		authController.params.defaultLanguage = 'fr'
		authController.params.phoneNumber = '+250 11 111 11 11'
		authController.params.password = '1234'
		authController.params.repeat = '1234'
		authController.sendRegistration()
		
		then:
		authController.response.redirectedUrl == '/auth/login'
		RegistrationToken.count() == 1
		User.count() == 1
		User.findByEmail('test@test.com').firstname == 'first'
		User.findByEmail('test@test.com').lastname == 'last'
		User.findByEmail('test@test.com').organisation == 'org'
		User.findByEmail('test@test.com').passwordHash == new Sha256Hash('1234').toString()
		User.findByEmail('test@test.com').uuid != null
	}
	
	def "register with already used email address"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		user.username = 'test'
		
		when:
		authController.params.email = 'test@test.com'
		authController.params.firstname = 'test'
		authController.params.lastname = 'test'
		authController.params.organisation = 'test'
		authController.params.phoneNumber = '+250 11 111 11 11'
		authController.params.password = '1234'
		authController.params.repeat = '1234'
		authController.sendRegistration()
		
		then:
		authController.modelAndView.model.register.email == 'test@test.com'
		authController.modelAndView.model.register.errors.hasFieldErrors('email') == true
	}
	
	def "register with already used username as email address"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		user.email = 'test'

		when:
		authController.params.email = 'test@test.com'
		authController.params.firstname = 'test'
		authController.params.lastname = 'test'
		authController.params.organisation = 'test'
		authController.params.phoneNumber = '+250 11 111 11 11'
		authController.params.password = '1234'
		authController.params.repeat = '1234'
		authController.sendRegistration()
		
		then:
		authController.modelAndView.model.register.email == 'test@test.com'
		authController.modelAndView.model.register.errors.hasFieldErrors('email') == true
	}
	
//	def "register with wrong phone number"() {
//		setup:
//		authController = new AuthController()
//		
//		when:
//		authController.params.email = 'test@test.com'
//		authController.params.firstname = 'test'
//		authController.params.lastname = 'test'
//		authController.params.organisation = 'test'
//		authController.params.phoneNumber = 'wrong'
//		authController.params.password = '1234'
//		authController.params.repeat = '1234'
//		authController.sendRegistration()
//		
//		then:
//		authController.modelAndView?.model?.register?.phoneNumber == 'wrong'
//		authController.modelAndView.model.register.errors.hasFieldErrors('phoneNumber') == true
//	}
	
	def "register with correct phone number"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.params.email = 'test@test.com'
		authController.params.firstname = 'test'
		authController.params.lastname = 'test'
		authController.params.organisation = 'test'
		authController.params.phoneNumber = '+250 11 111 11 11'
		authController.params.password = '1234'
		authController.params.repeat = '1234'
		authController.sendRegistration()
		
		then:
		User.count() == 1
		User.findByEmail('test@test.com').firstname == 'test'
		User.findByEmail('test@test.com').lastname == 'test'
		User.findByEmail('test@test.com').organisation == 'test'
		User.findByEmail('test@test.com').phoneNumber == '+250 11 111 11 11'
		User.findByEmail('test@test.com').passwordHash == new Sha256Hash('1234').toString()
		User.findByEmail('test@test.com').uuid != null
	}
	
	def "register properly saves all fields"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.params.email = 'test@test.com'
		authController.params.firstname = 'first'
		authController.params.lastname = 'last'
		authController.params.organisation = 'org'
		authController.params.defaultLanguage = 'fr'
		authController.params.phoneNumber = '+250 11 111 11 11'
		authController.params.password = '1234'
		authController.params.repeat = '1234'
		authController.sendRegistration()
		
		then:
		User.count() == 1
		User.findByEmail('test@test.com').firstname == 'first'
		User.findByEmail('test@test.com').lastname == 'last'
		User.findByEmail('test@test.com').defaultLanguage == 'fr'
		User.findByEmail('test@test.com').organisation == 'org'
		User.findByEmail('test@test.com').phoneNumber == '+250 11 111 11 11'
		User.findByEmail('test@test.com').passwordHash == new Sha256Hash('1234').toString()
		User.findByEmail('test@test.com').uuid != null
	}

	
	def "confirm account without token"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.confirmRegistration()
		
		then:
		authController.response.redirectedUrl == null
	}
	
	def "confirm account with valid token"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		new RegistrationToken(token: '123', user: user, used: false).save(failOnError: true)
		
		when:
		authController.params.token = '123'
		authController.confirmRegistration()
		
		then:
		authController.response.redirectedUrl == '/auth/login'
		User.findByEmail('test@test.com').confirmed == true
		RegistrationToken.count() == 1
		RegistrationToken.list()[0].used == true
	}
	
	def "confirm account with used token does not change user state"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		new RegistrationToken(token: '123', user:user, used:true).save(failOnError: true)
		
		when:
		authController.params.token = '123'
		authController.confirmRegistration()
		
		then:
		authController.response.redirectedUrl == '/auth/login'
		User.findByEmail('test@test.com').confirmed == false
		RegistrationToken.count() == 1
		RegistrationToken.list()[0].used == true
	}
	
	def "activate account with unconfirmed user"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		
		when:
		authController.params.id = user.id
		authController.params.targetURI = '/user/list'
		authController.activate()
		
		then:
		User.findByEmail('test@test.com').active == false
	}
	
	def "activate account with confirmed user"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, true)
		
		when:
		authController.params.id = user.id
		authController.params.targetUri = '/user/list'
		authController.activate()
		
		then:
		authController.response.redirectedUrl == '/user/list'
		User.findByEmail('test@test.com').active == true 
	}
	
	def "activate account with confirmed user deletes registration token"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, true)
		new RegistrationToken(token: '123', user:user, used:true).save(failOnError: true)
		
		when:
		authController.params.id = user.id
		authController.params.targetUri = '/user/list'
		authController.activate()
		
		then:
		authController.response.redirectedUrl == '/user/list'
		User.findByEmail('test@test.com').active == true
		RegistrationToken.count() == 0
	}
	
	def "retrieve password with wrong email address"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.params.email = '123'
		authController.retrievePassword()
		
		then:
		authController.modelAndView.model.retrievePassword.email == '123'
		authController.modelAndView.model.retrievePassword.errors.hasFieldErrors('email') == true
	}
	
	def "retrieve password with unknown user"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.params.email = 'test@test.com'
		authController.retrievePassword()
		
		then:
		authController.modelAndView.model.retrievePassword.email == 'test@test.com'
		authController.modelAndView.model.retrievePassword.errors.hasFieldErrors('email') == true
	}
	
	def "retrieve password with known user"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		
		when:
		authController.params.email = 'test@test.com'
		authController.retrievePassword()
		
		then:
		authController.response.redirectedUrl == '/auth/login'
	}
	
	def "create password token with known user - valid email"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		
		when:
		authController.params.email = 'test@test.com'
		authController.retrievePassword()
		
		then:
		PasswordToken.findByToken(user.passwordToken.token).count() == 1
		authController.response.redirectedUrl == '/auth/login'
	}
	
	
	def "create password token with known user deletes old token - valid email"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		new PasswordToken(token:"456", user:user).save()
		when:
		authController.params.email = 'test@test.com'
		authController.retrievePassword()
		
		then:
		PasswordToken.findByToken("456") == null
		PasswordToken.findByToken(user.passwordToken.token).count() == 1
		authController.response.redirectedUrl == '/auth/login'
	}
	
	def "new password without token 404"() {
		setup:
		setupSecurityManager(null)
		authController = new AuthController()
		
		when:
		authController.newPassword()
		
		then:
		authController.response.redirectedUrl == null
	}
	
	def "new password with valid token"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', true, true)
		new PasswordToken(token: '123', user:user).save(failOnError: true)
		
		when:
		authController.params.token = '123'
		authController.newPassword()
		
		then:
		authController.modelAndView.model.token == '123'
		authController.modelAndView.model.newPassword == null
		
	}
	
	def "new password with logged in user"() {
	
	}
	
	def "set password without token 404"() {
		setup:
		authController = new AuthController()
		
		when:
		authController.setPassword()
		
		then:
		authController.response.redirectedUrl == null
	}
	
	
	def "set password with valid token - invalid password"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		new PasswordToken(token: '123', user:user).save(failOnError: true)
		
		when:
		authController.params.token = '123'
		authController.params.password = '1234'
		authController.params.repeat = '12345'
		authController.setPassword()
		
		then:
		authController.modelAndView.model.newPassword.password == '1234'
		authController.modelAndView.model.newPassword.repeat == '12345'
		authController.modelAndView.model.newPassword.hasErrors() == true
		authController.modelAndView.model.token == '123'
		PasswordToken.count() == 1
	}
	
	def "set password with valid token - valid password"() {
		setup:
		authController = new AuthController()
		def user = newUser('test@test.com', false, false)
		new PasswordToken(token: '123', user:user).save(failOnError: true)
		
		when:
		authController.params.token = '123'
		authController.params.password = '1234'
		authController.params.repeat = '1234'
		authController.setPassword()
		
		then:
		authController.response.redirectedUrl == '/'
		PasswordToken.count() == 0
	}
	

}
