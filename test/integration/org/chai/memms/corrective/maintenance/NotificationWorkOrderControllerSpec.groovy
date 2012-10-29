package org.chai.memms.corrective.maintenance

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

class NotificationWorkOrderControllerSpec  extends IntegrationTests{
	
	def notificationWorkOrderController
	def notificationWorkOrderService
	
	def "can save a notification"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		setupSecurityManager(sender)
		sender.userType = UserType.TITULAIREHC
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		def receiverOne = newUser("receiverOne", true,true)
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.location = DataLocation.findByCode(KIVUYE)
		receiverOne.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		notificationWorkOrderController = new NotificationWorkOrderController()
		when:
		notificationWorkOrderController.params.content = " check this out"
		notificationWorkOrderController.params.workOrder = workOrder
		notificationWorkOrderController.save()
		then:
		notificationWorkOrderController.response.redirectUrl == "/notificationWorkOrder/list/${workOrder.id}"
		Notification.count() == 1
	}
	
	def "can't save a notification without a work order"(){
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

		notificationWorkOrderController = new NotificationWorkOrderController()
		setupSecurityManager(sender)
		when:
		notificationWorkOrderController.params.content = " check this out"
		notificationWorkOrderController.save()
		then:
		notificationWorkOrderController.modelAndView.viewName == "/entity/edit"
		Notification.count() == 0
	}
	
	def "reading a notification sets it's read status to read"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.TITULAIREHC
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.TITULAIREHC
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANDH
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		def notification = notificationWorkOrderService.searchNotificition("one",receiverFacility,workOrderOne,null,[:])[0]
		notificationWorkOrderController = new NotificationWorkOrderController()
		expect:
		notification.read == false
		when://Get only those that are unread
		notificationWorkOrderController.params.id = notification.id
		notificationWorkOrderController.read()
		
		then:
		notification.read == true
	}
	
	def "can list notification - from a work order"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.TITULAIREHC
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.TITULAIREHC
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANDH
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,true)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		def notifications = notificationWorkOrderService.searchNotificition("one",receiverFacility,workOrderOne,null,[:])
		notificationWorkOrderService.setNotificationRead(notifications[0])
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://Get only those that are unread
		notificationWorkOrderController.params.read = "false"
		notificationWorkOrderController.params.id = workOrderTwo.id
		notificationWorkOrderController.list()
		
		then:
		notificationWorkOrderController.modelAndView.model.entities.size() == 1
		Notification.count() == 3
	}
	
	def "can list notification - all of them for the current user"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.TITULAIREHC
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.TITULAIREHC
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANDH
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility, false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		def notifications = notificationWorkOrderService.searchNotificition("one",receiverFacility,workOrderOne,null,[:])
		notificationWorkOrderService.setNotificationRead(notifications[0])
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://list all for a given user
		notificationWorkOrderController = new NotificationWorkOrderController()
		notificationWorkOrderController.list()
		
		then:
		notificationWorkOrderController.modelAndView.model.entities.size() == 2
		Notification.count() == 3
	}
	
	def "can filter notifications"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.TITULAIREHC
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.TITULAIREHC
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANDH
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://Get only those that are unread
		notificationWorkOrderController.params.read = "false"
		notificationWorkOrderController.params.to = Initializer.now()+1
		notificationWorkOrderController.filter()
		
		then:
		//There are 4 notifications because the creator of a workorder always gets a copy
		Notification.count() == 4
		notificationWorkOrderController.modelAndView.model.entities.size() == 2
	}
	
	def "can search notifications"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newUser("senderOne", true,true)
		senderOne.userType = UserType.TITULAIREHC
		senderOne.location = DataLocation.findByCode(KIVUYE)
		senderOne.save(failOnError:true)
		
		def senderTwo = newUser("senderTwo", true,true)
		senderTwo.userType = UserType.TITULAIREHC
		senderTwo.location = DataLocation.findByCode(KIVUYE)
		senderTwo.save(failOnError:true)
		
		def receiverFacility = newUser("receiverFacility", true,true)
		receiverFacility.userType = UserType.TECHNICIANDH
		receiverFacility.location = DataLocation.findByCode(KIVUYE)
		receiverFacility.save(failOnError:true)
		
		def receiverMoH = newUser("receiverMoH", true,true)
		receiverMoH.userType = UserType.TECHNICIANDH
		receiverMoH.location = Location.findByCode(RWANDA)
		receiverMoH.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://Get only those that are unread
		notificationWorkOrderController.params.q = "one"
		notificationWorkOrderController.search()
		
		then:
		notificationWorkOrderController.modelAndView.model.entities.size() == 1
	}
}