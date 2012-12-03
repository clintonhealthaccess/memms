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

	def "can list notification none ajax"(){
		setup:
		setupLocationTree()
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(MUVUNA),UserType.TITULAIREHC)
		def receiver = newOtherUserWithType("receiver", "receiver", DataLocation.findByCode(MUSANZE),UserType.TECHNICIANDH)
		
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		notificationEquipmentService.newNotification(null, "Send for rapair, unknown",sender)
		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)

		setupSecurityManager(receiverOne)
		notificationEquipmentController = new NotificationEquipmentController()

		when:
		notificationEquipmentController.list()

		then:
		NotificationEquipment.count() == 5
		notificationEquipmentController.modelAndView.model.entities.size() == 2
	}
	
	def "can list notification with ajax"(){
		setup:
		setupLocationTree()
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(MUVUNA),UserType.TITULAIREHC)
		def receiver = newOtherUserWithType("receiver", "receiver", DataLocation.findByCode(MUSANZE),UserType.TECHNICIANDH)
		
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		notificationEquipmentService.newNotification(null, "Send for rapair, unknown",sender)
		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)

		setupSecurityManager(receiverOne)
		notificationEquipmentController = new NotificationEquipmentController()

		when:
		notificationEquipmentController.request.makeAjaxRequest()
		notificationEquipmentController.list()
		then:
		NotificationEquipment.count() == 5
		notificationEquipmentController.response.json.results[0].contains("Send for rapair, two")
		notificationEquipmentController.response.json.results[0].contains("Send for rapair, two")
		!notificationEquipmentController.response.json.results[0].contains("Send for rapair, unknown")
	}

	def "can filter notifications"(){
		setup:
		setupLocationTree()
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		notificationEquipmentService.newNotification(null, "Send for rapair, oneOld",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, oneTwo",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)

		setupSecurityManager(receiverOne)

		def notification = notificationEquipmentService.searchNotificition("oneOld",receiverOne,[:])
		notificationEquipmentService.setNotificationRead(notification)
		notificationEquipmentController = new NotificationEquipmentController()

		when:
		notificationEquipmentController.params.read = "false"
		notificationEquipmentController.params.dataLocation = DataLocation.findByCode(BUTARO)
		notificationEquipmentController.params.from = Initializer.now() - 1
		notificationEquipmentController.params.to = Initializer.now() + 1
		notificationEquipmentController.request.makeAjaxRequest()
		notificationEquipmentController.filter()

		then:
		!notificationEquipmentController.response.json.results[0].contains("Send for rapair, oneTwo")
		notificationEquipmentController.response.json.results[0].contains("Send for rapair, two")
		!notificationEquipmentController.response.json.results[0].contains("Send for rapair, oneOld")
		NotificationEquipment.count() == 6
	}

	def "cannot search notifications without using ajax"(){
		setup:
		setupLocationTree()
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, oneTwo",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)

		setupSecurityManager(receiverTwo)
		notificationEquipmentController = new NotificationEquipmentController()

		when:
		notificationEquipmentController.params.q = "one"
		notificationEquipmentController.search()

		then:
		notificationEquipmentController.response.status == 404
		NotificationEquipment.count() == 6
	}
	
	def "can search notifications"(){
		setup:
		setupLocationTree()
		def senderOne = newOtherUserWithType("senderOne", "senderOne", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)

		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo", DataLocation.findByCode(BUTARO),UserType.HOSPITALDEPARTMENT)

		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		def receiverTwo = newOtherUserWithType("receiverTwo", "receiverTwo", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

		notificationEquipmentService.newNotification(null, "Send for rapair, one",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, oneTwo",senderOne)
		notificationEquipmentService.newNotification(null, "Send for rapair, two",senderTwo)

		setupSecurityManager(receiverTwo)
		notificationEquipmentController = new NotificationEquipmentController()

		when:
		notificationEquipmentController.params.q = "one"
		notificationEquipmentController.request.makeAjaxRequest()
		notificationEquipmentController.search()

		then:
		notificationEquipmentController.response.json.results[0].contains("Send for rapair, one")
		notificationEquipmentController.response.json.results[0].contains("Send for rapair, oneTwo")
		!notificationEquipmentController.response.json.results[0].contains("Send for rapair, two")
		NotificationEquipment.count() == 6
	}
}