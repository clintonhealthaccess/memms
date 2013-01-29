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
package org.chai.memms.corrective.maintenance

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.chai.location.LocationLevel;
import org.chai.memms.CorrectiveMaintenance;
import org.chai.memms.CorrectiveMaintenances;
import org.chai.memms.Inventory
import org.chai.memms.Inventories
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.DataLocation
import org.chai.location.Location

class CorrectiveMaintenanceService {

    static transactional = true
	def locationService
	def workOrderService
	def grailsApplication
	
	public Set<LocationLevel> getSkipLocationLevels() {
		List<LocationLevel> levels = []
		for (String skipLevel : grailsApplication.config.location.sector.skip.level) {
			levels.add(locationService.findLocationLevelByCode(skipLevel));
		}
		return levels;
	}   
	
	public CorrectiveMaintenances getCorrectiveMaintenancesByLocation(Location location,Set<DataLocationType> types,Map<String, String> params) {
		if(log.isDebugEnabled()) log.debug("getCorrectiveMaintenancesByLocation url params: "+params)
		List<CorrectiveMaintenance> correctiveMaintenances = []
		Set<LocationLevel> skipLevels = getSkipLocationLevels()
		for(DataLocation dataLocation : location.collectDataLocations(types)){
			correctiveMaintenances.add(new CorrectiveMaintenance(dataLocation:dataLocation,workOrderCount:workOrderService.filterWorkOrders(dataLocation,null,null, null, null,null,[:]).size()))
		}
		
		CorrectiveMaintenances correctiveMaintenance = new CorrectiveMaintenances()
		//If user tries to access elements outside the range, return empty list
		//TODO this is ideally supposed to avoid trying to access out of range data, but could be a bite
		if(params.offset != null && params.max != null && params.offset > params.max) return correctiveMaintenance
		//If user specifies the pagination params, use them. Else return the whole list
		if(params.offset != null && params.offset > 0  && params.max != null && params.max > 0) correctiveMaintenance.correctiveMaintenanceList = correctiveMaintenances[(params.offset) .. ((params.offset + params.max) > correctiveMaintenances.size() ? correctiveMaintenances.size() - 1 : (params.offset + params.max))]
		else correctiveMaintenance.correctiveMaintenanceList = correctiveMaintenances
		correctiveMaintenance.totalCount = correctiveMaintenances.size()
		return correctiveMaintenance
	}
}