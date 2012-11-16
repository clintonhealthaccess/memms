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
		setupEquipment()
		def sender = newUser("sender", true,true)
		sender.userType = UserType.TITULAIREHC
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		
		def receiverFacilityOne = newUser("receiverFacilityOne", true,true)
		receiverFacilityOne.userType = UserType.TECHNICIANDH
		receiverFacilityOne.location = DataLocation.findByCode(KIVUYE)
		receiverFacilityOne.save(failOnError:true)
		
		def receiverFacilityTwo = newUser("receiverFacilityTwo", true,true)
		receiverFacilityTwo.userType = UserType.TECHNICIANDH
		receiverFacilityTwo.location = DataLocation.findByCode(KIVUYE)
		receiverFacilityTwo.save(failOnError:true)
		
		def receiverMoHOne = newUser("receiverMoHOne", true,true)
		receiverMoHOne.userType = UserType.TECHNICIANMMC
		receiverMoHOne.location = Location.findByCode(RWANDA)
		receiverMoHOne.save(failOnError:true)
		
		def receiverMoHTwo = newUser("receiverMoHTwo", true,true)
		receiverMoHTwo.userType = UserType.TECHNICIANMMC
		receiverMoHTwo.location = Location.findByCode(RWANDA)
		receiverMoHTwo.save(failOnError:true)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def userGroupOne,userGroupTwo,userGroupThree,userGroupFour

		when:
		userGroupOne = userService.getNotificationWorkOrderGroup(workOrder,sender,false)
		userGroupTwo = userService.getNotificationWorkOrderGroup(workOrder,receiverFacilityOne,false)
		userGroupThree = userService.getNotificationWorkOrderGroup(workOrder,receiverFacilityTwo,true)
		userGroupFour = userService.getNotificationWorkOrderGroup(workOrder,receiverMoHTwo,false)
		then:
		userGroupOne.size() == 2
		userGroupTwo.size() == 2
		userGroupThree.size() == 4
		userGroupFour.size() == 3
	}
	
	def "get notificationEquipment group"(){
		setup:
		setupLocationTree()
		
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		sender.userType = UserType.TITULAIREHC
		sender.save(failOnError:true)
		
		def receiverOne = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newOtherUser("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO))
		receiverTwo.userType = UserType.TECHNICIANDH
		receiverTwo.save(failOnError:true)
		
		def userGroup

		when:
		userGroup = userService.getNotificationEquipmentGroup(sender.location)
		then:
		userGroup.size() == 2
	}
	
	def "check if a user can request for equipment registration"(){
		setup:
		setupLocationTree()
		def userOne = newOtherUser("userOne", "userOne", DataLocation.findByCode(KIVUYE))
		userOne.userType = UserType.TITULAIREHC
		userOne.save(failOnError:true)
		
		def userTwo = newOtherUser("userTwo", "userTwo", DataLocation.findByCode(BUTARO))
		userTwo.userType = UserType.HOSPITALDEPARTMENT
		userTwo.save(failOnError:true)
		
		def techDh = newOtherUser("techDh", "techDh", DataLocation.findByCode(BUTARO))
		techDh.userType = UserType.TECHNICIANDH
		techDh.save(failOnError:true)
		
		def techMMC = newOtherUser("techMMC", "techMMC", DataLocation.findByCode(BUTARO))
		techMMC.userType = UserType.TECHNICIANMMC
		techMMC.save(failOnError:true)
		
		def admin = newOtherUser("admin", "admin", DataLocation.findByCode(RWANDA))
		admin.userType = UserType.ADMIN
		admin.save(failOnError:true)

		expect:
		userService.canRequestEquipmentRegistration(userOne)
		userService.canRequestEquipmentRegistration(userTwo)
		!userService.canRequestEquipmentRegistration(techDh)
		!userService.canRequestEquipmentRegistration(techMMC)
		!userService.canRequestEquipmentRegistration(admin)
	}
}
