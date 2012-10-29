package org.chai.memms.corrective.maintenance

import java.util.Date;
import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.Notification;
import org.chai.memms.corrective.maintenance.NotificationWorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;

class NotificationWorkOrderServiceSpec  extends IntegrationTests{
	
	def notificationWorkOrderService
	
	def "can send notifications"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		sender.userType = UserType.TITULAIREHC
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		
		def receiverOne = newUser("receiverOne", true,true)
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.location = DataLocation.findByCode(KIVUYE)
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newUser("receiverTwo", true,true)
		receiverTwo.userType = UserType.TECHNICIANDH
		receiverTwo.location = DataLocation.findByCode(KIVUYE)
		receiverTwo.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED, OrderStatus.OPENATFOSA)
		when:
		notificationWorkOrderService.newNotification(workOrder, "Send for rapair",sender,false)
		then:
		Notification.count() == 2
	}
	
	def "can escalate notifications"() {
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
		notificationWorkOrderService.newNotification(workOrder, "Send for rapair",sender,false)
		when:
		notificationWorkOrderService.newNotification(workOrder, "Send for rapair, higher",receiverFacilityOne,true)
		then:
		Notification.count() == 6
	}
	
	def "reading a notification sets it's read status to true"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		sender.userType = UserType.HOSPITALDEPARTMENT
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		
		def receiverOne = newUser("receiverOne", true,true)
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.location = DataLocation.findByCode(KIVUYE)
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newUser("receiverTwo", true,true)
		receiverTwo.userType = UserType.TECHNICIANDH
		receiverTwo.location = DataLocation.findByCode(KIVUYE)
		receiverTwo.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		notificationWorkOrderService.newNotification(workOrder, "Send for rapair",sender,false)
		def notificationToRead = Notification.list()[0]
		when:
		notificationWorkOrderService.setNotificationRead(notificationToRead)
		then:
		NotificationWorkOrder.count() == 2
		NotificationWorkOrder.get(notificationToRead.id).read == true
	}
	def "can filter notifications"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.HOSPITALDEPARTMENT
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.HOSPITALDEPARTMENT
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANMMC
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,true)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		
		def workOrderNotifications = NotificationWorkOrder.list()
		notificationWorkOrderService.setNotificationRead(workOrderNotifications[0])
		notificationWorkOrderService.setNotificationRead(workOrderNotifications[1])
		when://(WorkOrder workOrder,User receiver,Date from, Date to,Boolean read, Map<String, String> params)
		def allNotifications = notificationWorkOrderService.filterNotifications(null,null, null,null,null, [:])
		def readNotifications = notificationWorkOrderService.filterNotifications(null,null, null,null,true, [:])
		def notificationsByWorkOrder = notificationWorkOrderService.filterNotifications(workOrderOne,null, null,null,null, [:])
		def notificationsByreceiver = notificationWorkOrderService.filterNotifications(null,receiverMoH,null,null,null, [:])
		def jointFilter = notificationWorkOrderService.filterNotifications(workOrderOne,receiverFacility,null,null,true, [:])
		def madeAfterToday = notificationWorkOrderService.filterNotifications(null,null, Initializer.now()+1,null,null, [:])
		def madeBeforeToday = notificationWorkOrderService.filterNotifications(null,null, null,Initializer.now()+1,null, [:])
		def madeBetweenYesterdayAndTomorrow = notificationWorkOrderService.filterNotifications(null,null, Initializer.now()-1,Initializer.now()+1,null, [:])
		def unreadNotifications = notificationWorkOrderService.filterNotifications(null,null, null,null,false, [:])
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
		senderOne.userType = UserType.HOSPITALDEPARTMENT
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.HOSPITALDEPARTMENT
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANMMC
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, follow up",senderOne,false)
		
		when:
		def found = notificationWorkOrderService.searchNotificition("one",receiverFacility,workOrderOne, null,[:])
		then:
		NotificationWorkOrder.count() == 2
		found.size() == 1
	}
	
	def "get unread notifications count"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.HOSPITALDEPARTMENT
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.HOSPITALDEPARTMENT
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, follow up",senderTwo,false)
		def unreadNotifications = 0
		
		when:
		unreadNotifications = notificationWorkOrderService.getUnreadNotifications(receiverFacility)
		then:
		NotificationWorkOrder.count() == 2
		unreadNotifications == 2
	}
}
