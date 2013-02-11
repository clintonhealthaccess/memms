package org.chai.memms

import org.apache.commons.lang.StringEscapeUtils;
import org.chai.memms.security.User;
import org.chai.memms.util.Utils
import org.apache.shiro.SecurityUtils;

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
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

class UtilTagLib {
	
	def languageService
	
	def workOrderNotificationService
	
	def userService
	
	def equipmentRegistration = { attrs, body ->
		def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])
		if(userService.canRequestEquipmentRegistration(user))
			out << '<a href=' + createLinkWithTargetURI(controller: "notificationEquipment", action: "create") + ' class="next medium left push-r">' + message(code:"equipment.new.request.label") + '</a>'
		else if(attrs["dataLocation"])
			out << '<a href=' + createLinkWithTargetURI(controller: "equipment", action: "create", params:["dataLocation.id": attrs["dataLocation"] ]) + ' class="next medium left push-r">' + message(code:"default.new.label" , args: attrs["entityName"] ) + '</a>'
		else
			out << '<a href=' + createLinkWithTargetURI(controller: "equipmentView", action: "selectFacility") + ' class="next medium left push-r">' + message(code:"default.new.label" , args: attrs["entityName"] ) + '</a>'

	}
	
	def notificationCount = { attrs, body ->
		def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])
		out << workOrderNotificationService.getUnreadNotifications(user)
	}
	def createLinkWithTargetURI = {attrs, body ->
		if (attrs['params'] == null) attrs['params'] = [:]
		else attrs['params'] = new HashMap(attrs['params'])
		
		log.debug("found query string: ${request.queryString}")
		attrs['params'] << [targetURI: request.forwardURI - request.contextPath + (request.queryString==null?'':'?'+request.queryString)];
		
		log.debug('creating link with attrs: '+attrs)
		out << createLink(attrs, body)
	}		
	
	def toHtml = {attrs, body ->
		out << attrs.value.replaceAll("(\\r\\n|\\n)", "<br/>").replaceAll("( )", "&nbsp;")	
	}
	
	def dateFormat = { attrs, body ->
		out << new java.text.SimpleDateFormat(attrs.format).format(attrs.date)
	}
	
	def searchBox = { attrs, body ->
		if(log.isDebugEnabled()) log.debug("Search tag with attrs:" + attrs)
		if (attrs['controller'] == null) attrs['controller'] = controllerName;
		if (attrs['action'] == null) attrs['action'] = actionName;
		attrs['hiddenParams'] = new HashMap(attrs['params']?attrs['params']:params)
		attrs['hiddenParams'].remove('max')
		attrs['hiddenParams'].remove('offset')
		attrs['hiddenParams'].remove('controller')
		attrs['hiddenParams'].remove('action')
		attrs['hiddenParams'].remove('q')
		out << render(template:"/tags/util/searchBox", model: attrs);
	}
	
	def locales = { attrs, body ->
		attrs["locales"] = languageService.getAvailableLanguages();
		out << render(template:"/tags/util/locales", model: attrs)
	}
	
	def prettyList = { attrs, body ->
		def entities = attrs['entities']
		def splitDelim = attrs['split'] ?: ','
		def joinDelim = attrs['join'] ?: '<br />'
		
		def text = null
		if(entities instanceof List)
			text = entities.join(joinDelim)
		else if(entities instanceof String)
			text = entities.split(splitDelim).join(joinDelim)
		else {}
		
		out << text
	}
	
	def i18nField = { attrs, body ->
		def field = attrs['field']
		out << field + '_' + languageService.currentLanguage
	}
	
	def ifText = { attrs, body ->
		def text = attrs['field'] == null?'':attrs['field']+''
		if (!utilsService.stripHtml(text).trim().isEmpty()) out << body()
	}
	
	def stripHtml = { attrs, body ->
		def text = attrs['field'] == null?'':attrs['field']+''
		def strippedText = Utils.stripHtml(text)
		def smallText = strippedText.length() > attrs.int('chars')?strippedText.substring(0, attrs.int('chars')):strippedText
		out << render(template:"/tags/util/stripHtml", model: [fullText: strippedText, smallText: smallText])
	}
}
