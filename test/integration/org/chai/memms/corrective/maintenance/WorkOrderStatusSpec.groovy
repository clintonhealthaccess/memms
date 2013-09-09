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
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;


class WorkOrderStatusSpec extends IntegrationTests{
	
	def "can create a workOrderStatus"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",addedOn:Initializer.now(),criticality:Criticality.NORMAL,currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		when:
		def workOrderStatus =  new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATFOSA,changeOn:Initializer.now(),changedBy:user);
		workOrder.addToStatus(workOrderStatus)
		workOrder.save(failOnError:true)
		then:
		WorkOrder.count() == 1
		WorkOrder.list()[0].status.status.equals([OrderStatus.OPENATFOSA])
		WorkOrderStatus.count() == 1
	}
	def "escalate only when OrderStatus is OPENATMMC"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment =  Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",addedOn:Initializer.now(),criticality:Criticality.NORMAL,currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED)
		
		when:
		def workOrderStatus =  new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATFOSA,changeOn:Initializer.now(),changedBy:user);
		workOrder.addToStatus(workOrderStatus)
		workOrder.save(failOnError:true)
		workOrderStatus.escalation =  true
		workOrderStatus.save()
		then:
		WorkOrderStatus.count() == 1
		workOrderStatus.errors.hasFieldErrors("escalation") == true
		
		
		when:
		def workOrderStatusOne =  new WorkOrderStatus(workOrder:workOrder,status:OrderStatus.OPENATMMC,changeOn:Initializer.now(),changedBy:user);
		workOrder.addToStatus(workOrderStatusOne)
		workOrder.save()
		workOrderStatusOne.escalation =  false
		workOrderStatusOne.save()
		then:
		WorkOrderStatus.count() == 1
		workOrderStatusOne.errors.hasFieldErrors("escalation") == true
	}

	def "can get current and previous equipment status"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment =  Equipment.findBySerialNumber(CODE(123))
		def workOrder =  new WorkOrder(equipment:equipment,description:"test work order",addedOn:Initializer.now(),criticality:Criticality.NORMAL,currentStatus:OrderStatus.OPENATFOSA,addedBy:user,openOn:Initializer.now(),failureReason:FailureReason.NOTSPECIFIED).save(failOnError: true)
		
		when:
		def workOrderStatusOne =  new WorkOrderStatus(workOrder:workOrder,previousStatus:workOrder.currentStatus,status:OrderStatus.OPENATFOSA,changeOn:Initializer.now(),changedBy:user)
		workOrder.addToStatus(workOrderStatusOne)
		workOrder.save()
		workOrderStatusOne.save(failOnError: true)

		def workOrderStatusTwo =  new WorkOrderStatus(workOrder:workOrder,previousStatus:workOrder.currentStatus,status:OrderStatus.CLOSEDFIXED,changeOn:Initializer.now(),changedBy:user)
		workOrder.addToStatus(workOrderStatusTwo)
		workOrder.save()
		workOrderStatusTwo.save(failOnError: true)

		def currentState = workOrder.getTimeBasedStatus()
		def previousState = workOrder.getTimeBasedPreviousStatus()

		then:
		// WorkOrderStatus.count() == 2

		workOrderStatusTwo.previousStatus.equals(workOrderStatusOne.status)
		workOrderStatusTwo.getWorkOrderStatusChange().equals(WorkOrderStatusChange.CLOSEDORDERFIXED)
		workOrderStatusTwo.getWorkOrderStatusChange([WorkOrderStatusChange.NEWORDER]).equals(null)
	}

}