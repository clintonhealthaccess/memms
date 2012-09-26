package org.chai.memms.maintenance

import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.equipment.Equipment;
import org.chai.memms.maintenance.WorkOrder.Criticality;
import org.chai.memms.maintenance.WorkOrder.OrderStatus;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;

class NotificationServiceSpec  extends IntegrationTests{
	def notificationService
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
		
		def workOrder = Initializer.newWorkOrder(Equipment.findBySerialNumber(CODE(123)), "Nothing yet", Criticality.NORMAL, OrderStatus.OPEN, sender, new Date())
		when:
		notificationService.newNotification(workOrder, "Send for rapair",sender)
		then:
		Notification.count() == 2
		workOrder.notificationGroup.size() == 3
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
		
		def workOrder = Initializer.newWorkOrder(Equipment.findBySerialNumber(CODE(123)), "Nothing yet", Criticality.NORMAL, OrderStatus.OPEN, sender, new Date())
		when:
		notificationService.newNotification(workOrder, "Send for rapair",sender)
		notificationService.newNotification(workOrder, "Send for rapair, higher",receiverFacilityOne)
		then:
		Notification.count() == 6
		workOrder.notificationGroup.size() == 5
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
		
		def workOrder = Initializer.newWorkOrder(Equipment.findBySerialNumber(CODE(123)), "Nothing yet", Criticality.NORMAL, OrderStatus.OPEN, sender, new Date())
		notificationService.newNotification(workOrder, "Send for rapair",sender)
		def notificationToRead = Notification.list().first()
		when:
		notificationService.readNotification(notificationToRead.id)
		then:
		Notification.count() == 2
		Notification.get(notificationToRead.id).read
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
		
		
		def workOrderOne = Initializer.newWorkOrder(Equipment.findBySerialNumber(CODE(123)), "Nothing yet", Criticality.NORMAL, OrderStatus.OPEN, senderOne, new Date())
		def workOrderTwo = Initializer.newWorkOrder(Equipment.findBySerialNumber(CODE(123)), "Nothing yet", Criticality.NORMAL, OrderStatus.OPEN, senderTwo, new Date())
		
		notificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne)
		notificationService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility)
		notificationService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo)
		def notifications = Notification.list()
		notificationService.readNotification(notifications[0].id)
		notificationService.readNotification(notifications[1].id)
		when://getNotifications(WorkOrder workOrder,User sender, User receiver,Boolean read, Map<String, String> params)
		def allNotifications = notificationService.filterNotifications(null,null, null,null, [:])
		def readNotifications = notificationService.filterNotifications(null,null, null,true, [:])
		def notificationsByWorkOrder = notificationService.filterNotifications(workOrderOne,null, null,null, [:])
		def notificationsBySender = notificationService.filterNotifications(null,receiverFacility, null,null, [:])
		def notificationsByreceiver = notificationService.filterNotifications(null,null, receiverMoH,null, [:])
		def jointFilter = notificationService.filterNotifications(workOrderOne,senderOne, receiverFacility,true, [:])
		then:
		allNotifications.size() == 4
		readNotifications.size() == 2
		notificationsByWorkOrder.size() == 3
		notificationsBySender.size() == 2
		notificationsByreceiver.size() == 1
		jointFilter.size() == 1
	}
}
