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
import org.chai.memms.maintenance.WorkOrderNotificationController

class WorkOrderNotificationControllerSpec  extends IntegrationTests{
	
	def workOrderNotificationController
	def workOrderNotificationService
	
	def "can save a notification"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("sender", true,true)
		setupSecurityManager(sender)
		sender.userType = UserType.DATACLERK
		sender.location = DataLocation.findByCode(KIVUYE)
		sender.save(failOnError:true)
		def receiverOne = newUser("receiverOne", true,true)
		receiverOne.userType = UserType.TECHNICIANFACILITY
		receiverOne.location = DataLocation.findByCode(KIVUYE)
		receiverOne.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		workOrderNotificationController = new WorkOrderNotificationController()
		when:
		workOrderNotificationController.params.content = " check this out"
		workOrderNotificationController.params.workOrder = workOrder
		workOrderNotificationController.save()
		then:
		workOrderNotificationController.response.redirectUrl == "/workOrderNotification/list/${workOrder.id}"
		Notification.count() == 1
	}
	
	def "can't save a notification without a work order"(){
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

		workOrderNotificationController = new WorkOrderNotificationController()
		setupSecurityManager(sender)
		when:
		workOrderNotificationController.params.content = " check this out"
		workOrderNotificationController.save()
		then:
		workOrderNotificationController.modelAndView.viewName == "/entity/edit"
		Notification.count() == 0
	}
	
	def "reading a notification sets it's read status to read"(){
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
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
		workOrderNotificationService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		def notification = workOrderNotificationService.searchNotificition("one",receiverFacility,workOrderOne,null,[:])[0]
		workOrderNotificationController = new WorkOrderNotificationController()
		expect:
		notification.read == false
		when://Get only those that are unread
		workOrderNotificationController.params.id = notification.id
		workOrderNotificationController.read()
		
		then:
		notification.read == true
	}
	
	def "can list notification - from a work order"(){
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
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,true)
		workOrderNotificationService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		def notifications = workOrderNotificationService.searchNotificition("one",receiverFacility,workOrderOne,null,[:])
		workOrderNotificationService.setNotificationRead(notifications[0])
		workOrderNotificationController = new WorkOrderNotificationController()
		
		when://Get only those that are unread
		workOrderNotificationController.params.read = "false"
		workOrderNotificationController.params.id = workOrderTwo.id
		workOrderNotificationController.list()
		
		then:
		workOrderNotificationController.modelAndView.model.entities.size() == 1
		Notification.count() == 4
	}
	
	def "can list notification - all of them for the current user"(){
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
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderTwo,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility, false)
		workOrderNotificationService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		def notifications = workOrderNotificationService.searchNotificition("one",receiverFacility,workOrderOne,null,[:])
		workOrderNotificationService.setNotificationRead(notifications[0])
		workOrderNotificationController = new WorkOrderNotificationController()
		
		when://list all for a given user
		workOrderNotificationController = new WorkOrderNotificationController()
		workOrderNotificationController.list()
		
		then:
		workOrderNotificationController.modelAndView.model.entities.size() == 2
		Notification.count() == 3
	}
	
	def "can filter notifications"(){
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
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
		workOrderNotificationService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		workOrderNotificationController = new WorkOrderNotificationController()
		
		when://Get only those that are unread
		workOrderNotificationController.params.read = "false"
		workOrderNotificationController.params.to = Initializer.now()+1
		workOrderNotificationController.filter()
		
		then:
		//There are 4 notifications because the creator of a workorder always gets a copy
		Notification.count() == 4
		workOrderNotificationController.modelAndView.model.entities.size() == 2
	}
	
	def "can search notifications"(){
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
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		workOrderNotificationService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
		workOrderNotificationService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		workOrderNotificationController = new WorkOrderNotificationController()
		
		when://Get only those that are unread
		workOrderNotificationController.params.q = "one"
		workOrderNotificationController.search()
		
		then:
		workOrderNotificationController.modelAndView.model.entities.size() == 1
	}
}