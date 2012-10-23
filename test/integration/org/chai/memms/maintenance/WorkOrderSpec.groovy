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
package org.chai.memms.maintenance

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.equipment.Equipment;
import org.chai.memms.maintenance.MaintenanceProcess.ProcessType;
import org.chai.memms.maintenance.WorkOrder.Criticality;
import org.chai.memms.maintenance.WorkOrder.FailureReason;
import org.chai.memms.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.maintenance.WorkOrderStatus
import org.chai.memms.maintenance.WorkOrder
import org.chai.memms.security.User;


class WorkOrderSpec extends IntegrationTests{
	
	def "can create a workOrder"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatus =  new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATFOSA,changeOn:Initializer.now(),changedBy:user);
		workOrder.addToStatus(workOrderStatus)
		workOrder.save(failOnError:true)
		then:
		WorkOrder.count() == 1
		WorkOrder.list()[0].status.status.equals([OrderStatus.OPENATFOSA])
		WorkOrderStatus.count() == 1
	}
	def "if status is clossed closedOn should be set and to newer that openOn"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment =  Equipment.findBySerialNumber(CODE(123))
		when:
		def workOrder = new WorkOrder(equipment:equipment, description: "test work order", criticality:Criticality.NORMAL, currentStatus:OrderStatus.CLOSEDFORDISPOSAL,
			addedBy:user,openOn:Initializer.now()-1,failureReason:FailureReason.NOTSPECIFIED)
		workOrder.save()
		then:
		workOrder.errors.hasFieldErrors("closedOn") == true
		WorkOrder.count() == 0
		
		when:
		def workOrderOne = new WorkOrder(equipment:equipment, description: "test work order", criticality:Criticality.NORMAL, currentStatus:OrderStatus.CLOSEDFORDISPOSAL,
			addedBy:user,openOn:Initializer.now()-1,failureReason:FailureReason.NOTSPECIFIED)
		workOrderOne.closedOn = Initializer.now()-2
		workOrderOne.save()
		then:
		workOrderOne.errors.hasFieldErrors("closedOn") == true
		workOrderOne.count() == 0
	}

	def "retrieve notifications received by user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		def senderTwo = newUser("senderTwo", true,true)
		def receiver = newUser("receiver", true,true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment,"Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		workOrder.addToNotifications(Initializer.newWorkOrderNotification(workOrder, senderOne, receiver,Initializer.now(), "test one"))
		workOrder.addToNotifications(Initializer.newWorkOrderNotification(workOrder, senderTwo, receiver,Initializer.now(), "test one"))
		workOrder.save(failOnError:true)
		then:
		workOrder.notifications.size() == 2
		workOrder.getNotificationsReceivedByUser(receiver).size() == 2
	}
	
	def "retrieve unread notifications for a user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		def receiver = newUser("receiver", true,true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment,"Nothing yet",Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		def notificationOne = Initializer.newWorkOrderNotification(workOrder, sender, receiver,Initializer.now(), "test one")
		def notificationTwo = Initializer.newWorkOrderNotification(workOrder, sender, receiver,Initializer.now(), "test Two")
		
		workOrder.addToNotifications(notificationOne)
		workOrder.addToNotifications(notificationTwo)
		workOrder.save(failOnError:true)
		
		notificationTwo.read = true
		notificationTwo.save(failOnError:true)
		then:
		workOrder.notifications.size() == 2
		workOrder.getUnReadNotificationsForUser(receiver).size() == 1
	}
	
	def "retrieve notifications sent by user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		def receiver = newUser("receiver", true,true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment,"Nothing yet",Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		def notificationOne = Initializer.newWorkOrderNotification(workOrder, sender, receiver,Initializer.now(), "test one")
		def notificationTwo = Initializer.newWorkOrderNotification(workOrder, receiver, sender,Initializer.now(), "test Two")
		
		workOrder.addToNotifications(notificationOne)
		workOrder.addToNotifications(notificationTwo)
		workOrder.save(failOnError:true)
		
		notificationTwo.read = true
		notificationTwo.save(failOnError:true)
		then:
		workOrder.notifications.size() == 2
		workOrder.getNotificationsSentByUser(sender).size() == 1
	}
	
	def "get list of actions performed"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment,"Nothing yet",Criticality.NORMAL,user,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		def actionOne = Initializer.newMaintenanceProcess(workOrder,ProcessType.ACTION,"Action 1",Initializer.now(), user)
		def actionTwo = Initializer.newMaintenanceProcess(workOrder,ProcessType.ACTION,"Action 2",Initializer.now(), user)
		def actionThree = Initializer.newMaintenanceProcess(workOrder,ProcessType.ACTION,"Action 3",Initializer.now(), user)
		def materialFour = Initializer.newMaintenanceProcess(workOrder,ProcessType.MATERIAL,"Material 1",Initializer.now(), user)
		workOrder.processes=[actionOne,actionTwo,actionThree,materialFour]
		then:
		workOrder.actions.size() == 3
		workOrder.actions.each{[actionOne,actionTwo,actionThree].contains(it) }
		
	} 
	def "get list of used materials performed"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		def materialOne = Initializer.newMaintenanceProcess(workOrder,ProcessType.MATERIAL,"Material 1",Initializer.now(), user)
		def actionTwo = Initializer.newMaintenanceProcess(workOrder,ProcessType.ACTION,"Action 2",Initializer.now(), user)
		def actionThree = Initializer.newMaintenanceProcess(workOrder,ProcessType.ACTION,"Action 3",Initializer.now(), user)
		def materialFour = Initializer.newMaintenanceProcess(workOrder,ProcessType.MATERIAL,"Material 2",Initializer.now(), user)
		workOrder.processes=[materialOne,actionTwo,actionThree,materialFour]
		then:
		workOrder.materials.size() == 2
		workOrder.materials.each{[materialOne,materialFour].contains(it) }
		
	}
	def "can't have value on closedOn when it not a closed status workOrderStatus based on Time"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatusOne =  Initializer.newWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,Initializer.now(),user,false);
		workOrder.save(failOnError:true)
		when:
		workOrder.closedOn = Initializer.now()
		workOrder.save()
		then:
		WorkOrder.count() == 1
		workOrder.errors.hasFieldErrors("closedOn") == true
	}
	def "get current workOrderStatus based on Time"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatusOne =  Initializer.newWorkOrderStatus(workOrder,OrderStatus.OPENATFOSA,Initializer.now(),user,false);
		workOrder.save(failOnError:true)
		when:
		def workOrderStatusTwo =  Initializer.newWorkOrderStatus(workOrder,OrderStatus.CLOSEDFIXED,Initializer.now(),user,false);
		workOrder.closedOn = Initializer.now()
		workOrder.save(failOnError:true)
		then:
		WorkOrder.count() == 1
		WorkOrderStatus.list().size()==2
		WorkOrder.list()[0].status.size()==2
		WorkOrder.list()[0].currentStatus==WorkOrder.list()[0].timeBasedStatus.status
	}
	def "get escalation status an escalted workOrder"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatus =  new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATFOSA,changeOn:Initializer.now(),changedBy:user);
		workOrder.addToStatus(workOrderStatus)
		workOrder.save(failOnError:true)
		then:
		WorkOrder.count() == 1
		WorkOrder.list()[0].status.status.equals([OrderStatus.OPENATFOSA])
		WorkOrderStatus.count() == 1
	}
}
