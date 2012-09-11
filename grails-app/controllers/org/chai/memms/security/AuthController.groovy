/**
 * Copyright (c) 2012, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.memms.security

import org.apache.commons.lang.RandomStringUtils
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.web.util.SavedRequest
import org.apache.shiro.web.util.WebUtils
import org.chai.memms.security.NewPasswordCommand;
import org.grails.datastore.mapping.config.utils.ConfigUtils;
import org.chai.memms.security.User.UserType;

class AuthController {
    def shiroSecurityManager
	def languageService
	def grailsApplication
	def getTargetUri() {
		// this is because shiro automatically adds the parameter 'targetUri'
		// and there is no way to change it so we expect it here as well
		if (params.targetUri != null) return params.targetUri
		else return params.targetUri?: "/"
	}
	
	def getFromEmail() {
		def fromEmail = grailsApplication.config.site.from.email;
		return fromEmail
	}
	
    def index = { redirect(action: "login", params: params) }

    def login = {
		if (log.isDebugEnabled()) log.debug("auth.login, params:"+params)
        return [ username: params.username, rememberMe: (params.rememberMe != null), targetUri: params.targetUri ]
    }
	
	def register = {
		render (view:'register', model:[register: null, languages: languageService.availableLocales])
	}
	
	// this will disappear once we have a real registration mechanism
	def sendRegistration = { RegisterCommand cmd ->
		if (log.isDebugEnabled()) log.debug("auth.sendRegistration, params:"+params)
		
		if (cmd.hasErrors()) {
			if (log.isDebugEnabled()) log.debug("command has errors: "+cmd)

			render(view:'register', model:[register: cmd, languages: languageService.availableLocales])
		}
		else {
			RegistrationToken token = new RegistrationToken(token: RandomStringUtils.randomAlphabetic(20), used: false)
			
			def user = new User(userType: UserType.OTHER, username: cmd.email, email: cmd.email, passwordHash: new Sha256Hash(cmd.password).toHex(), permissionString:'',
				firstname: cmd.firstname, lastname: cmd.lastname, organisation: cmd.organisation,
				phoneNumber: cmd.phoneNumber, defaultLanguage: cmd.defaultLanguage, uuid: UUID.randomUUID().toString(),registrationToken:token).save(failOnError: true)
				
			
			def url = createLink(absolute: true, controller:'auth', action:'confirmRegistration', params:[token:token.token])
			
			def contactEmail = grailsApplication.config.site.contact.email;
			sendMail {
				to contactEmail
				from getFromEmail()
				subject "Registration received from ${user.email}"
				body "Registration received from ${user.email}, first name: ${user.firstname}, last name: ${user.lastname}, organisation: ${user.organisation}, phone number: ${user.phoneNumber}"
			}
			
			if (log.isDebugEnabled()) log.debug("sending email to: ${user.email}, token: ${token.token}, url: ${url}")
			sendMail {
				to user.email
				from getFromEmail()
				subject message(code:'register.account.email.subject', default:'DHSST - your account has been created, please confirm your email address.')
				body message(code:'register.account.email.body', args:[user.firstname, url], default:'Dear {0},\n\nPlease confirm your email address by following this link: {1}\nSomeone will then review your account and activate it.\n\nYour DHSST Team.')
			}
			
			flash.message = message(code:'register.account.successful', default:'Thanks for registering, you should receive a confirmation email.')
			redirect(action: "login")
		}
	}
	
	def confirmRegistration = {
		if (log.isDebugEnabled()) log.debug("auth.confirmRegistration, params:"+params)
		
		RegistrationToken token = null
		if (params.token != null) token = RegistrationToken.findByToken(params.token)
		if (token != null) {
			if (!token.used) {
				def user = token.user
				user.confirmed = true
				user.save()
				token.used = true
				token.save()
				
				def contactEmail = grailsApplication.config.site.contact.email;
				sendMail {
					to contactEmail
					from getFromEmail()
					subject "Email verified from ${user.email}."
					body "Email verified from ${user.email}, please review and activate."
				}
				
				sendMail {
					to user.email
					from getFromEmail()
					subject message(code:'confirm.account.email.subject', default:'DHSST - your account has been verified.')
					body message(code:'confirm.account.email.body', args:[user.firstname], default:'Dear {0},\n\nThank you, your email has been verified, someone will review your account and activate it.\nWe will let you know when it is ready.\n\nYour DHSST Team.')
				}
				
				flash.message = message(code:'confirm.account.successful', default:'Your email has been verified. We will review your account and let you know when it is ready.')
				redirect(action: 'login')
			}
			else {
				flash.message = message(code:'confirm.account.used.token', default:'Your email has already been verified, please wait for us to activate your account.')
				redirect(action: 'login')
			}
		}
		else {
			response.sendError(404)
		}
	}

	def activate = {
		if (log.isDebugEnabled()) log.debug("auth.confirmRegistration, params:"+params)
		
		User user = User.get(params.int('id'))
		if (user != null) {
			if (user.confirmed) {
				user.active = true
				user.save()
				
				if(user.registrationToken != null){
					//This assumes that a user has only one regitration token
					RegistrationToken registrationTokenToDelete = user.registrationToken
					user.registrationToken = null
					registrationTokenToDelete.delete()
				}
				
				
				def url = createLink(absolute: true, controller:'auth', action:'login', params:[username:user.username])
				
				sendMail {
					to user.email
					from getFromEmail()
					subject message(code:'activate.account.email.subject', default:'DHSST - your account has been activated.')
					body message(code:'activate.account.email.body', args:[user.firstname, user.username, url], default:'Dear {0},\n\nYour email has just been activated by the DHSST team. You can now login using {1} as username and the password you set when you registered.\nOr follow this URL: {2}\n\nYour DHSST Team.')
				}
				
				flash.message = message(code:'activate.account.successful', default:'The account has been activated and the user notified.')
				redirect(uri: getTargetUri())
			}
			else {
				flash.message = message(code:'activate.account.unconfirmed', default:'The account has not been confirmed. Let the user confirm its address and activate later.')
			}
		}
		else {
			response.sendError(404)
		}
	}
	
    def signIn = {
		if (log.isDebugEnabled()) log.debug("auth.singIn, params:"+params)
        def authToken = new UsernamePasswordToken(params.username, params.password as String)

        // Support for "remember me"
        if (params.rememberMe) {
            authToken.rememberMe = true
        }
        
        // If a controller redirected to this page, redirect back
        // to it. Otherwise redirect to the root URI.
        def targetUri = params.targetUri ?: "/"
        
        // Handle requests saved by Shiro filters.
        def savedRequest = WebUtils.getSavedRequest(request)
        if (savedRequest) {
            targetUri = savedRequest.requestURI - request.contextPath
            if (savedRequest.queryString) targetUri = targetUri + '?' + savedRequest.queryString
        }
        
        try{
            // Perform the actual login. An AuthenticationException
            // will be thrown if the username is unrecognised or the
            // password is incorrect.
            SecurityUtils.subject.login(authToken)

            log.info "Redirecting to '${targetUri}'."
            redirect(uri: targetUri)
        }
        catch (AuthenticationException ex){
            // Authentication failed, so display the appropriate message
            // on the login page.
            log.info "Authentication failure for user '${params.username}'."
            flash.message = message(code: "login.failed")

            // Keep the username and "remember me" setting so that the
            // user doesn't have to enter them again.
            def m = [ username: params.username ]
            if (params.rememberMe) {
                m["rememberMe"] = true
            }

            // Remember the target URI too.
            if (params.targetUri) {
                m["targetUri"] = params.targetUri
            }

            // Now redirect back to the login page.
            redirect(action: "login", params: m)
        }
    }
	
    def signOut = {
        // Log the user out of the application.
        SecurityUtils.subject?.logout()

        // For now, redirect back to the home page.
        redirect(uri: "/")
    }
	
	def forgotPassword = {
		render (view:'forgotPassword', model: [retrievePassword: null])
	}

	def retrievePassword = { RetrievePasswordCommand cmd ->
		if (log.isDebugEnabled()) log.debug("auth.retrievePassword, params:"+params)
		
		if (cmd.hasErrors()) {
			render (view:'forgotPassword', model:[retrievePassword: cmd])	
		}
		else {
			// create token
			def user = User.findByEmail(cmd.email)
			//if user has an already existing token, delete it
			if(user.passwordToken != null){
				//This assumes that a user has only one password token
				PasswordToken passwordTokenToDelete = user.passwordToken
				user.passwordToken = null
				passwordTokenToDelete.delete()
			}
			
			PasswordToken token = new PasswordToken(token: RandomStringUtils.randomAlphabetic(20), user: user).save()
			
			def url = createLink(absolute: true, controller:'auth', action:'newPassword', params:[token:token.token])
			if (log.isDebugEnabled()) log.debug("sending email to: ${user.email}, token: ${token.token}, url: ${url}")
			
			// send email
			sendMail {
				to user.email
				from getFromEmail()
				subject message(code:'forgot.password.email.subject', default: "DHSST - Lost password?")
				body message(code:'forgot.password.email.body', args:[user.firstname, url], default: "Dear {0}\n\nTo set a new password, please go to {1}.\n\nYour DHSST Team.")
			}
			flash.message = message(code:'forgot.password.successful', default:'An email has been sent with the instructions.')
			redirect(action:'login')
		}
	}

	def newPassword = {
		if (log.isDebugEnabled()) log.debug("auth.newPassword, params:"+params)
		
		PasswordToken token = null
		if (params.token != null) token = PasswordToken.findByToken(params.token)
		if (token != null) {
			// if token in URL
			render (view:'newPassword', model:[token: token.token, newPassword: null, targetUri: getTargetUri()])
		}
		else if (SecurityUtils.subject?.principal != null) {
			// if user is logged in
			render (view:'newPassword', model:[newPassword: null, targetUri: getTargetUri()])
		}
		else {
			// to 404 error page
			response.sendError(404)
		}
	}
	
	def setPassword = { NewPasswordCommand cmd ->
		if (log.isDebugEnabled()) log.debug("auth.setPassword, params:"+params)
		
		if (cmd.hasErrors()) {
			render (view: 'newPassword', model:[token: params.token, newPassword: cmd, targetUri: getTargetUri()])
		}
		else {
			PasswordToken token = null
			if (params.token != null) token = PasswordToken.findByToken(params.token)
			User user = null
			if (token != null) {
				// retrieve user from token
				user = token.user
				//Disassociate token from user
				user.passwordToken = null
				// delete the token
				token.delete()
			}
			else if (SecurityUtils.subject?.principal != null) {
				user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])
			}
			
			if (user != null) {
				user.passwordHash = new Sha256Hash(cmd.password).toHex()
				user.save()
				
				log.info("password changed succesfully, redirecting to: "+getTargetUri())
				flash.message = message(code:'set.password.success', default:'Your new password has been set.')
				redirect(uri: getTargetUri())
			}
			else {
				// to 404 error page
				response.sendError(404)
			}
		}
	}
    def unauthorized = {
        render "You do not have permission to access this page."
    }
}

class RetrievePasswordCommand {
	String email
	
	static constraints = {
		email(blank:false, validator: {val, obj ->
			return User.findByEmail(val) != null
		})
	}
}

class NewPasswordCommand {
	String password
	String repeat

	static constraints = {
		password(blank: false, minSize: 4)
		repeat(validator: {val, obj ->
			val == obj.password
		})
	}
}

class RegisterCommand extends NewPasswordCommand {
	String firstname
	String lastname
	String organisation
	String email
	String phoneNumber
	String defaultLanguage
	
	static constraints = {
		firstname(nullable:false, blank:false)
		lastname(nullable:false, blank:false)
		organisation(nullable:false, blank:false)
		phoneNumber(nullable:false, blank:false, phoneNumber: true)
		defaultLanguage(nullable:true)
		email(blank:false, email:true, validator: {val, obj ->
			return User.findByEmail(val) == null && User.findByUsername(val) == null
		})
	}
}