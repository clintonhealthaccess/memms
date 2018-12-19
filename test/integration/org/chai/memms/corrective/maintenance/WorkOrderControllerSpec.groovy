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
import java.util.Map;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderController;
import org.chai.memms.security.User;

/**
 * @author Jean Kahigiso M.
 *
 */
class WorkOrderControllerSpec extends IntegrationTests{
	
	def workOrderController
	def equipmentStatusService
	def workOrderStatusService
	
	def "can create and save a workOrder"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		workOrderController = new WorkOrderController()
		when:
		workOrderController.params."equipment.id" = equipment.id
		workOrderController.params.description = "equiment description"
		workOrderController.params.criticality = "NORMAL"
		workOrderController.params.disposalRefNumber = "DISPREFNUM01"
		workOrderController.save()
		def workOrder = WorkOrder.list()[0]
		then:
		WorkOrder.count()==1
		workOrder.currentStatus == OrderStatus.OPENATFOSA
		workOrder.timeBasedStatus == WorkOrderStatus.list()[0]
		workOrder.timeBasedStatus.status == OrderStatus.OPENATFOSA
		workOrder.equipment == equipment
		//check equipment update
		equipment.status.size() ==1
		equipment.workOrders.size() ==1
		equipment.currentStatus == Status.UNDERMAINTENANCE
		equipment.timeBasedStatus == EquipmentStatus.list()[0]
		
		
	}
	
	def "can update (when escalating) workOrder status and update equipment status"(){
		setup:		
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = User.findByUsername("systemUser")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,
			currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED,
			)
		equipment.addToWorkOrders(workOrder)
		equipment.save(failOnError:true)
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,user,false)
		equipmentStatusService.createEquipmentStatus(user,Status.UNDERMAINTENANCE,equipment,Initializer.now(),null,[:])
		when:
		workOrderController = new WorkOrderController()
		workOrderController.params."id" = workOrder.id+''
		workOrderController.params.criticality = 'LOW'
		workOrderController.params.currentStatus = 'OPENATMMC'
		workOrderController.save()

		then:
		WorkOrder.count()==1
		WorkOrderStatus.count() == 2
		workOrder.criticality == Criticality.LOW
		workOrder.currentStatus == OrderStatus.OPENATMMC
		workOrder.timeBasedStatus == WorkOrderStatus.list()[1]
		workOrder.timeBasedStatus.status == OrderStatus.OPENATMMC
		workOrder.status.asList().size() == 2
		workOrder.status.asList().contains(workOrder.timeBasedStatus)
		workOrder.timeBasedStatus.escalation == true
		//check equipment update
		equipment.status.asList()[0] == EquipmentStatus.list()[0]
		equipment.status.asList()[0].status == Status.UNDERMAINTENANCE
		equipment.currentStatus == Status.UNDERMAINTENANCE
		equipment.timeBasedStatus == EquipmentStatus.list()[0]
		equipment.timeBasedStatus.status == Status.UNDERMAINTENANCE
		equipment.workOrders.size() ==1
		equipment.workOrders.asList().contains(workOrder)
		
	}
	
	def "can update  (de-escalating) workOrder status and update equipment status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = User.findByUsername("systemUser")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,
			currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		equipment.addToWorkOrders(workOrder)
		equipment.save(failOnError:true)
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,user,false)
		equipmentStatusService.createEquipmentStatus(user,Status.UNDERMAINTENANCE,equipment,Initializer.now(),null,[:])
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATMMC,user,true)
		when:
		workOrderController = new WorkOrderController()
		workOrderController.params."id" = workOrder.id+''
		workOrderController.params.criticality = 'NORMAL'
		workOrderController.params.currentStatus = 'OPENATFOSA'
		workOrderController.save()

		then:
		WorkOrder.count()==1
		WorkOrderStatus.count() == 3
		workOrder.criticality == Criticality.NORMAL
		workOrder.currentStatus == OrderStatus.OPENATFOSA
		workOrder.timeBasedStatus == WorkOrderStatus.list()[2]
		workOrder.timeBasedStatus.status == OrderStatus.OPENATFOSA
		workOrder.status.asList().size() == 3
		workOrder.status.asList().contains(workOrder.timeBasedStatus)
		//check equipment update
		equipment.status.asList()[0] == EquipmentStatus.list()[0]
		equipment.status.asList()[0].status == Status.UNDERMAINTENANCE
		equipment.currentStatus == Status.UNDERMAINTENANCE
		equipment.timeBasedStatus == EquipmentStatus.list()[0]
		equipment.timeBasedStatus.status == Status.UNDERMAINTENANCE
		equipment.workOrders.size() ==1
		equipment.workOrders.asList().contains(workOrder)
		
	}
	
	def "can update (closed fixed) workOrder and update equipment status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = User.findByUsername("systemUser")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,
			currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		equipment.addToWorkOrders(workOrder)
		equipment.save(failOnError:true)
		
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,user,false)
		equipmentStatusService.createEquipmentStatus(user,Status.UNDERMAINTENANCE,equipment,Initializer.now(),null,[:])
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATMMC,user,true)
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,user,false)
		
		when://CLOSEDFIXED
		workOrderController = new WorkOrderController()
		workOrderController.params."id" = workOrder.id+''
		workOrderController.params.criticality = 'NORMAL'
		workOrderController.params.currentStatus = 'CLOSEDFIXED'
		workOrderController.save()

		then:
		WorkOrder.count()==1
		WorkOrderStatus.count() == 4
		WorkOrder.list()[0].criticality == Criticality.NORMAL
		WorkOrder.list()[0].currentStatus == OrderStatus.CLOSEDFIXED
		WorkOrder.list()[0].timeBasedStatus == WorkOrderStatus.list()[3]
		WorkOrder.list()[0].timeBasedStatus.status == OrderStatus.CLOSEDFIXED
		WorkOrder.list()[0].status.size() == 4
		WorkOrder.list()[0].status.asList().contains(workOrder.timeBasedStatus)
		WorkOrder.list()[0].closedOn !=null
		//check equipment update
		equipment.status.size() == 2
		equipment.currentStatus == Status.OPERATIONAL
		equipment.timeBasedStatus.status == Status.OPERATIONAL
		equipment.workOrders.size() ==1
		equipment.workOrders.asList().contains(workOrder)
	}
	
	def "can update (closed for disposal) workOrder and update equipment status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = User.findByUsername("systemUser")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,
			currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED,
			)
		equipment.addToWorkOrders(workOrder)
		equipment.save(failOnError:true,flush:true)
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,user,false)
		equipmentStatusService.createEquipmentStatus(user,Status.UNDERMAINTENANCE,equipment,Initializer.now(),null,[:])
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATMMC,user,true)
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,user,false)
	
		when://CLOSEDFORDISPOSAL
		workOrderController = new WorkOrderController()
		workOrderController.params."id" = workOrder.id+''
		workOrderController.params.criticality 	= 'NORMAL'
		workOrderController.params.currentStatus = 'CLOSEDFORDISPOSAL'
		workOrderController.params.disposalRefNumber = 'DISPMEC001'
		workOrderController.save()

		then:
		WorkOrder.count()==1
		WorkOrderStatus.count() == 4
		WorkOrder.list()[0].criticality == Criticality.NORMAL
		WorkOrder.list()[0].currentStatus == OrderStatus.CLOSEDFORDISPOSAL
		WorkOrder.list()[0].timeBasedStatus == WorkOrderStatus.list()[3]
		WorkOrder.list()[0].timeBasedStatus.status == OrderStatus.CLOSEDFORDISPOSAL
		WorkOrder.list()[0].status.size() == 4
		WorkOrder.list()[0].status.asList().contains(workOrder.timeBasedStatus)
		WorkOrder.list()[0].closedOn !=null
		//check equipment update
		equipment.status.size() == 2
		equipment.currentStatus == Status.FORDISPOSAL
		equipment.timeBasedStatus.status == Status.FORDISPOSAL
		equipment.workOrders.size() ==1
		equipment.workOrders.asList().contains(workOrder)
		
	}
	
	def "can't update a closed work order"(){
		setup:	
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = User.findByUsername("systemUser")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,
			currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED,
			)
		equipment.addToWorkOrders(workOrder)
		equipment.save(failOnError:true,flush:true)
		
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,user,false)
		equipmentStatusService.createEquipmentStatus(user,Status.UNDERMAINTENANCE,equipment,Initializer.now(),null,[:])
		workOrderStatusService.createWorkOrderStatus(workOrder,OrderStatus.CLOSEDFIXED,user,false)
		equipmentStatusService.createEquipmentStatus(user,Status.OPERATIONAL,equipment,Initializer.now(),null,[:])
		
		when:
		workOrderController = new WorkOrderController()
		workOrderController.params."id" = workOrder.id+''
		workOrderController.params.description = 'testing'
		workOrderController.params.criticality = 'LOW'
		workOrderController.save()

		then:
		WorkOrder.count()==1
		WorkOrderStatus.count() == 2
		WorkOrder.list()[0].description == "test work order"
		WorkOrder.list()[0].criticality == Criticality.NORMAL
		//check equipment update
		equipment.workOrders.size() == 1
		equipment.timeBasedStatus.status==Status.OPERATIONAL
		
		
	}
	
	
}
