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
import org.chai.memms.AbstractEntityController;
import org.chai.memms.corrective.maintenance.Comment;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.security.User.UserType;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.spare.part.SparePartService;


/**
 * @author Jean Kahigiso M.
 *
 */
class WorkOrderController extends AbstractEntityController{
	
	def locationService
	def equipmentService
	def equipmentStatusService
	def workOrderStatusService
	def notificationWorkOrderService
	def userService
	def sparePartService

	def getEntity(def id) {
		return WorkOrder.get(id)
	}

	def createEntity() {
		return new WorkOrder();
	}
	
	
	def getTemplate() {
		return "/entity/workOrder/createWorkOrder";
	}

	def getLabel() {
		return "work.order.label";
	}

	def getEntityClass() {
		return WorkOrder.class;
	}

	def getModel(entity) {
		def equipments =  []
		def compatibleSpareParts = [:]
		def dataLocation = DataLocation.get(params.long("dataLocation.id"))
		dataLocation = (dataLocation)?dataLocation:entity.equipment?.dataLocation
		def usersInCharge = userService.getActiveUserByTypeAndLocation([UserType.HOSPITALDEPARTMENT,UserType.TECHNICIANMMC,UserType.TITULAIREHC,UserType.TECHNICIANDH],dataLocation,['sort':'firstname'])
		if(entity.equipment) equipments << entity.equipment
		if(entity.id!=null) compatibleSpareParts = sparePartService.getCompatibleSparePart(entity.equipment.type,user,dataLocation)

		[
			order:entity,
			equipments: equipments,
			dataLocation: dataLocation,
			currencies: grailsApplication.config.site.possible.currency,
			orderClosed:(entity.currentStatus == OrderStatus.CLOSEDFIXED || entity.currentStatus == OrderStatus.CLOSEDFORDISPOSAL)? true:false,
			//entity can be null?
			technicians : usersInCharge,
			compatibleSpareParts:compatibleSpareParts
		]
	}

	def bindParams(def entity) {
		if(!entity.id){
			entity.addedBy = user
			entity.openOn = now
			entity.failureReason = FailureReason.NOTSPECIFIED
			entity.currentStatus = OrderStatus.OPENATFOSA
		}else{
			params.oldStatus = entity.currentStatus
			//Only change currentStatus value if currentStatus was changed
			entity.currentStatus = (!params.currentStatus)?entity.currentStatus:OrderStatus."$params.currentStatus"
			entity.lastModifiedBy = user
			if(entity.currentStatus.equals(OrderStatus.CLOSEDFIXED) || entity.currentStatus.equals(OrderStatus.CLOSEDFORDISPOSAL))
				entity.closedOn = now
			else entity.closedOn = null
		}
		if(log.isDebugEnabled()) log.debug("oldStatus= "+params.oldStatus+" newStatus= "+entity.currentStatus)
		
		//Don't bind if a closed workOrder is being edited
		if(!params.oldStatus.equals(OrderStatus.CLOSEDFIXED) && !params.oldStatus.equals(OrderStatus.CLOSEDFORDISPOSAL))
			bindData(entity,params,[exclude:["spareParts"]])
	}
	
	def validateEntity(def entity) {
		def updateClosedOrder = true
		if(entity.id!=null){
			//Making sure we cannot modify a closed workOrder
			if(log.isDebugEnabled()) log.debug("in validate closure oldStatus= "+params.oldStatus)
			if(params.oldStatus.equals(OrderStatus.CLOSEDFIXED) || params.oldStatus.equals(OrderStatus.CLOSEDFORDISPOSAL)){
				flash.message = message(code: "error.cannot.modify.closed.work.order", default: 'Cannot modify a closed equipment, please reactivate it')
				updateClosedOrder = false
			}
			if(log.isDebugEnabled()) log.debug("updateClosedOrder value= "+updateClosedOrder)
			
		}
		return (updateClosedOrder & entity.validate())
	}
		
	def saveEntity(def entity) {
		if(log.isDebugEnabled()) log.debug("saveEntity is called: "+entity.criticality)
		def equipment
		def newEntity = false
		def escalation = false
		def users = []
		
		//Change Equipment Status and Create first workOrderStatus to the new workOrder
		if(entity.id==null){
			newEntity=true
			entity = workOrderStatusService.createWorkOrderStatus(entity,OrderStatus.OPENATFOSA,user,escalation)
			equipment = equipmentStatusService.createEquipmentStatus(user,Status.UNDERMAINTENANCE,entity.equipment,now,[:])
			equipment.addToWorkOrders(entity)
			equipment.save(failOnError:true)
		}else{
			if(log.isDebugEnabled()) log.debug("Old status stored in params: "+params.oldStatus)
			//If status has be changed
			if(entity.currentStatus != params.oldStatus){
				//Escalate
				if(entity.currentStatus == OrderStatus.OPENATMMC && params.oldStatus == OrderStatus.OPENATFOSA) escalation = true			
				//Change Equipment Status When closing workorder
				if(entity.currentStatus == OrderStatus.CLOSEDFIXED)
					equipment = equipmentStatusService.createEquipmentStatus(user,Status.OPERATIONAL,entity.equipment,now,[:])
				if(entity.currentStatus == OrderStatus.CLOSEDFORDISPOSAL)
					equipment = equipmentStatusService.createEquipmentStatus(user,Status.FORDISPOSAL,entity.equipment,now,[:])
					
				entity = workOrderStatusService.createWorkOrderStatus(entity,entity.currentStatus,user,escalation)
			}
		}
		
		if(log.isDebugEnabled()) log.debug("Created or updated workOrder: "+entity)
		if(newEntity || escalation){ //TODO define default message
			def content = "Please review work order on equipment with code: ${entity.equipment.code}"
			//notificationWorkOrderService.newNotification(entity,message(code:"workorder.creation.default.message"),user,false)
			notificationWorkOrderService.newNotification(entity,content,user,false)
		}		
	}


}


