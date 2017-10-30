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
		setupLocationTree()//newOtherUserWithType("userOne", "userOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		when:
		notificationEquipmentService.newNotification(null, "Send for rapair",sender)
		then:
		sender
		NotificationEquipment.count() == 2
	}
	
	def "reading a notification sets it's read status to true"() {
		setup:
		setupLocationTree()
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)
		
		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		
		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		
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
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE), UserType.TITULAIREHC)
		senderOne.save(failOnError:true)
		
		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)
		
		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def adminUser = newOtherUserWithType("admin", "admin", DataLocation.findByCode(BUTARO),UserType.ADMIN)
		
		
		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)
		
		def notificationEquipment = NotificationEquipment.list()
		notificationEquipmentService.setNotificationRead(notificationEquipment[0])
		notificationEquipmentService.setNotificationRead(notificationEquipment[1])
		
		when:
		def allNotifications = notificationEquipmentService.filterNotifications(null,null, adminUser,null,null,null, [:])
		def readNotifications = notificationEquipmentService.filterNotifications(null,null, receiverTwo,null,null,true, [:])
		//TODO we cannot specify the departments as of now
		//def notificationsByDepartmentOrder = notificationEquipmentService.filterNotifications(null,null, null,null,null, [:])
		def notificationsByreceiver = notificationEquipmentService.filterNotifications(null,null,receiverOne,null,null,null, [:])
		def jointFilter = notificationEquipmentService.filterNotifications(receiverOne.location,null,receiverOne,null,null,null, [:])
		def madeAfterToday = notificationEquipmentService.filterNotifications(null,null,receiverOne, Initializer.now()+1,null,null, [:])
		def madeBeforeToday = notificationEquipmentService.filterNotifications(null,null, receiverOne,null,Initializer.now()+1,null, [:])
		def madeBetweenYesterdayAndTomorrow = notificationEquipmentService.filterNotifications(null,null,receiverOne, Initializer.now()-1,Initializer.now()+1,null, [:])
		def unreadNotifications = notificationEquipmentService.filterNotifications(null,null, receiverOne,null,null,false, [:])
		then:
		allNotifications.size() == 4
		readNotifications.size() == 1
		//notificationsByDepartmentOrder.size() == 3
		notificationsByreceiver.size() == 2
		jointFilter.size() == 2
		madeAfterToday.size() == 0
		madeBeforeToday.size() == 2
		madeBetweenYesterdayAndTomorrow.size() == 2
		unreadNotifications.size() == 1
	}
	
	def "can search notifications"() {
		setup:
		setupLocationTree()
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE), UserType.TITULAIREHC)
		
		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO), UserType.HOSPITALDEPARTMENT)
		
		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		
		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)
		
		when:
		def found = notificationEquipmentService.searchNotificition("one",receiverOne,[:])
		then:
		NotificationEquipment.count() == 4
		found.size() == 1
	}
	
	def "get unread notifications count"() {
		setup:
		setupLocationTree()
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)
		
		def receiver = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)
		
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
