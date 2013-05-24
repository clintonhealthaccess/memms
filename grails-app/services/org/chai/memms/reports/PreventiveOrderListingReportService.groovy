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

	def getCustomReportOfPreventiveOrders(User user,def customPreventiveOrderParams,Map<String, String> params) {
		
		def dataLocations = customPreventiveOrderParams.get('dataLocations')
		def departments = customPreventiveOrderParams.get('departments')
		def equipmentTypes = customPreventiveOrderParams.get('equipmentTypes')
		def lowerLimitCost = customPreventiveOrderParams.('fromCost')
		def upperLimitCost = customPreventiveOrderParams.('toCost')
		def currency = customPreventiveOrderParams.get('costCurrency')
		def workOrderStatus = customPreventiveOrderParams.get('workOrderStatus')
		def responsibles = customPreventiveOrderParams.get('whoIsResponsible')
		
		def criteria = PreventiveOrder.createCriteria();
	
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
			/*if(departments != null)
				inList ("equip.department", departments)
			if(equipmentTypes != null)
				inList ("equip.type", equipmentTypes)
			if(workOrderStatus!=null && !workOrderStatus.empty)
				inList ("status",workOrderStatus)
			if(lowerLimitCost!=null)
				gt ("equip.purchaseCost", lowerLimitCost)
			if(upperLimitCost!=null)
				lt ("equip.purchaseCost", upperLimitCost)
			if(currency !=null)
				eq ("equip.currency",currency)	
			if(responsibles!=null && !responsibles.empty)
				inList ("preventionResponsible",responsibles)*/
		}
	}
}
