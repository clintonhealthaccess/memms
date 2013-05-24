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

	def getCustomReportOfWorkOrders(User user, def customWorkOrderParams, Map<String, String> params) {

		def dataLocations = customWorkOrderParams.get('dataLocations')
		def departments = customWorkOrderParams.get('departments')
		def equipmentTypes = customWorkOrderParams.get('equipmentTypes')
		def lowerLimitCost = customWorkOrderParams.('fromCost')
		def upperLimitCost = customWorkOrderParams.('toCost')
		def currency = customWorkOrderParams.get('costCurrency')
		def workOrderStatus = customWorkOrderParams.get('workOrderStatus')
		
		def criteria = WorkOrder.createCriteria();
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){

			createAlias("equipment","equip")

			if(dataLocations!=null)
				inList('equip.dataLocation',dataLocations)
			if(workOrderStatus!=null && !workOrderStatus.empty)
				inList ("currentStatus",workOrderStatus)
			if(lowerLimitCost!=null)
				gt ("equip.purchaseCost", lowerLimitCost)
			if(upperLimitCost!=null)
				lt ("equip.purchaseCost", upperLimitCost)
			if(currency !=null)
				eq ("equip.currency",currency)
			if(departments != null)
				inList ("equip.department", departments)
				
			if(equipmentTypes != null)
				inList ("equip.type", equipmentTypes)		
		}
	}
}
