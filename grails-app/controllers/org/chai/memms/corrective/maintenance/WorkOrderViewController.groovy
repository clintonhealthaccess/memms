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

import org.chai.location.CalculationLocation;
import java.util.Date;
import java.util.Map;
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.AbstractController;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.corrective.maintenance.Comment;
import org.chai.memms.corrective.maintenance.CorrectiveProcess;
import org.chai.memms.corrective.maintenance.CorrectiveProcess.ProcessType;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePartService;


/**
 * @author Jean Kahigiso M.
 *
 */
class WorkOrderViewController extends AbstractController{
	
	def workOrderService
	def maintenanceService
	def commentService
	def maintenanceProcessService
	def userService
	def sparePartService

	def getLabel() {
		return "work.order.label";
	}

	def getEntityClass() {
		return WorkOrder.class;
	}
	
	def getWorkOrderClueTipsAjaxData = {
		def workOrder = WorkOrder.get(params.long("id"))
		def html = g.render(template:"/templates/workOrderClueTip",model:[workOrder:workOrder])
		render(contentType:"text/plain", text:html)
	}
	
	def list = {
		def orders= []
		def equipment = null
		def  dataLocation = null
		if(params["dataLocation.id"]) dataLocation = CalculationLocation.get(params.int("dataLocation.id"))
		if(params["equipment.id"]) equipment = Equipment.get(params.int("equipment.id"))
		
		if(dataLocation && !user.canAccessCalculationLocation(dataLocation)) response.sendError(404)
		else if(equipment && !user.canAccessCalculationLocation(equipment.dataLocation)) response.sendError(404)
		else{
			adaptParamsForList()
			if(dataLocation)
				orders = maintenanceService.getMaintenanceOrderByCalculationLocation(WorkOrder.class,dataLocation,params)
			if(equipment)
				orders= maintenanceService.getMaintenanceOrderByEquipment(WorkOrder.class,equipment,params)
			if(request.xhr){
				this.ajaxModel(orders,dataLocation,equipment,"")
			}else{
				render(view:"/entity/list", model: model(orders, dataLocation, equipment) << [
					template:"workOrder/workOrderList",
					filterTemplate:"workOrder/workOrderFilter",
					listTop:"workOrder/listTop",
				])
			}
		}
	}
	
	def workOrdersEscalatedAtMMC={
		if (log.isDebugEnabled()) log.debug("listing.workOrdersEscalatedToMMC start, params:"+params)
		def equipment = null
		def  dataLocation = null
		if(params["dataLocation.id"]) dataLocation = CalculationLocation.get(params.int("dataLocation.id"))
		if(params["equipment.id"]) equipment = Equipment.get(params.int("equipment.id"))

		adaptParamsForList()
		def workOrders = workOrderService.getWorkOrdersEscalatedToMMC(user,params)
		if(!request.xhr)
			render(view:"/entity/list", model: model(workOrders, dataLocation, equipment) << [
					template:"workOrder/workOrderList",
					filterTemplate:"workOrder/workOrderFilter",
					listTop:"workOrder/listTop",
				])
	}
		
	def search = {
		def equipment = null
		def dataLocation = null
		if(params["equipment.id"])
			equipment = Equipment.get(params.long("equipment.id"))
		else if(params["dataLocation.id"])
			dataLocation = DataLocation.get(params.long('dataLocation.id'))
			
		adaptParamsForList()
		def orders = maintenanceService.searchOrder(WorkOrder.class,params['q'],dataLocation,equipment,params)
		if(request.xhr)
			this.ajaxModel(orders,dataLocation,equipment,params['q'])
		else {
			render(view:"/entity/list", model: model(orders, dataLocation, equipment) << [
				template:"workOrder/workOrderList",
				filterTemplate:"workOrder/workOrderFilter",
				listTop:"workOrder/listTop",
			])	
		}

	}
	
	
	def escalate = {
		WorkOrder order = WorkOrder.get(params.int("order"))
		DataLocation dataLocation = DataLocation.get(params.int("dataLocation"))
		def result = false
		def html = ""
		if (!order || order.currentStatus.equals(OrderStatus.CLOSEDFIXED) || order.currentStatus.equals(OrderStatus.CLOSEDFORDISPOSAL))
			response.sendError(404)
		else {
			def equipment = order.equipment
			//TODO define default escalation message
			def content = "Please review work order on equipment with code: ${order.equipment.code}"
			workOrderService.escalateWorkOrder(order, content, user)
			result=true
			def orders
			if(dataLocation)
				orders = maintenanceService.getMaintenanceOrderByCalculationLocation(WorkOrder.class,dataLocation,[:])
			else orders= maintenanceService.getMaintenanceOrderByEquipment(WorkOrder.class,equipment,[:])
			
			html = g.render(template:"/entity/workOrder/workOrderList",model:[equipment:equipment,entities:orders,dataLocation:dataLocation])
			if(!request.xhr)
				response.sendError(404)
			this.ajaxModel(orders,null,equipment,params['q'])
		}
	}
	
	def filter = { FilterWorkOrderCommand cmd ->
		if(log.isDebugEnabled()) log.debug("workOrder filter command object:"+cmd)
		
		adaptParamsForList()
		def orders = workOrderService.filterWorkOrders(cmd.dataLocation,cmd.equipment,cmd.openOn,cmd.closedOn,cmd.criticality,cmd.currentStatus,params)
		if(request.xhr)
			this.ajaxModel(orders,cmd.dataLocation,cmd.equipment,"")
		else {
			render(view:"/entity/list", model: model(orders, cmd.dataLocation, cmd.equipment) << [
				template:"workOrder/workOrderList",
				filterTemplate:"workOrder/workOrderFilter",
				listTop:"workOrder/listTop",
				code: getLabel()
			])
		}
	}
	
	def model(def entities, def dataLocation, def equipment) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			dataLocation:dataLocation,
			equipment:equipment,
			code: getLabel()
		]
	}
	
	def ajaxModel(def entities,def dataLocation,def equipment, def searchTerm) {
		def model = model(entities, dataLocation, equipment) << [q:searchTerm]
		def listHtml = g.render(template:"/entity/workOrder/workOrderList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	def summaryPage = {
		if(user.location instanceof DataLocation) redirect (controller: "workOrderView", action: "list",params:['dataLocation.id':user.location.id])

		def location = Location.get(params.int('location'))
		def dataLocationTypesFilter = getLocationTypes()
		def template = null
		def maintenances = null
		def locationSkipLevels = maintenanceService.getSkipLocationLevels()

		adaptParamsForList()
		log.debug("location = " +location)

		if(params['q'] != null && !params['q'].empty){
			if(location == null){
				location = user.location
			}
			List<DataLocation> dataLocations = locationService.searchLocation(DataLocation.class, params['q'], params)
			maintenances = maintenanceService.getMaintenancesByDataLocation(WorkOrder.class,location,dataLocations,dataLocationTypesFilter,params)
		}
		else if (location != null) 
			maintenances = maintenanceService.getMaintenancesByLocation(WorkOrder.class,location,dataLocationTypesFilter,params)
		
		if(log.isDebugEnabled()) log.debug("maintenances: " +maintenances?.maintenanceList)
		render (view: '/orderSummaryPage/summaryPage', model: [
					maintenances:maintenances?.maintenanceList,
					currentLocation: location,
					currentLocationTypes: dataLocationTypesFilter,
					template: "/orderSummaryPage/sectionTable",
					entityCount: maintenances?.totalCount,
					locationSkipLevels: locationSkipLevels,
					controller:'workOrderView',
					code:getLabel()
				])
	}


	def addProcess = {
		WorkOrder order = WorkOrder.get(params.int("order.id"))
		def type = params["type"].toUpperCase()
		type = ProcessType."$type"
		def value = params["value"]
		def result = false
		def html =""
		if (order == null || order.currentStatus.equals(OrderStatus.CLOSEDFIXED) || order.currentStatus.equals(OrderStatus.CLOSEDFORDISPOSAL) || type==null || value.equals(""))
			response.sendError(404)
		else {
				if (log.isDebugEnabled()) log.debug("addProcess params: "+params)
				maintenanceProcessService.addWorkOrderProcess(order,type,value,user)	
				if(order!=null){
					result=true
					def processes = (type==ProcessType.ACTION)? order.actions:order.materials
					html = g.render(template:"/templates/processList",model:[processes:processes,type:type.name])
				}
				render(contentType:"text/json") { results = [result,html,type.name] }
		}
	}

	def removeProcess = {
		CorrectiveProcess  process = CorrectiveProcess.get(params.int("process.id"))
		def result = false
		def html =""
		def type =null
		if(!process || process.workOrder.currentStatus.equals(OrderStatus.CLOSEDFIXED) || process.workOrder.currentStatus.equals(OrderStatus.CLOSEDFORDISPOSAL)) 
			response.sendError(404)
		else{
			type = process.type
			WorkOrder order = maintenanceProcessService.deleteWorkOrderProcess(process,user)
			result = true
			def processes = (type==ProcessType.ACTION)? order.actions:order.materials
			html = g.render(template:"/templates/processList",model:[processes:processes,type:type.name])
		}
		render(contentType:"text/json") { results = [result,html,type.name]}
	}
	
	def addComment ={
		WorkOrder order = WorkOrder.get(params.int("order.id"))
		def html =""
		def content = params["content"]
		def result = false
		if (order == null || content.equals("") || order.currentStatus.equals(OrderStatus.CLOSEDFIXED) || order.currentStatus.equals(OrderStatus.CLOSEDFORDISPOSAL))
			response.sendError(404)
		else {
			def comment = commentService.createComment(order,user,content)
			if(comment==null) response.sendError(404)
			else{ 
				result=true
				html = g.render(template:"/templates/comments",model:[order:order])
			}
		}
		render(contentType:"text/json") { results = [result,html]}
	}
	
	def removeComment = {
		Comment comment = Comment.get(params.int("comment.id"))
		WorkOrder order
		def html =""
		def result = false
		if(!comment || comment.workOrder.currentStatus.equals(OrderStatus.CLOSEDFIXED) || comment.workOrder.currentStatus.equals(OrderStatus.CLOSEDFORDISPOSAL)) 
			response.sendError(404)
		else{
			order = commentService.deleteComment(comment,user)
			result = true
			html = g.render(template:"/templates/comments",model:[order:order])
		}
		render(contentType:"text/json") { results = [result,html]}
	}
	def addSpareParts = {
		WorkOrder order = WorkOrder.get(params.int("order.id"))
		SparePartType type = SparePartType.get(params.int("sparePart.type.id"))
		def quantity = params.int("quantity")
		if(log.isDebugEnabled()) log.debug("WorkOrder: "+order+" SparePartType: :"+type+" quantity: "+quantity)
		def html =""
		def result = false
		def options = ""
		if(order == null || type == null || quantity <= 0 || quantity == null)
			response.sendError(404)
		else{
			def dataLocation = order.equipment.dataLocation
			order = sparePartService.assignSparePartsToWorkOrder(order,type,user,quantity)
			def compatibleSpareParts = sparePartService.getCompatibleSparePart(order.equipment.type,user,dataLocation)
			for(def sparePart: compatibleSpareParts)
				options = options+" <option value='"+sparePart.key.id+"'>"+sparePart.key.names+" - "+[sparePart.value]+"</option>";
			result = true
			html = g.render(template:"/templates/usedSparePartList",model:[order:order,usedSpareParts:order.usedSpareParts])
		}

		render(contentType:"text/json") { results = [result,html,options]}
	}
	
	def export = {
		DataLocation dataLocation = DataLocation.get(params.int('dataLocation.id'))
		adaptParamsForList()
		def workOrders = workOrderService.exportWorkOrders(dataLocation)
		if (log.isDebugEnabled()) log.debug("WORK ORDERS TO BE EXPORTED IN SIZE "+workOrders.size())
		File file = workOrderService.exporter(dataLocation?:user.location, workOrders)

		response.setHeader "Content-disposition", "attachment; filename=${file.name}.csv"
		response.contentType = 'text/csv'
		response.outputStream << file.text
		response.outputStream.flush()

}

class FilterWorkOrderCommand {
	
	Date openOn
	Date closedOn
	Criticality criticality
	OrderStatus currentStatus
	DataLocation dataLocation
	Equipment equipment


	static constraints = {
		dataLocation  nullable:true
		equipment nullable:true
		openOn nullable:true
		closedOn nullable:true
		currentStatus nullable:true
		criticality nullable:true
	}

	String toString() {
		return "FilterCommand[OrderStatus="+currentStatus+", Criticality="+criticality+ 
		", closedOn="+closedOn+", openOn="+openOn+"]"
	}
}

class FilterCommand {
	DataLocation dataLocation

	static constraints = {
		dataLocation nullable:false
	}

	String toString() {
		return "FilterCommand[DataLocation="+dataLocation+"]"
	}
}
}

