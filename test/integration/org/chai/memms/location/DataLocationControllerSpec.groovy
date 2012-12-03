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
import org.chai.location.DataLocationType;
import org.chai.memms.location.DataLocationController;
import org.chai.location.Location;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;

/**
 * @author Jean Kahigiso M.
 *
 */
class DataLocationControllerSpec extends IntegrationTests{
	def dataLocationController
	
	def "can create save a dataLocation"(){
		setup:
		dataLocationController =  new DataLocationController()
		when:
		setupLocationTree()
		then:
		DataLocation.count() == 5
		
		when:
		def type = DataLocationType.findByCode("Health Center")
		def location = Location.findByCode("Burera")
		dataLocationController.params.code = CODE("123")
		dataLocationController.params.names_en = "Test data location"
		dataLocationController.params."type.id" =  type.id
		dataLocationController.params."location.id" = location.id
		dataLocationController.save()
		then:
		DataLocation.count() == 6
	}
	/**
	 * When you call the save method, all the past managed objects are removed
	 * and only those objects which appear in the managesIds list are persisted
	 * @return
	 */
	def "can edit a dataLocation add/remove manages "(){
		setup:
		setupLocationTree()
		
		def gitarama =  Location.findByCode(GITARAMA)
		def butaro =  DataLocation.findByCode(BUTARO)
		def muvuna = DataLocation.findByCode(MUVUNA)
		def type = DataLocationType.findByCode("Health Center")
		def ruliOne = Initializer.newDataLocation(['en':"ruliOne"], CODE("901"), gitarama, type)
		def ruliTwo = Initializer.newDataLocation(['en':"ruliTwo"], CODE("902"), gitarama, type)
		dataLocationController =  new DataLocationController()
		when:
		dataLocationController.params."id" = butaro.id
		dataLocationController.params.code = butaro.code
		dataLocationController.params.names_en = "Test data location"
		dataLocationController.params."type.id" =  butaro.type.id
		dataLocationController.params."location.id" = butaro.location.id
		dataLocationController.params."managesIds" = [ruliOne.id+'',ruliTwo.id+'']
		dataLocationController.save()
		then:
		DataLocation.count() == 7
		butaro.manages.size() == 2
		butaro.manages.contains(ruliOne)
		butaro.manages.contains(ruliTwo)
		!butaro.manages.contains(muvuna)
		ruliOne.managedBy == butaro
		ruliTwo.managedBy == butaro
	}
	
	def "list dataLocations"(){
		setup:
		setupLocationTree()
		dataLocationController =  new DataLocationController()
		
		when: "none ajax"
		dataLocationController.list()
		
		then:
		DataLocation.count() == 5
		dataLocationController.modelAndView.model.entities.size() == 5
		
		when: "with ajax"
		dataLocationController.request.makeAjaxRequest()
		dataLocationController.list()
		
		then:
		DataLocation.count() == 5
		dataLocationController.response.json.results[0].contains(DataLocation.findByCode(BUTARO).code)
		dataLocationController.response.json.results[0].contains(DataLocation.findByCode(KIVUYE).code)
		dataLocationController.response.json.results[0].contains(DataLocation.findByCode(MUSANZE).code)
		dataLocationController.response.json.results[0].contains(DataLocation.findByCode(GITWE).code)
		dataLocationController.response.json.results[0].contains(DataLocation.findByCode(MUVUNA).code)
		!dataLocationController.response.json.results[0].contains(Location.findByCode(NORTH).code)
	}
	
	def "search dataLocation"(){
		setup:
		setupLocationTree()
		dataLocationController =  new DataLocationController()

		when:
		dataLocationController.params.q = BUTARO
		dataLocationController.request.makeAjaxRequest()
		dataLocationController.search()
		then:
		DataLocation.count() == 5
		dataLocationController.response.json.results[0].contains(DataLocation.findByCode(BUTARO).code)
		!dataLocationController.response.json.results[0].contains(DataLocation.findByCode(KIVUYE).code)
		!dataLocationController.response.json.results[0].contains(DataLocation.findByCode(MUSANZE).code)
		!dataLocationController.response.json.results[0].contains(DataLocation.findByCode(GITWE).code)
		!dataLocationController.response.json.results[0].contains(DataLocation.findByCode(MUVUNA).code)
		!dataLocationController.response.json.results[0].contains(Location.findByCode(NORTH).code)
	}
	
	def "cannot search without using ajax dataLocation"(){
		setup:
		setupLocationTree()
		dataLocationController =  new DataLocationController()

		when:
		dataLocationController.params.q = BUTARO
		dataLocationController.search()
		then:
		DataLocation.count() == 5
		dataLocationController.response.status == 404
	}
}
