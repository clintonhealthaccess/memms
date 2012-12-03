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

import org.chai.location.LocationLevel;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;

/**
 * @author Jean Kahigiso M.
 *
 */
class LocationLevelControllerSpec extends IntegrationTests{
	
	def locationLevelController
	
//	def "can create save a location order"(){
//		setup:
//		locationLevelController =  new LocationController()
//		when:
//		locationLevelController.params.order = 1
//		locationLevelController.params.code = CODE("3123")
//		locationLevelController.params.names_en = "Test Sector location en"
//		locationLevelController.params.names_fr = "Test Sector location fr"
//		locationLevelController.save()
//		then:
//		LocationLevel.count() == 1
//		LocationLevel.list()[0].code == CODE("3123")
//		LocationLevel.list()[0].order == 1
//		LocationLevel.list()[0].names_en == "Test Sector location en"
//		LocationLevel.list()[0].names_fr == "Test Sector location fr"
//	}
	
	def "can't create save a location level without order"(){
		setup:
		locationLevelController =  new LocationLevelController()
		when:
		locationLevelController.params.code = CODE("123")
		locationLevelController.params.names_en = "Test Sector location en"
		locationLevelController.params.names_fr = "Test Sector location fr"
		locationLevelController.save()
		then:
		LocationLevel.count() == 0
		
	}
	def "can't create save a location level without code"(){
		setup:
		locationLevelController =  new LocationLevelController()
		when:
		locationLevelController.params.order = "1"
		locationLevelController.params.names_en = "Test Sector location en"
		locationLevelController.params.names_fr = "Test Sector location fr"
		locationLevelController.save()
		then:
		LocationLevel.count() == 0
		
	}
	def "can't create save a location level with duplicate code"(){
		setup:
		Initializer.newLocationLevel(['en':SECTOR], SECTOR,4)
		locationLevelController =  new LocationLevelController()
		when:
		locationLevelController.params.order = "1"
		locationLevelController.params.code = SECTOR
		locationLevelController.params.names_en = "Test Sector location en"
		locationLevelController.params.names_fr = "Test Sector location fr"
		locationLevelController.save()
		then:
		LocationLevel.count() == 1
	}
	
	def "list LocationLevel"(){
		setup:
		setupLocationTree()
		locationLevelController =  new LocationLevelController()
		
		when: "none ajax"
		locationLevelController.list()
		
		then:
		LocationLevel.count() == 4
		locationLevelController.modelAndView.model.entities.size() == 4
		
		when: "with ajax"
		locationLevelController.request.makeAjaxRequest()
		locationLevelController.list()
		
		then:
		LocationLevel.count() == 4
		locationLevelController.response.json.results[0].contains(LocationLevel.findByCode(NATIONAL).code)
		locationLevelController.response.json.results[0].contains(LocationLevel.findByCode(PROVINCE).code)
		locationLevelController.response.json.results[0].contains(LocationLevel.findByCode(DISTRICT).code)
		locationLevelController.response.json.results[0].contains(LocationLevel.findByCode(SECTOR).code)
	}
}
