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
package org.chai.memms.security

import java.util.Map;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User.UserType;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.Location;

/**
 * @author Jean Kahigiso M.
 *
 */
class UserServiceSpec extends IntegrationTests{
	def userService;

	def "search user test"(){
		setup:
		setupLocationTree()
		def dataLocation = DataLocation.findByCode(KIVUYE);
		def userOne = newUser("userOne",UUID.randomUUID().toString());
		def userTwo = newUser("userTwo",UUID.randomUUID().toString());
		def personUserOne = newOtherUser("personUserOne",UUID.randomUUID().toString(),dataLocation);
		def personUserTwo = newOtherUser("personUserTwo",UUID.randomUUID().toString(),dataLocation);
		def systemUserOne = newSystemUser("systemUserOne",UUID.randomUUID().toString(),dataLocation);
		def systemUserTwo = newSystemUser("systemUserTwo",UUID.randomUUID().toString(),dataLocation);



		when:
		def users = userService.searchUser("user",[:]);
		def sortUsersByFirstname = userService.searchUser("one",["sort":"firstname","order":"asc"]);
		def sortUsersByUsername = userService.searchUser("two",["sort":"username"]);
		then:
		//test default sorting
		users.equals([systemUserTwo,systemUserOne,personUserTwo,personUserOne,userTwo,userOne])
		sortUsersByFirstname.equals([personUserOne,systemUserOne,userOne])
		//test if the sorting params is taken in consideration
		sortUsersByUsername.equals([userTwo,systemUserTwo,personUserTwo])

	}

	def "count searched user test"(){
		setup:
		setupLocationTree()
		def dataLocation = DataLocation.findByCode(KIVUYE);
		def userOne = newUser("userOne",UUID.randomUUID().toString());
		def userTwo = newUser("userTwo",UUID.randomUUID().toString());
		def systemUserOne = newSystemUser("systemUserOne",UUID.randomUUID().toString(),dataLocation);
		def systemUserTwo = newSystemUser("systemUserTwo",UUID.randomUUID().toString(),dataLocation);
		def personUserOne = newOtherUser("personUserOne",UUID.randomUUID().toString(),dataLocation);
		def personUserTwo = newOtherUser("personUserTwo",UUID.randomUUID().toString(),dataLocation);

		when:
		def users = userService.searchUser("user",[:]);
		def sortUsersByFirstname = userService.searchUser("one",["sort":"firstname"]);
		then:
		users.size()==6
		sortUsersByFirstname.size()==3
	}

	def "can filter user test"(){
		setup:
		Initializer.createDummyStructure()
		Initializer.createUsers()
		Initializer.createInventoryStructure()

		when:
		def usersOne = userService.getActiveUserByTypeAndLocation(UserType.TECHNICIANDH, CalculationLocation.findByCode(Initializer.NYANZA), [:])
		def usersTwo = userService.getActiveUserByTypeAndLocation(UserType.TECHNICIANMMC, null, [:])
		then:
		usersOne.size()==1
		usersTwo.size()==1
	}
	
	def "get notificationWorkOrder group"(){
		setup:
		setupLocationTree()
		def senderTitulaire = newOtherUserWithType("senderTitulaire", "senderTitulaire",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderDepartment = newOtherUserWithType("senderDepartment", "senderDepartment",DataLocation.findByCode(KIVUYE),UserType.HOSPITALDEPARTMENT)
		
		def receiverFacilityOne = newOtherUserWithType("receiverFacilityOne", "receiverFacilityOne",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverFacilityTwo = newOtherUserWithType("receiverFacilityTwo", "receiverFacilityTwo",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverMoHOne = newOtherUserWithType("receiverMoHOne", "receiverMoHOne",Location.findByCode(RWANDA),UserType.TECHNICIANMMC)
		
		def receiverMoHTwo = newOtherUserWithType("receiverMoHTwo", "receiverMoHTwo",Location.findByCode(RWANDA),UserType.TECHNICIANMMC)
		
		def equipmentManaged = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentNotManaged = newEquipment(CODE(133),DataLocation.findByCode(BUTARO))
		
		def workOrderManaged = Initializer.newWorkOrder(equipmentManaged, "Nothing yet managed", Criticality.NORMAL,senderTitulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderNotManaged = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet not managed", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def initialGroupManaged,initialGroupNotManaged,sentByTechdh,escalated,sentByTechAtMOH

		when:
		initialGroupManaged = userService.getNotificationWorkOrderGroup(workOrderManaged,senderTitulaire,false)
		initialGroupNotManaged = userService.getNotificationWorkOrderGroup(workOrderNotManaged,senderDepartment,false)
		sentByTechdh = userService.getNotificationWorkOrderGroup(workOrderManaged,receiverFacilityOne,false)
		escalated = userService.getNotificationWorkOrderGroup(workOrderNotManaged,receiverFacilityTwo,true)
		sentByTechAtMOH = userService.getNotificationWorkOrderGroup(workOrderManaged,receiverMoHTwo,false)
		then:
		initialGroupManaged.size() == 2
		initialGroupNotManaged.size() == 2
		sentByTechdh.size() == 1
		escalated.size() == 3
		sentByTechAtMOH.size() == 3
	}
	
	def "get notificationEquipment group"(){
		setup:
		setupLocationTree()
		
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE), UserType.TITULAIREHC)
		
		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		
		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		
		def userGroup

		when:
		userGroup = userService.getNotificationEquipmentGroup(sender.location)
		then:
		userGroup.size() == 2
	}
	
	def "check if a user can request for equipment registration"(){
		setup:
		setupLocationTree()
		def userOne = newOtherUserWithType("userOne", "userOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def userTwo = newOtherUserWithType("userTwo", "userTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)
		
		def techDh = newOtherUserWithType("techDh", "techDh", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def techMMC = newOtherUserWithType("techMMC", "techMMC", DataLocation.findByCode(BUTARO),UserType.TECHNICIANMMC)
		
		def admin = newOtherUserWithType("admin", "admin", DataLocation.findByCode(RWANDA),UserType.ADMIN)

		expect:
		userService.canRequestEquipmentRegistration(userOne)
		userService.canRequestEquipmentRegistration(userTwo)
		!userService.canRequestEquipmentRegistration(techDh)
		!userService.canRequestEquipmentRegistration(techMMC)
		!userService.canRequestEquipmentRegistration(admin)
	}
	
	def "check if a user can view managed equipments"(){
		setup:
		setupLocationTree()
		def userOne = newOtherUserWithType("userOne", "userOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def userTwo = newOtherUserWithType("userTwo", "userTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)
		
		def techDh = newOtherUserWithType("techDh", "techDh", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def techMMC = newOtherUserWithType("techMMC", "techMMC", DataLocation.findByCode(BUTARO),UserType.TECHNICIANMMC)
		
		def admin = newOtherUserWithType("admin", "admin", DataLocation.findByCode(RWANDA),UserType.ADMIN)

		expect:
		!userService.canViewManagedEquipments(userOne)
		!userService.canViewManagedEquipments(userTwo)
		userService.canViewManagedEquipments(techDh)
		!userService.canViewManagedEquipments(techMMC)
		!userService.canViewManagedEquipments(admin)
	}
}
