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

package org.chai.memms.equipment

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.location.DataLocationType
import org.chai.location.Location

class InventoryServiceSpec extends IntegrationTests{
	def inventoryService
	def "can retrieve all location levels to skip"() {
		setup:
		Initializer.createDummyStructure()
		Initializer.createUsers()
		Initializer.createInventoryStructure()
		when:
		def skipLevels = inventoryService.getSkipLocationLevels()

		then:
		skipLevels.size() == grailsApplication.config.location.sector.skip.level.size()
	}
	
	def "can retrieve inventories from a location, given an EquipmentType to filter on"() {
		setup:
		Initializer.createDummyStructure()
		Initializer.createUsers()
		Initializer.createInventoryStructure()
		
		def types = grailsApplication.config.site.datalocationtype.checked.collect{ DataLocationType.findByCode(it) }.toSet()
		when:
		def inventories = inventoryService.getInventoryByLocation(Location.findByCode('Burera'),types,adaptParamsForList())
		then:
		inventories.totalCount == 1
		inventories.inventoryList.size() == 1
	}
}
