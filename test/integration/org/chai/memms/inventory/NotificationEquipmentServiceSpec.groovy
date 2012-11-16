package org.chai.memms.inventory

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

class NotificationEquipmentServiceSpec  extends IntegrationTests{
	
	def notificationEquipmentService
	
	def "can send notifications"() {
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
		
		when:
		notificationEquipmentService.newNotification(null, "Send for rapair",sender)
		then:
		sender
		NotificationEquipment.count() == 2
	}
	
	def "reading a notification sets it's read status to true"() {
		setup:
		setupLocationTree()
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(BUTARO))
		sender.userType = UserType.HOSPITALDEPARTMENT
		sender.save(failOnError:true)
		
		def receiverOne = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newOtherUser("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO))
		receiverTwo.userType = UserType.TECHNICIANDH
		receiverTwo.save(failOnError:true)
		
		notificationEquipmentService.newNotification(null, "Send for rapair",sender)
		def notificationToRead = NotificationEquipment.list()[0]
		when:
		notificationEquipmentService.setNotificationRead(notificationToRead)
		then:
		NotificationEquipment.count() == 2
		NotificationEquipment.get(notificationToRead.id).read == true
	}
	def "can filter notifications"() {
		setup:
		setupLocationTree()
		def senderOne = newOtherUser("senderOne", "senderOne", DataLocation.findByCode(KIVUYE))
		senderOne.userType = UserType.TITULAIREHC
		senderOne.save(failOnError:true)
		
		def senderTwo = newOtherUser("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO))
		senderTwo.userType = UserType.HOSPITALDEPARTMENT
		senderTwo.save(failOnError:true)
		
		def receiverOne = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newOtherUser("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO))
		receiverTwo.userType = UserType.TECHNICIANDH
		receiverTwo.save(failOnError:true)
		
		
		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)
		
		def notificationEquipment = NotificationEquipment.list()
		notificationEquipmentService.setNotificationRead(notificationEquipment[0])
		notificationEquipmentService.setNotificationRead(notificationEquipment[1])
		
		when:
		def allNotifications = notificationEquipmentService.filterNotifications(null,null, null,null,null,null, [:])
		def readNotifications = notificationEquipmentService.filterNotifications(null,null, null,null,null,true, [:])
		//TODO we cannot specify the departments as of now
		//def notificationsByDepartmentOrder = notificationEquipmentService.filterNotifications(null,null, null,null,null, [:])
		def notificationsByreceiver = notificationEquipmentService.filterNotifications(null,null,receiverOne,null,null,null, [:])
		def jointFilter = notificationEquipmentService.filterNotifications(receiverOne.location,null,receiverOne,null,null,null, [:])
		def madeAfterToday = notificationEquipmentService.filterNotifications(null,null,null, Initializer.now()+1,null,null, [:])
		def madeBeforeToday = notificationEquipmentService.filterNotifications(null,null, null,null,Initializer.now()+1,null, [:])
		def madeBetweenYesterdayAndTomorrow = notificationEquipmentService.filterNotifications(null,null,null, Initializer.now()-1,Initializer.now()+1,null, [:])
		def unreadNotifications = notificationEquipmentService.filterNotifications(null,null, null,null,null,false, [:])
		then:
		allNotifications.size() == 4
		readNotifications.size() == 2
		//notificationsByDepartmentOrder.size() == 3
		notificationsByreceiver.size() == 2
		jointFilter.size() == 1
		madeAfterToday.size() == 0
		madeBeforeToday.size() == 4
		madeBetweenYesterdayAndTomorrow.size() == 4
		unreadNotifications.size() == 2
	}
	
	def "can search notifications"() {
		setup:
		setupLocationTree()
		def senderOne = newOtherUser("senderOne", "senderOne", DataLocation.findByCode(KIVUYE))
		senderOne.userType = UserType.TITULAIREHC
		senderOne.save(failOnError:true)
		
		def senderTwo = newOtherUser("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO))
		senderTwo.userType = UserType.HOSPITALDEPARTMENT
		senderTwo.save(failOnError:true)
		
		def receiverOne = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.save(failOnError:true)
		
		def receiverTwo = newOtherUser("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO))
		receiverTwo.userType = UserType.TECHNICIANDH
		receiverTwo.save(failOnError:true)
		
		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)
		
		when:
		def found = notificationEquipmentService.searchNotificition("one",null,[:])
		then:
		NotificationEquipment.count() == 4
		found.size() == 2
	}
	
	def "get unread notifications count"() {
		setup:
		setupLocationTree()
		def senderOne = newOtherUser("senderOne", "senderOne", DataLocation.findByCode(KIVUYE))
		senderOne.userType = UserType.TITULAIREHC
		senderOne.save(failOnError:true)
		
		def senderTwo = newOtherUser("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO))
		senderTwo.userType = UserType.HOSPITALDEPARTMENT
		senderTwo.save(failOnError:true)
		
		def receiver = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiver.userType = UserType.TECHNICIANDH
		receiver.save(failOnError:true)
		
		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, follow up",senderTwo)
		def unreadNotifications = 0
		
		when:
		unreadNotifications = notificationEquipmentService.getUnreadNotifications(receiver)
		then:
		NotificationEquipment.count() == 2
		unreadNotifications == 2
	}
}
