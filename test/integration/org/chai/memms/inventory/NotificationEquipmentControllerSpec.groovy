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
		
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		sender.userType = UserType.TITULAIREHC
		sender.save(failOnError:true)
		
		def receiverOne = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.save(failOnError:true)
		
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
	
	def "can filter notifications"(){
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
		
		setupSecurityManager(receiverTwo)
		
		notificationEquipmentController = new NotificationEquipmentController()
		
		when:
		notificationEquipmentController.params.read = "false"
		notificationEquipmentController.params.to = Initializer.now()+1
		notificationEquipmentController.filter()
		
		then:
		NotificationEquipment.count() == 4
		notificationEquipmentController.modelAndView.model.entities.size() == 2
	}
	
	def "can search notifications"(){
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
		
		setupSecurityManager(receiverTwo)
		notificationEquipmentController = new NotificationEquipmentController()
		
		when:
		notificationEquipmentController.params.q = "one"
		notificationEquipmentController.search()
		
		then:
		notificationEquipmentController.modelAndView.model.entities.size() == 1
	}
}