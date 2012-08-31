package org.chai.memms

import java.util.Set;

import org.apache.commons.lang.math.NumberUtils
import org.apache.shiro.SecurityUtils;
import org.chai.memms.location.DataLocationType;
import org.chai.memms.security.User
import org.chai.memms.location.Location
import org.chai.memms.location.LocationLevel

public abstract class AbstractController {
	def locationService
	def getTargetURI() {
		return params.targetURI?: "/"
	}
	
	def getUser() {
		return User.findByUuid(SecurityUtils.subject.principal, [cache: true])
	}
	
	def adaptParamsForList() {
		log.debug("Grails application value: " + grailsApplication)
		params.max = Math.min(params.max ? params.int('max') : grailsApplication.config.site.entity.list.max, 5)
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
			dataLocationTypes.addAll(grailsApplication.config.site.datalocationtype.checked.collect{ DataLocationType.findByCode(it) } - null)
		}
		
		return dataLocationTypes.sort()
	}
}