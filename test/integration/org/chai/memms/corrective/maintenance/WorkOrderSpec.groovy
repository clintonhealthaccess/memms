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

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.WorkOrderStatusChange;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.CorrectiveProcess.ProcessType;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
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
		def workOrderStatus =  new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATFOSA,changedBy:user);
		workOrder.addToStatus(workOrderStatus)
		workOrder.save()
		then:
		equipment!=null
		WorkOrder.count() == 1
		WorkOrder.list()[0].status.status.equals([OrderStatus.OPENATFOSA])
		WorkOrderStatus.count() == 1
		workOrder.currentStatus ==  OrderStatus.OPENATFOSA
		workOrder.timeBasedStatus.status ==  OrderStatus.OPENATFOSA
		workOrder.status.size() ==1
	}
	
	def "if status is clossed closedOn should be set and to newer than openOn"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment =  Equipment.findBySerialNumber(CODE(123))
		when:
		def workOrder = new WorkOrder(equipment:equipment, description: "test work order", criticality:Criticality.NORMAL, currentStatus:OrderStatus.CLOSEDFORDISPOSAL,
			addedBy:user,dateCreated:Initializer.now()-3,openOn:Initializer.now()-1,failureReason:FailureReason.NOTSPECIFIED)
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
		def workOrder = new WorkOrder(equipment:equipment, description: "Nothing yet", criticality:Criticality.NORMAL, currentStatus:OrderStatus.OPENATFOSA,
			addedBy:senderOne,openOn:Initializer.now()-1,failureReason:FailureReason.NOTSPECIFIED).save(failOnError:true)
		when:
		def notificationOne = new NotificationWorkOrder(workOrder:workOrder,sender: senderOne, receiver: receiver, content: "test one")
		def notificationTwo = new NotificationWorkOrder(workOrder:workOrder,sender: senderTwo, receiver: receiver, content: "test two")
		workOrder.notifications = [notificationOne,notificationTwo]
		workOrder.save(failOnError:true)
		then:
		WorkOrder.list()[0].notifications.size() == 2
		WorkOrder.list()[0].getNotificationsReceivedByUser(receiver).size() == 2
	}
	
	def "retrieve unread notifications for a user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		def receiver = newUser("receiver", true,true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = new WorkOrder(equipment:equipment, description: "Nothing yet", criticality:Criticality.NORMAL, currentStatus:OrderStatus.OPENATFOSA,
			addedBy:sender,openOn:Initializer.now()-1,failureReason:FailureReason.NOTSPECIFIED).save(failOnError:true)
		when:
		def notificationOne = new NotificationWorkOrder(workOrder: workOrder, sender: sender, receiver: receiver, content: "test one")
		def notificationTwo = new NotificationWorkOrder(workOrder: workOrder, sender: sender, receiver: receiver, content: "test two")
		
		workOrder.addToNotifications(notificationOne)
		workOrder.addToNotifications(notificationTwo)
		workOrder.save(failOnError:true)
		notificationTwo.read = true
		notificationTwo.save(failOnError:true)
		then:
		WorkOrder.list()[0].notifications.size() == 2
		WorkOrder.list()[0].getUnReadNotificationsForUser(receiver).size() == 1
	}
	
	def "retrieve notifications sent by user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		def receiver = newUser("receiver", true,true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrder = new WorkOrder(equipment:equipment, description: "Nothing yet", criticality:Criticality.NORMAL, currentStatus:OrderStatus.OPENATFOSA,
			addedBy:sender,openOn:Initializer.now()-1,failureReason:FailureReason.NOTSPECIFIED).save(failOnError:true)
		when:
		def notificationOne = new NotificationWorkOrder(sender: sender, receiver: receiver, content: "test one")
		def notificationTwo = new NotificationWorkOrder(sender: receiver, receiver: sender, content: "test two")
		workOrder.addToNotifications(notificationOne)
		workOrder.addToNotifications(notificationTwo)
		workOrder.save(failOnError:true)
		
		notificationTwo.read = true
		notificationTwo.save(failOnError:true)
		then:
		WorkOrder.list()[0].notifications.size() == 2
		WorkOrder.list()[0].getNotificationsSentByUser(sender).size() == 1
	}
	
	def "get list of actions performed"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = new WorkOrder(equipment:equipment,description:"Nothing yet",currentStatus:OrderStatus.OPENATFOSA,criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED).save(failOnError:true)		
		when:
		def actionOne = new CorrectiveProcess(type: ProcessType.ACTION,name: "Action 1",addedBy: user)
		def actionTwo = new CorrectiveProcess(type: ProcessType.ACTION,name: "Action 2",addedBy: user)
		def actionThree = new CorrectiveProcess(type: ProcessType.ACTION,name: "Action 3",addedBy: user)
		def materialFour = new CorrectiveProcess(type: ProcessType.MATERIAL,name: "Material 1",addedBy: user)
		
		workOrder.addToProcesses(actionOne)
		workOrder.addToProcesses(actionTwo)
		workOrder.addToProcesses(actionThree)
		workOrder.addToProcesses(materialFour)
		workOrder.save(failOnError:true)
		then:
		WorkOrder.list()[0].actions.size() == 3
		WorkOrder.list()[0].actions.each{[actionOne,actionTwo,actionThree].contains(it) }
		
	} 
	def "get list of used materials performed"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = new WorkOrder(equipment:equipment,description:"test work order",currentStatus:OrderStatus.OPENATFOSA,criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED).save(failOnError:true)
		when:
		def materialOne = new CorrectiveProcess(type: ProcessType.MATERIAL,name: "Material 1",addedBy: user)
		def actionTwo = new CorrectiveProcess(type: ProcessType.ACTION,name: "Action 2",addedBy: user)
		def actionThree = new CorrectiveProcess(type: ProcessType.ACTION,name: "Action 3",addedBy: user)
		def materialFour = new CorrectiveProcess(type: ProcessType.MATERIAL,name: "Material 1",addedBy: user)
		workOrder.addToProcesses(materialOne)
		workOrder.addToProcesses(actionTwo)
		workOrder.addToProcesses(actionThree)
		workOrder.addToProcesses(materialFour)
		workOrder.save(failOnError:true)
		then:
		WorkOrder.list()[0].materials.size() == 2
		WorkOrder.list()[0].materials.each{[materialOne,materialFour].contains(it) }
		
	}
	def "can't have value on closedOn when it not a closed status workOrderStatus based on Time"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",currentStatus:OrderStatus.OPENATFOSA,criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatusOne =   new WorkOrderStatus(status:OrderStatus.OPENATFOSA,changedBy:user);
		workOrder.save(failOnError:true,flush:true)
		when:
		workOrder.closedOn = Initializer.now()
		workOrder.save()
		then:
		WorkOrder.count() == 1
		workOrder.errors.hasFieldErrors("closedOn") == true
	}

	def "get time based current status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",currentStatus:OrderStatus.OPENATFOSA,criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatusOne =  new WorkOrderStatus(status:OrderStatus.OPENATFOSA,changedBy:user);
		workOrder.addToStatus(workOrderStatusOne)
		workOrder.save(failOnError:true,flush:true)
		when:
		workOrder = WorkOrder.list()[0]
		def workOrderStatusTwo =  new WorkOrderStatus(status:OrderStatus.CLOSEDFIXED,changedBy:user);
		workOrder.closedOn = Initializer.now()
		workOrder.currentStatus =  OrderStatus.CLOSEDFIXED
		workOrder.addToStatus(workOrderStatusTwo)
		workOrder.save(failOnError:true,flush:true)
		then:
		WorkOrder.count() == 1
		WorkOrderStatus.count()==2
		WorkOrder.list()[0].status.size()==2
		WorkOrder.list()[0].currentStatus==WorkOrder.list()[0].timeBasedStatus.status
	}

	def "get time based previous status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",currentStatus:OrderStatus.OPENATFOSA,criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatusOne =  new WorkOrderStatus(status:OrderStatus.OPENATFOSA,changedBy:user);
		workOrder.addToStatus(workOrderStatusOne)
		workOrder.save(failOnError:true,flush:true)
		when:
		workOrder = WorkOrder.list()[0]
		def workOrderStatusTwo =  new WorkOrderStatus(status:OrderStatus.CLOSEDFIXED,changedBy:user);
		workOrder.closedOn = Initializer.now()
		workOrder.currentStatus =  OrderStatus.CLOSEDFIXED
		workOrder.addToStatus(workOrderStatusTwo)
		workOrder.save(failOnError:true,flush:true)
		def result = WorkOrder.list()[0]

		then:
		WorkOrder.count() == 1
		WorkOrderStatus.count()==2
		result.timeBasedStatus==workOrderStatusTwo
		result.timeBasedPreviousStatus==workOrderStatusOne	
		result.status.size()==2
		result.status.sort{ it.dateCreated }
		result.timeBasedStatus==workOrderStatusTwo
		result.timeBasedPreviousStatus==workOrderStatusOne
	}

	def "get time based status change"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",currentStatus:OrderStatus.OPENATFOSA,criticality:Criticality.NORMAL,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatusOne =  new WorkOrderStatus(status:OrderStatus.OPENATFOSA,changedBy:user);
		workOrder.addToStatus(workOrderStatusOne)
		workOrder.save(failOnError:true,flush:true)
		when:
		workOrder = WorkOrder.list()[0]
		def workOrderStatusTwo =  new WorkOrderStatus(previousStatus:workOrder.currentStatus,status:OrderStatus.CLOSEDFIXED,changedBy:user);
		workOrder.closedOn = Initializer.now()
		workOrder.currentStatus =  OrderStatus.CLOSEDFIXED
		workOrder.addToStatus(workOrderStatusTwo)
		workOrder.save(failOnError:true,flush:true)
		def result = WorkOrder.list()[0]

		then:
		WorkOrder.count() == 1
		WorkOrderStatus.count()==2
		result.timeBasedStatus==workOrderStatusTwo
		result.timeBasedPreviousStatus==workOrderStatusOne
		result.timeBasedPreviousStatus==workOrderStatusOne
		workOrderStatusTwo.getWorkOrderStatusChange() == WorkOrderStatusChange.CLOSEDORDERFIXED
	}

	def "get escalation status an escalted workOrder"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",criticality:Criticality.NORMAL,currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		def workOrderStatus =  new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATFOSA,changedBy:user);
		workOrder.addToStatus(workOrderStatus)
		workOrder.save(failOnError:true,flush:true)
		then:
		WorkOrder.count() == 1
		WorkOrder.list()[0].status.status.equals([OrderStatus.OPENATFOSA])
		WorkOrderStatus.count() == 1
	}
}
