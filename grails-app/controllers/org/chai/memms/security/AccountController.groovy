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

import org.apache.shiro.SecurityUtils;
import org.chai.memms.AbstractController


class AccountController extends  AbstractController{
	def languageService
	//def shiroSecurityManager
	
	def editAccount = {
		def user = User.findByUuid(SecurityUtils.subject?.principal)
		if (user != null) {
			render (view:'editAccount', model: [user: user, languages: languageService.availableLocales])
		}
		else {
			// to 404 error page
			response.sendError(404)
		}
	}
	
	def saveAccount = {
		def user = User.findByUuid(SecurityUtils.subject?.principal)
		if (user != null) {
			bindData(user, params, [include: ['firstname', 'lastname', 'defaultLanguage', 'phoneNumber', 'organisation']])
			
			if (!user.validate()) {
				render(view:'editAccount', model: [user: user, languages: languageService.availableLocales])
			}
			else {
				flash.message = message(code: 'myaccount.saved')
				
				user.save()
				redirect(uri: targetURI)
			}
		}
		else {
			// to 404 error page
			response.sendError(404)
		}
	}
}