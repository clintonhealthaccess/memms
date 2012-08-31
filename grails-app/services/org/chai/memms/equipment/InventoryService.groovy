package org.chai.memms.equipment

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chai.memms.location.LocationLevel;
import org.chai.memms.Inventory
import org.chai.memms.location.CalculationLocation;
import org.chai.memms.location.DataLocationType;
import org.chai.memms.location.DataLocation
import org.chai.memms.location.Location

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
		log.debug("Skipped Levels"+levels)
		return levels;
	}
	
	public List<Inventory> getInventoryByLocation(Location location,Set<DataLocationType> types) {
		List<Inventory> inventories = []
		Set<LocationLevel> skipLevels = getSkipLocationLevels()
		log.debug("skipLevels "+skipLevels+" types "+types+" location.getDataLocations(skipLevels,types)) "+location.getDataLocations(skipLevels,types))
		for(DataLocation dataLocation : location.getDataLocations(skipLevels,types)){
			//log.debug("Skipped Levels"+levels)
			inventories.add(new Inventory(dataLocation:dataLocation,equipmentCount:equipmentService.getEquipmentsByDataLocation(dataLocation,[:]).size()))
		}
		return inventories
	}
}
