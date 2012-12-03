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
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.MaintenanceProcess.ProcessType;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderController;
import org.chai.memms.security.User
import org.chai.memms.security.User.UserType
import org.chai.location.DataLocation
import org.chai.location.Location

/**
 * @author Jean Kahigiso M.
 *
 */
class WorkOrderViewControllerSpec extends IntegrationTests{
	
	def workOrderViewController


	def "can deleteProcess test"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def process = Initializer.newMaintenanceProcess(workOrder, ProcessType.ACTION, "Process", Initializer.now(),user)
		workOrder.addToProcesses(process)
		workOrder.save(flush:true)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params.'process.id' = process.id
		workOrderViewController.removeProcess()
		then:
		workOrder.actions.size() == 0
	}
	
	def "can andProcess test"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params.'order.id' = workOrder.id
		workOrderViewController.params.type = "action"
		workOrderViewController.params."value" = "text action"
		workOrderViewController.addProcess()
		then:
		workOrder.actions.size() == 1
	}
	
	def "can andComment test"(){
		setup:
		setupLocationTree()
		setupEquipment()
		setupSystemUser()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params.'order.id' = workOrder.id
		workOrderViewController.params.content = "comment content text"
		workOrderViewController.addComment()
		then:
		workOrder.comments.size() == 1
	}
	
	def "can deleteComment test"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user","user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def comment = Initializer.newComment(workOrder,user, Initializer.now(), "Test comment")
		workOrder.addToComments(comment)
		workOrder.save(flush:true)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params.'comment.id' = comment.id
		workOrderViewController.removeComment()
		then:
		workOrder.comments.size() == 0
	}
	
	
	def "can list workOrders - only those that a user at a dataLocation can access"(){
		setup:
		setupLocationTree()
		
		def techdh = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		techdh.userType = UserType.TECHNICIANDH
		techdh.save(failOnError:true)
		
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		sender.userType = UserType.TITULAIREHC
		sender.save(failOnError:true)
		
		def equipmentOne = newEquipment(CODE(123),DataLocation.findByCode(BUTARO))
		def equipmentTwo = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		def workOrderOne = Initializer.newWorkOrder(equipmentOne, "Nothing yet", Criticality.NORMAL,techdh,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrder = Initializer.newWorkOrder(equipmentTwo, "Nothing yet, not even after escalations", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		setupSecurityManager( techdh)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params."dataLocation.id" = techdh.location.id
		workOrderViewController.list()
		then:
		WorkOrder.count() == 2
		workOrderViewController.modelAndView.model.entities.size() == 2
	}
	
	def "cannot list workOrders - only those that a user at a dataLocation cannot access"(){
		setup:
		setupLocationTree()
		
		def techdh = newOtherUser("techdh", "techdh", DataLocation.findByCode(MUSANZE))
		techdh.userType = UserType.TECHNICIANDH
		techdh.save(failOnError:true)
		
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		sender.userType = UserType.TITULAIREHC
		sender.save(failOnError:true)
		
		def equipmentOne = newEquipment(CODE(123),DataLocation.findByCode(BUTARO))
		def equipmentTwo = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		def workOrderOne = Initializer.newWorkOrder(equipmentOne, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrder = Initializer.newWorkOrder(equipmentTwo, "Nothing yet, not even after escalations", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		setupSecurityManager( techdh)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params."dataLocation.id" = techdh.location.id
		workOrderViewController.list()
		then:
		WorkOrder.count() == 2
		workOrderViewController.modelAndView.model.entities.size() == 0
	}
	def "can list workOrders - by admin"(){
		setup:
		setupLocationTree()
		
		def admin = newOtherUserWithType("admin", "admin", Location.findByCode(RWANDA),UserType.ADMIN)
		
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def equipmentOne = newEquipment(CODE(123),DataLocation.findByCode(BUTARO))
		def equipmentTwo = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		def workOrderOne = Initializer.newWorkOrder(equipmentOne, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrder = Initializer.newWorkOrder(equipmentTwo, "Nothing yet, not even after escalations", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		setupSecurityManager( admin)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params."dataLocation.id" = DataLocation.findByCode(BUTARO).id
		workOrderViewController.list()
		then:
		WorkOrder.count() == 2
		workOrderViewController.modelAndView.model.entities.size() == 2
	}
	
	def "can list workOrders - by admin using ajax"(){
		setup:
		setupLocationTree()
		
		def admin = newOtherUserWithType("admin", "admin", Location.findByCode(RWANDA),UserType.ADMIN)
		
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def equipmentOne = newEquipment(CODE(123),DataLocation.findByCode(BUTARO))
		def equipmentTwo = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		def workOrderOne = Initializer.newWorkOrder(equipmentOne, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrder = Initializer.newWorkOrder(equipmentTwo, "Nothing yet, not even after escalations", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		setupSecurityManager( admin)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params."dataLocation.id" = DataLocation.findByCode(BUTARO).id
		workOrderViewController.request.makeAjaxRequest()
		workOrderViewController.list()
		then:
		WorkOrder.count() == 2
		workOrderViewController.response.json.results[0].contains("Nothing yet")
		workOrderViewController.response.json.results[0].contains("Nothing yet, not even after escalations")
	}
	
	def "can search notifications workorder"(){
		setup:
		setupLocationTree()
		
		def admin = newOtherUserWithType("admin", "admin", Location.findByCode(RWANDA),UserType.ADMIN)
		
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def equipmentOne = newEquipment(CODE(123),DataLocation.findByCode(BUTARO))
		def equipmentTwo = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		def workOrderOne = Initializer.newWorkOrder(equipmentOne, "Nothing yet, before", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrder = Initializer.newWorkOrder(equipmentTwo, "Nothing yet, not even after escalations", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		setupSecurityManager( admin)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params."q" = "escalations"
		workOrderViewController.request.makeAjaxRequest()
		workOrderViewController.search()
		then:
		WorkOrder.count() == 2
		!workOrderViewController.response.json.results[0].contains("before")
		workOrderViewController.response.json.results[0].contains("Nothing yet, not even after escalations")
	}
	
	def "cannot search notifications workorder without using ajax"(){
		setup:
		setupLocationTree()
		
		def admin = newOtherUserWithType("admin", "admin", Location.findByCode(RWANDA),UserType.ADMIN)
		
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def equipmentOne = newEquipment(CODE(123),DataLocation.findByCode(BUTARO))
		def equipmentTwo = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		def workOrderOne = Initializer.newWorkOrder(equipmentOne, "Nothing yet, before", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrder = Initializer.newWorkOrder(equipmentTwo, "Nothing yet, not even after escalations", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		setupSecurityManager( admin)
		workOrderViewController = new WorkOrderViewController()
		when:
		workOrderViewController.params."q" = "escalations"
		workOrderViewController.search()
		then:
		WorkOrder.count() == 2
		workOrderViewController.response.status == 404
	}
}
