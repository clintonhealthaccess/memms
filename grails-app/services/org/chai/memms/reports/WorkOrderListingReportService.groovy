/**
 * 
 */
package org.chai.memms.reports

import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.security.User;
import org.chai.memms.util.Utils;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;

/**
 * @author Aphrodice Rwagaju
 *
 */
class WorkOrderListingReportService {
	def getWorkOrdersEscalatedToMMC(User user,Map<String, String> params) {

		def criteria = WorkOrder.createCriteria();
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
			eq ("currentStatus",OrderStatus.OPENATMMC)
		}
	}
	def getWorkOrdersOfLastMonth(User user,Map<String, String> params) {
		//TODO to add the last month criteria
		def lastMonth= (new Date())-30
		def criteria = WorkOrder.createCriteria();
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
				eq ("currentStatus",OrderStatus.OPENATMMC)
				eq ("currentStatus",OrderStatus.OPENATFOSA)
			}
			ge ("openOn",lastMonth)
		}
	}
}
