package org.chai.memms

/*
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

import org.apache.shiro.SecurityUtils;
import org.chai.memms.security.User;
import org.chai.location.LocationService;
import org.chai.location.DataLocationType;
import org.chai.location.DataLocation
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.location.DataLocationType;

class FilterTagLib {

	def locationService;
	def reportService;

		
	def locationFilter = {attrs, body ->
		def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])
		log.debug("user : ${user}, at location: ${user.location}")
		Location.withTransaction {
			def model = new HashMap(attrs)
			def location = null
			def locationFilterRoot = null
			def locationFilterTree = null
			if(user.location instanceof Location){
				location = attrs['selected']
				locationFilterRoot = user.location
				locationFilterTree = locationFilterRoot.collectTreeWithDataLocations(attrs['skipLevels'], attrs['selectedTypes'])
				log.debug("location tree : " + locationFilterTree.each{it} + " , with attributes : " + attrs)
			}
			model << 
				[
					currentLocation: location,
					locationFilterRoot: locationFilterRoot, 
					locationFilterTree: locationFilterTree
				]
			if (model.linkParams == null) model << [linkParams: [:]]
			out << render(template:'/tags/filter/locationFilter', model:model)
		}
	}
	
	def dataLocationTypeFilter = {attrs, body ->
		DataLocationType.withTransaction {
			def model = new HashMap(attrs)
			model << 
				[
					currentLocationTypes: attrs['selected'],
					dataLocationTypes: DataLocationType.list([cache: true])					
				]
			if (model.linkParams == null) model << [linkParams: [:]]
			out << render(template:'/tags/filter/dataLocationTypeFilter', model:model)
		}
	}
	
	
	// attrs['skipLevels'] is only needed for reports with both top-level locationFilter & levelFilter
	def createLinkByFilter = {attrs, body ->
		if (attrs['params'] == null) attrs['params'] = [:]
		else{
			Map params = new HashMap(attrs['params'])
			def skipLevels = attrs['skipLevels']
			if(skipLevels == null) skipLevels = new HashSet<LocationLevel>()
			attrs['params'] = updateParamsByFilter(params, skipLevels);
		}
		out << createLink(attrs, body)
	}		
	
	public Map updateParamsByFilter(Map params, Set<LocationLevel> skipLevels) {
		if (!params.containsKey("filter")) return params;
		String filter = (String) params.get("filter");
		Location location = null;
		if (params.get("location") != null) {
			location = Location.get(Integer.parseInt(params.get("location")))
		}
		LocationLevel level = null;
		if (params.get("level") != null) {
			level = LocationLevel.get(Integer.parseInt(params.get("level")))
		}

		if (location != null) {
			if (level != null) {
				// TODO use isAfter()
				if (location.getLevel().getOrder() >= level.getOrder()) {
					// conflict
					if (filter == "level") {
						// adjust location to level
						LocationLevel levelBefore = locationService.getLevelBefore(location.getLevel(), skipLevels)
						if (levelBefore != null)
							location = locationService.getParentOfLevel(location, levelBefore);
					}
					// conflict
					else {
						// adjust level to location
						level = locationService.getLevelAfter(location.getLevel(), skipLevels)
					}
				}
			}
			// conflict
			else {
				// adjust level to location
				level = locationService.getLevelAfter(location.getLevel(), skipLevels)
			}
		}
		
		if (location != null) params.put("location", location.id);
		if (level != null) params.put("level", level.id);
		return params;
	}
}