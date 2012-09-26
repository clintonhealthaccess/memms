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
import org.chai.memms.maintenance.WorkOrder.Criticality;
import org.chai.memms.maintenance.WorkOrder.OrderStatus;
import org.chai.memms.security.User;


class WorkOrderSpec extends IntegrationTests{
	def "can create a workOrder"(){
		setup:
		setupLocationTree()
		setupEquipment()
		newUser("user", "user")
		when:
		new WorkOrder(equipment:Equipment.findBySerialNumber(CODE(123)),description: "test work order",criticality:Criticality.NORMAL,status:OrderStatus.OPEN,
			addedBy:User.findByUsername("user"),openOn: new Date(),assistaceRequested:false).save(failOnError:true)
		then:
		WorkOrder.count() == 1
	}
	
	def "retrieve notifications for a user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		def senderTwo = newUser("senderTwo", true,true)
		def receiver = newUser("receiver", true,true)
		def workOrder = Initializer.newWorkOrder(Equipment.findBySerialNumber(CODE(123)), "Nothing yet", Criticality.NORMAL, OrderStatus.OPEN, User.findByUsername("senderOne"), new Date())
		when:
		workOrder.addToNotifications(Initializer.newNotification(workOrder, senderOne, receiver,new Date(), "test one"))
		workOrder.addToNotifications(Initializer.newNotification(workOrder, senderTwo, receiver,new Date(), "test one"))
		workOrder.save(failOnError:true)
		then:
		workOrder.notifications.size() == 2
		workOrder.getNotificationsForUser(senderOne).size() == 1
	}
	
	def "retrieve unread notifications for a user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		def receiver = newUser("receiver", true,true)
		def workOrder = Initializer.newWorkOrder(Equipment.findBySerialNumber(CODE(123)), "Nothing yet", Criticality.NORMAL, OrderStatus.OPEN, User.findByUsername("sender"), new Date())
		when:
		workOrder.notifications = [
			Initializer.newNotification(workOrder, sender, receiver,new Date(), "test one"),
			Initializer.newNotification(workOrder, sender, receiver,new Date(), "test one")
		]
		workOrder.save(failOnError:true)
		then:
		workOrder.notifications.size() == 2
		workOrder.getUnReadNotificationsForUser(sender).size() == 2
	}
}
