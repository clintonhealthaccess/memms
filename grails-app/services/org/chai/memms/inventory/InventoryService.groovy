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
package org.chai.memms.inventory

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.location.LocationLevel;
import org.chai.memms.Inventory
import org.chai.memms.Inventories
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.DataLocation
import org.chai.location.Location

class InventoryService {

    static transactional = true
	def locationService
	def equipmentService
	def grailsApplication
	
	public Set<LocationLevel> getSkipLocationLevels() {
		List<LocationLevel> levels = []
		for (String skipLevel : grailsApplication.config.location.sector.skip.level) {
			levels.add(locationService.findLocationLevelByCode(skipLevel));
		}
		return levels;
	}
	
	//If params are not in order return the whole list.
	public Inventories getInventoryByLocation(Location location,Set<DataLocationType> types,Map<String, String> params) {
		List<Inventory> inventories = []
		Set<LocationLevel> skipLevels = getSkipLocationLevels()
		def locations = location.collectDataLocations(types)
		for (DataLocation dataLocation : locations[(params.offset) ..< (((params.offset + params.max) > locations.size()) ? locations.size() : (params.offset + params.max))]) {
			inventories.add(new Inventory(dataLocation:dataLocation,equipmentCount:equipmentService.filterEquipment(null,dataLocation,null,null,null,null,null,null,null,null,[:]).size()))
		}
		
		Inventories inventory = new Inventories()
		inventory.inventoryList = inventories

		inventory.totalCount = locations.size()
		return inventory
	}

	//If params are not in order return the whole list.
	public Inventories getInventoryByDataLocations(Location location,List<DataLocation> dataLocations,Set<DataLocationType> types,Map<String, String> params) {
		List<Inventory> inventories = []
		Set<LocationLevel> skipLevels = getSkipLocationLevels()
		def locations = location.collectDataLocations(types)
		dataLocations.retainAll(locations)
		for (DataLocation dataLocation : dataLocations[(params.offset) ..< (((params.offset + params.max) > dataLocations.size()) ? dataLocations.size() : (params.offset + params.max))]) {
			inventories.add(new Inventory(dataLocation:dataLocation,equipmentCount:equipmentService.filterEquipment(null,dataLocation,null,null,null,null,null,null,null,null,[:]).size()))
		}
		
		Inventories inventory = new Inventories()
		inventory.inventoryList = inventories

		inventory.totalCount = dataLocations.size()
		return inventory
	}
}