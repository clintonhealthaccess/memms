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
package org.chai.memms.location

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.memms.IntegrationTests;

/**
 * @author Jean Kahigiso M.
 *
 */
class LocationControllerSpec extends IntegrationTests{
	def locationController
	
	def "can create save a location"(){
		setup:
		setupLocationTree()
		locationController =  new LocationController()
		when:
		def level = LocationLevel.findByCode(SECTOR)
		def parent = Location.findByCode(BURERA)
		locationController.params.code = CODE("123")
		locationController.params.names_en = "Test Sector location"
		locationController.params."level.id" =  level.id
		locationController.params."parent.id" = parent.id
		locationController.save()
		then:
		Location.count() == 6
		Location.list()[5].code == CODE("123")
		Location.list()[5].parent == parent
	}

	def "list Locations"(){
		setup:
		setupLocationTree()
		locationController =  new LocationController()
		
		when: "none ajax"
		locationController.list()
		
		then:
		Location.count() == 5
		locationController.modelAndView.model.entities.size() == 5
		
		when: "with ajax"
		locationController.request.makeAjaxRequest()
		locationController.list()
		
		then:
		Location.count() == 5
		locationController.response.json.results[0].contains(Location.findByCode(RWANDA).code)
		locationController.response.json.results[0].contains(Location.findByCode(NORTH).code)
		locationController.response.json.results[0].contains(Location.findByCode(SOUTH).code)
		locationController.response.json.results[0].contains(Location.findByCode(BURERA).code)
		locationController.response.json.results[0].contains(Location.findByCode(GITARAMA).code)
		!locationController.response.json.results[0].contains(DataLocation.findByCode(BUTARO).code)
	}
	
	def "search Location"(){
		setup:
		setupLocationTree()
		locationController =  new LocationController()

		when:
		locationController.params.q = NORTH
		locationController.request.makeAjaxRequest()
		locationController.search()
		then:
		Location.count() == 5
		locationController.response.json.results[0].contains(Location.findByCode(NORTH).code)
		!locationController.response.json.results[0].contains(Location.findByCode(SOUTH).code)
		!locationController.response.json.results[0].contains(Location.findByCode(BURERA).code)
		!locationController.response.json.results[0].contains(Location.findByCode(GITARAMA).code)
		!locationController.response.json.results[0].contains(DataLocation.findByCode(BUTARO).code)
	}
	
	def "cannot search without using ajax Location"(){
		setup:
		setupLocationTree()
		locationController =  new LocationController()

		when:
		locationController.params.q = BUTARO
		locationController.search()
		then:
		Location.count() == 5
		locationController.response.status == 404
	}
}
