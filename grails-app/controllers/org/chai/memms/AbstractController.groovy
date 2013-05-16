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
package org.chai.memms

import java.util.Set;

import org.apache.commons.lang.math.NumberUtils
import org.apache.shiro.SecurityUtils;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.memms.inventory.Department;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.security.User
import org.chai.memms.util.Utils
import org.chai.memms.util.Utils.ReportType
import org.chai.memms.util.Utils.ReportSubType

public abstract class AbstractController {
	
	def languageService
	def locationService
	
	def getNames(){
		return 'names_'+languageService.getCurrentLanguagePrefix();
	}
	def getDescriptions(){
		return 'descriptions_'+languageService.getCurrentLanguagePrefix();
	}
	def getTargetURI() {
		return params.targetURI?: "/"
	}
	
	def getUser() {
		return User.findByUuid(SecurityUtils.subject.principal, [cache: true])
	}
	
	def getNow(){
		return new Date();
	}
	
	def adaptParamsForList() {
		if(log.isDebugEnabled()) log.debug("Grails application value: " + grailsApplication)
		params.max = Math.min(params.max ? params.int('max') : grailsApplication.config.site.entity.list.max, 40)
		params.offset = params.offset ? params.int('offset'): 0
	}
	
	def getLocation(){
		Location location = Location.get(params.int('location'))
		//TODO add skips and types to method
		//TODO if location != null, get location tree, and if the location tree doesn't contain the location, return root location
		if (location == null)
			location = locationService.getRootLocation()
		return location
	}

	public Set<DataLocationType> getLocationTypes() {
		Set<DataLocationType> dataLocationTypes = new HashSet<DataLocationType>()
		if (params.list('dataLocationTypes') != null && !params.list('dataLocationTypes').empty) {
			def types = params.list('dataLocationTypes')
			dataLocationTypes.addAll(types.collect{ NumberUtils.isNumber(it as String) ? DataLocationType.get(it) : null } - null)
		}
		
		if(dataLocationTypes == null || dataLocationTypes.empty){
			dataLocationTypes.addAll(DataLocationType.findAllByDefaultSelected(true))
		}
		
		return dataLocationTypes.sort()
	}

	def hasAccess(CalculationLocation location){
		if(!user.canAccessCalculationLocation(location)) response.sendError(403)
	}
}