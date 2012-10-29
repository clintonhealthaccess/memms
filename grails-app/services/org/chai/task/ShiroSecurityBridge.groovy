package org.chai.task

import org.apache.shiro.SecurityUtils;
import org.grails.plugin.platform.security.SecurityBridge;
import org.chai.memms.security.User

class ShiroSecurityBridge implements SecurityBridge {

	
	/**
	 * Implementations must return the name of their security provider
	 * @return A name such as "Spring Security"
	 */
	String getProviderName() {
		return 'shiro'
	}
 
	/**
	 * Get user id string i.e. "marcpalmer" of the currently logged in user, from whatever
	 * underlying security API is in force
	 * @return the user name / identity String or null if nobody is logged in
	 */
	String getUserIdentity() {
		// used by task plugin
		return SecurityUtils.subject.principal
	}
 
	/**
	 * Get user info object containing i.e. email address, other stuff defined by the security implementation
	 * @return the implementation's user object or null if nobody is logged in
	 */
	def getUserInfo(){
		// not used by task plugin
		return User.findByUuid(SecurityUtils.subject.principal)
	}
 
	/**
	 * Return true if the current logged in user has the specified role
	 */
	boolean userHasRole(role) {
		// not used by task plugin
		SecurityUtils.getSubject().hasRole(role)
	}
 
	/**
	 * Can the current user access this object to perform the named action?
	 * @param object The object, typically domain but we don't care what
	 * @param action Some application-defined action string i.e. "view" or "edit"
	 */
	boolean userIsAllowed(object, action) {
		// not used by task plugin
		SecurityUtils.getSubject().isPermitted(action)
	}
	 
	/**
	 * Create a link to the specified security action
	 * @param action One of "login", "logout", "signup"
	 * @return Must return a Map of arguments to pass to g:link to create the link
	 */
	Map createLink(String action) {
		// TODO not used by task plugin
	}
	 
	/**
	 * Execute code masquerading as the specified user, for the duration of the Closure block
	 * @return Whatever the closure returns
	 */
	def withUser(identity, Closure code) {
		// TODO not used by task plugin
	}
	
}
