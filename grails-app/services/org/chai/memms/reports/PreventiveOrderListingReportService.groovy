/**
 * 
 */
package org.chai.memms.reports

import java.util.Map;

import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.memms.preventive.maintenance.PreventiveOrder;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.security.User;

/**
 * @author Aphrodice Rwagaju
 *
 */
class PreventiveOrderListingReportService {
	def getEquipmentsWithPreventionPlan(User user,Map<String, String> params) {

		def criteria = PreventiveOrder.createCriteria();
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
		}
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
			eq ("status",PreventiveOrderStatus.OPEN)
		}
	}
	def getPreventionsDelayed(User user,Map<String, String> params) {
		def criteria = PreventiveOrder.createCriteria();
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
		}
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
			or{
				eq ("status",PreventiveOrderStatus.OPEN)
				//eq ("status",PreventiveOrderStatus.OPEN)
			}
		}
	}
}
