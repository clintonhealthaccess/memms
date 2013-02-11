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
package org.chai.memms.preventive.maintenance

import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.memms.AbstractController
import org.chai.memms.AbstractEntityController;
import org.chai.memms.inventory.Equipment;

/**
 * @author Jean Kahigiso M.
 *
 */
class PreventiveOrderViewController extends AbstractController {
	
	def preventiveOrderService
	def equipmentService
	def maintenanceService
	
	def getEntityClass() {
		return PreventiveOrder.class;
	}
	
	def getLabel() {
		return "preventive.order.label";
	}
	
	def list = {	
		adaptParamsForList()
		def orders = []
		def equipment = null
		def  dataLocation = null
		if(params["dataLocation.id"]) dataLocation = CalculationLocation.get(params.int("dataLocation.id"))
		if(params["equipment.id"]) equipment = Equipment.get(params.int("equipment.id"))
		
		if(dataLocation)
			orders = maintenanceService.getMaintenanceOrderByCalculationLocation(PreventiveOrder.class,dataLocation,params)	
		if(equipment)
			orders = maintenanceService.getMaintenanceOrderByEquipment(PreventiveOrder.class,equipment,params)
		 if(request.xhr){
			 this.ajaxModel(orders,dataLocation,equipment,"")
		 }else{
			render (view: '/entity/list', model:model(orders, dataLocation, equipment) << [
				template:"preventiveOrder/preventiveOrderList",
				filterTemplate:"preventiveOrder/preventiveOrderFilter",
				listTop:"preventiveOrder/listTop",
			])
		}
	}

	def search = {
		def equipment = null
		def dataLocation = null
		if(params["equipment.id"])
			equipment = Equipment.get(params.long("equipment.id"))
		else if(params["dataLocation.id"])
			dataLocation = DataLocation.get(params.long('dataLocation.id'))
			
		adaptParamsForList()
		def orders = maintenanceService.searchOrder(PreventiveOrder.class,params['q'],dataLocation,equipment,params)
		if(request.xhr)
			this.ajaxModel(orders,dataLocation,equipment,params['q'])
		else {
			render (view: '/entity/list', model:model(orders, dataLocation, equipment) << [
				template:"preventiveOrder/preventiveOrderList",
				filterTemplate:"preventiveOrder/preventiveOrderFilter",
				listTop:"preventiveOrder/listTop",
			])
		}
	}

	def model(def entities, def dataLocation, def equipment) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			dataLocation:dataLocation,
			equipment:equipment,
			code:getLabel(),
		]
	}

	def ajaxModel(def entities,def dataLocation,def equipment, def searchTerm) {
		def model = model(entities, dataLocation, equipment) << [q:searchTerm]
		def listHtml = g.render(template:"/entity/preventiveOrder/preventiveOrderList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}
	
	def summaryPage = {
		if(user.location instanceof DataLocation) redirect (controller: "preventiveOrderView", action: "list",params:['dataLocation.id':user.location.id])

		def location = Location.get(params.long('location'))
		def dataLocationTypesFilter = getLocationTypes()
		def template = null
		def maintenances = null

		adaptParamsForList()
		def locationSkipLevels = maintenanceService.getSkipLocationLevels()

		if (location != null) {
			template = '/orderSummaryPage/sectionTable'
			maintenances = maintenanceService.getMaintenancesByLocation(PreventiveOrder.class,location,dataLocationTypesFilter,params)
		}
		render (view: '/orderSummaryPage/summaryPage', model: [
					maintenances:maintenances?.maintenanceList,
					currentLocation: location,
					currentLocationTypes: dataLocationTypesFilter,
					template: template,
					entityCount: maintenances?.totalCount,
					locationSkipLevels: locationSkipLevels,
					controller:'preventiveOrderView',
					code:getLabel()
				])
	}
	
	

}
