package org.chai.memms.maintenance

import java.util.Date;
import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.equipment.Equipment;
import org.chai.memms.maintenance.WorkOrder.Criticality;
import org.chai.memms.maintenance.WorkOrder.FailureReason;
import org.chai.memms.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;

class WorkOrderNotificationServiceSpec  extends IntegrationTests{
	def workOrderNotificationService
	
	def "can send notifications"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		sender.userType = UserType.DATACLERK
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		
		def receiverOne = newUser("receiverOne", true,true)
		receiverOne.userType = UserType.TECHNICIANFACILITY
		receiverOne.location = DataLocation.findByCode(KIVUYE)
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newUser("receiverTwo", true,true)
		receiverTwo.userType = UserType.TECHNICIANFACILITY
		receiverTwo.location = DataLocation.findByCode(KIVUYE)
		receiverTwo.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED, OrderStatus.OPENATFOSA)
		when:
		workOrderNotificationService.newNotification(workOrder, "Send for rapair",sender,false)
		then:
		Notification.count() == 2
	}
	
	def "can escalate notifications"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		sender.userType = UserType.DATACLERK
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		
		def receiverFacilityOne = newUser("receiverFacilityOne", true,true)
		receiverFacilityOne.userType = UserType.TECHNICIANFACILITY
		receiverFacilityOne.location = DataLocation.findByCode(KIVUYE)
		receiverFacilityOne.save(failOnError:true)
		
		def receiverFacilityTwo = newUser("receiverFacilityTwo", true,true)
		receiverFacilityTwo.userType = UserType.TECHNICIANFACILITY
		receiverFacilityTwo.location = DataLocation.findByCode(KIVUYE)
		receiverFacilityTwo.save(failOnError:true)
		
		def receiverMoHOne = newUser("receiverMoHOne", true,true)
		receiverMoHOne.userType = UserType.TECHNICIANMOH
		receiverMoHOne.location = Location.findByCode(RWANDA)
		receiverMoHOne.save(failOnError:true)
		
		def receiverMoHTwo = newUser("receiverMoHTwo", true,true)
		receiverMoHTwo.userType = UserType.TECHNICIANMOH
		receiverMoHTwo.location = Location.findByCode(RWANDA)
		receiverMoHTwo.save(failOnError:true)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		workOrderNotificationService.newNotification(workOrder, "Send for rapair",sender,false)
		when:
		workOrderNotificationService.newNotification(workOrder, "Send for rapair, higher",receiverFacilityOne,true)
		then:
		Notification.count() == 6
	}
	
	def "reading a notification sets it's read status to true"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		sender.userType = UserType.DATACLERK
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		
		def receiverOne = newUser("receiverOne", true,true)
		receiverOne.userType = UserType.TECHNICIANFACILITY
		receiverOne.location = DataLocation.findByCode(KIVUYE)
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newUser("receiverTwo", true,true)
		receiverTwo.userType = UserType.TECHNICIANFACILITY
		receiverTwo.location = DataLocation.findByCode(KIVUYE)
		receiverTwo.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		workOrderNotificationService.newNotification(workOrder, "Send for rapair",sender,false)
		def notificationToRead = Notification.list()[0]
		when:
		workOrderNotificationService.setNotificationRead(notificationToRead)
		then:
		WorkOrderNotification.count() == 2
		WorkOrderNotification.get(notificationToRead.id).read == true
	}
	def "can filter notifications"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.DATACLERK
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.DATACLERK
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANFACILITY
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANMOH
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,true)
		workOrderNotificationService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		
		def workOrderNotifications = WorkOrderNotification.list()
		workOrderNotificationService.setNotificationRead(workOrderNotifications[0])
		workOrderNotificationService.setNotificationRead(workOrderNotifications[1])
		when://(WorkOrder workOrder,User receiver,Date from, Date to,Boolean read, Map<String, String> params)
		def allNotifications = workOrderNotificationService.filterNotifications(null,null, null,null,null, [:])
		def readNotifications = workOrderNotificationService.filterNotifications(null,null, null,null,true, [:])
		def notificationsByWorkOrder = workOrderNotificationService.filterNotifications(workOrderOne,null, null,null,null, [:])
		def notificationsByreceiver = workOrderNotificationService.filterNotifications(null,receiverMoH,null,null,null, [:])
		def jointFilter = workOrderNotificationService.filterNotifications(workOrderOne,receiverFacility,null,null,true, [:])
		def madeAfterToday = workOrderNotificationService.filterNotifications(null,null, Initializer.now()+1,null,null, [:])
		def madeBeforeToday = workOrderNotificationService.filterNotifications(null,null, null,Initializer.now()+1,null, [:])
		def madeBetweenYesterdayAndTomorrow = workOrderNotificationService.filterNotifications(null,null, Initializer.now()-1,Initializer.now()+1,null, [:])
		def unreadNotifications = workOrderNotificationService.filterNotifications(null,null, null,null,false, [:])
		then:
		allNotifications.size() == 4
		readNotifications.size() == 2
		notificationsByWorkOrder.size() == 3
		notificationsByreceiver.size() == 1
		jointFilter.size() == 1
		madeAfterToday.size() == 0
		madeBeforeToday.size() == 4
		madeBetweenYesterdayAndTomorrow.size() == 4
		unreadNotifications.size() == 2
	}
	
	def "can search notifications"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.DATACLERK
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.DATACLERK
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANFACILITY
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANMOH
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, follow up",senderOne,false)
		
		when:
		def found = workOrderNotificationService.searchNotificition("one",receiverFacility,workOrderOne, null,[:])
		then:
		WorkOrderNotification.count() == 2
		found.size() == 1
	}
	
	def "get unread notifications count"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.DATACLERK
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.DATACLERK
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANFACILITY
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		workOrderNotificationService.newNotification(workOrderTwo, "Send for rapair, follow up",senderTwo,false)
		def unreadNotifications = 0
		
		when:
		unreadNotifications = workOrderNotificationService.getUnreadNotifications(receiverFacility)
		then:
		WorkOrderNotification.count() == 2
		unreadNotifications == 2
	}
}
