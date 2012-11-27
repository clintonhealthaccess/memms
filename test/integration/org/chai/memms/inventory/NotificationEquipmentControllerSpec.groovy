package org.chai.memms.inventory

import java.util.Date;
import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.Notification;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.corrective.maintenance.NotificationWorkOrderController;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;

class NotificationEquipmentControllerSpec  extends IntegrationTests{

	def notificationEquipmentController
	def notificationEquipmentService

	def "can save a notification"(){
		setup:
		setupLocationTree()

		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		setupSecurityManager(sender)

		notificationEquipmentController = new NotificationEquipmentController()
		when:
		notificationEquipmentController.params.content = "check this out"
		notificationEquipmentController.save()
		then:
		notificationEquipmentController.response.redirectUrl == "/notificationEquipment/list"
		NotificationEquipment.count() == 1
	}

	def "reading a notification sets it's read status to true"(){
		setup:
		setupLocationTree()
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO), UserType.TECHNICIANDH)

		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)

		setupSecurityManager(receiverOne)

		def notification = notificationEquipmentService.searchNotificition("one",receiverOne,[:])[0]
		notificationEquipmentController = new NotificationEquipmentController()

		when:
		notificationEquipmentController.params.id = notification.id
		notificationEquipmentController.read()

		then:
		notification.read == true
	}

	def "can list notification"(){
		setup:
		setupLocationTree()
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)

		setupSecurityManager(receiverOne)

		def notification = notificationEquipmentService.searchNotificition("one",receiverOne,[:])
		notificationEquipmentService.setNotificationRead(notification)
		notificationEquipmentController = new NotificationEquipmentController()

		when:
		notificationEquipmentController.list()

		then:
		notificationEquipmentController.modelAndView.model.entities.size() == 2
		NotificationEquipment.count() == 4
	}

	//TODO find a way to parse returned json to check the content
//	def "can filter notifications"(){
//		setup:
//		setupLocationTree()
//		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
//
//		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)
//
//		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
//
//		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
//
//		notificationEquipmentService.newNotification(null, "Send for rapair, oneOld",senderOne)
//		notificationEquipmentService.newNotification(null, "Send for rapair, oneTwo",senderOne)
//		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)
//
//		setupSecurityManager(receiverOne)
//
//		def notification = notificationEquipmentService.searchNotificition("oneOld",receiverOne,[:])
//		notificationEquipmentService.setNotificationRead(notification)
//		notificationEquipmentController = new NotificationEquipmentController()
//
//		when:
//		notificationEquipmentController.params.read = "false"
//		notificationEquipmentController.params.dataLocation = DataLocation.findByCode(BUTARO)
//		notificationEquipmentController.params.from = Initializer.now() - 1
//		notificationEquipmentController.params.to = Initializer.now() + 1
//		notificationEquipmentController.request.makeAjaxRequest()
//		notificationEquipmentController.filter()
//
//		then:
//		notificationEquipmentController.response.json.results == notificationEquipmentController.ajaxModel(notificationEquipmentService.filterNotifications(DataLocation.findByCode(BUTARO),null, receiverOne, Initializer.now() - 1,Initializer.now() + 1,false, [:]),"")
//		NotificationEquipment.count() == 6
//	}

//	def "can search notifications"(){
//		setup:
//		setupLocationTree()
//		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
//
//		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)
//
//		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
//
//		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
//
//		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
//		notificationEquipmentService.newNotification(null, "Send for rapair, oneTwo",senderOne)
//		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)
//
//		setupSecurityManager(receiverTwo)
//		notificationEquipmentController = new NotificationEquipmentController()
//
//		when:
//		notificationEquipmentController.params.q = "one"
//		notificationEquipmentController.request.makeAjaxRequest()
//		notificationEquipmentController.search()
//
//		then:
//		notificationEquipmentController.modelAndView.model.entities.size() == 1
//		NotificationEquipment.count() == 6
//	}
}