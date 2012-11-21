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

class NotificationWorkOrderControllerSpec extends IntegrationTests{
	
	def notificationWorkOrderController
	def notificationWorkOrderService
	
	def "can save a notification"(){
		setup:
		setupLocationTree()
		
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		sender.userType = UserType.TITULAIREHC
		sender.save(failOnError:true)
		
		def receiverOne = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiverOne.userType = UserType.TECHNICIANDH
		receiverOne.save(failOnError:true)
		
		def equipment = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED, OrderStatus.OPENATFOSA)
		setupSecurityManager(sender)
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
		def sender = newOtherUser("sender", "sender", DataLocation.findByCode(KIVUYE))
		sender.userType = UserType.TITULAIREHC
		sender.save(failOnError:true)
		
		def receiverOne = newOtherUser("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO))
		receiverOne.userType = UserType.TECHNICIANDH
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
		
		def senderTitulaire = newUser("senderTitulaire", true,true)
		senderTitulaire.userType = UserType.TITULAIREHC
		senderTitulaire.location = DataLocation.findByCode(KIVUYE)
		senderTitulaire.save(failOnError:true)
		
		def senderDepartment = newUser("senderDepartment", true,true)
		senderDepartment.userType = UserType.HOSPITALDEPARTMENT
		senderDepartment.location = DataLocation.findByCode(KIVUYE)
		senderDepartment.save(failOnError:true)
		
		def receiverFacilityOne = newUser("receiverFacilityOne", true,true)
		receiverFacilityOne.userType = UserType.TECHNICIANDH
		receiverFacilityOne.location = DataLocation.findByCode(BUTARO)
		receiverFacilityOne.save(failOnError:true)
		
		def receiverFacilityTwo = newUser("receiverFacilityTwo", true,true)
		receiverFacilityTwo.userType = UserType.TECHNICIANDH
		receiverFacilityTwo.location = DataLocation.findByCode(BUTARO)
		receiverFacilityTwo.save(failOnError:true)
		
		def equipmentManaged = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentNotManaged = newEquipment(CODE(133),DataLocation.findByCode(BUTARO))
		
		def workOrderOne = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderTitulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderTitulaire,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacilityOne,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderDepartment,false)
		
		setupSecurityManager(receiverFacilityOne)
		def notification = notificationWorkOrderService.searchNotificition("one",receiverFacilityOne,workOrderOne,null,[:])[0]
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
		
		def senderTitulaire = newUser("senderTitulaire", true,true)
		senderTitulaire.userType = UserType.TITULAIREHC
		senderTitulaire.location = DataLocation.findByCode(KIVUYE)
		senderTitulaire.save(failOnError:true)
		
		def senderDepartment = newUser("senderDepartment", true,true)
		senderDepartment.userType = UserType.HOSPITALDEPARTMENT
		senderDepartment.location = DataLocation.findByCode(KIVUYE)
		senderDepartment.save(failOnError:true)
		
		def receiverFacilityOne = newUser("receiverFacilityOne", true,true)
		receiverFacilityOne.userType = UserType.TECHNICIANDH
		receiverFacilityOne.location = DataLocation.findByCode(BUTARO)
		receiverFacilityOne.save(failOnError:true)
		
		def receiverFacilityTwo = newUser("receiverFacilityTwo", true,true)
		receiverFacilityTwo.userType = UserType.TECHNICIANDH
		receiverFacilityTwo.location = DataLocation.findByCode(BUTARO)
		receiverFacilityTwo.save(failOnError:true)
		
		def equipmentManaged = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentNotManaged = newEquipment(CODE(133),DataLocation.findByCode(BUTARO))
		
		def workOrderOne = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderTitulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderTitulaire,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacilityOne,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderDepartment,false)
		
		setupSecurityManager(receiverFacilityOne)
		
		def notifications = notificationWorkOrderService.searchNotificition("one",receiverFacilityOne,workOrderOne,null,[:])
		notificationWorkOrderService.setNotificationRead(notifications[0])
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://Get only those that are unread
		notificationWorkOrderController.params.read = "false"
		notificationWorkOrderController.params.id = workOrderTwo.id
		notificationWorkOrderController.list()
		
		then:
		notificationWorkOrderController.modelAndView.model.entities.size() == 1
		Notification.count() == 5
	}
	
	def "can list notification - all of them for the current user"(){
		setup:
		setupLocationTree()
		
		def senderTitulaire = newUser("senderTitulaire", true,true)
		senderTitulaire.userType = UserType.TITULAIREHC
		senderTitulaire.location = DataLocation.findByCode(KIVUYE)
		senderTitulaire.save(failOnError:true)
		
		def senderDepartment = newUser("senderDepartment", true,true)
		senderDepartment.userType = UserType.HOSPITALDEPARTMENT
		senderDepartment.location = DataLocation.findByCode(KIVUYE)
		senderDepartment.save(failOnError:true)
		
		def receiverFacilityOne = newUser("receiverFacilityOne", true,true)
		receiverFacilityOne.userType = UserType.TECHNICIANDH
		receiverFacilityOne.location = DataLocation.findByCode(BUTARO)
		receiverFacilityOne.save(failOnError:true)
		
		def receiverFacilityTwo = newUser("receiverFacilityTwo", true,true)
		receiverFacilityTwo.userType = UserType.TECHNICIANDH
		receiverFacilityTwo.location = DataLocation.findByCode(BUTARO)
		receiverFacilityTwo.save(failOnError:true)
		
		def equipmentManaged = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentNotManaged = newEquipment(CODE(133),DataLocation.findByCode(BUTARO))
		
		def workOrderOne = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderTitulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderTitulaire,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacilityOne,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderDepartment,false)
		
		setupSecurityManager(receiverFacilityOne)
		
		def notifications = notificationWorkOrderService.searchNotificition("one",receiverFacilityOne,workOrderOne,null,[:])
		notificationWorkOrderService.setNotificationRead(notifications[0])
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://list all for a given user
		notificationWorkOrderController = new NotificationWorkOrderController()
		notificationWorkOrderController.list()
		
		then:
		notificationWorkOrderController.modelAndView.model.entities.size() == 2
		Notification.count() == 5
	}
	
//	def "can filter notifications"(){
//		setup:
//		setupLocationTree()
//		setupEquipment()
//		def senderOne = newUser("senderOne", true,true)
//		senderOne.userType = UserType.TITULAIREHC
//		senderOne.location = DataLocation.findByCode(KIVUYE)
//		senderOne.save(failOnError:true)
//		
//		def senderTwo = newUser("senderTwo", true,true)
//		senderTwo.userType = UserType.TITULAIREHC
//		senderTwo.location = DataLocation.findByCode(KIVUYE)
//		senderTwo.save(failOnError:true)
//		
//		def receiverFacility = newUser("receiverFacility", true,true)
//		receiverFacility.userType = UserType.TECHNICIANDH
//		receiverFacility.location = DataLocation.findByCode(KIVUYE)
//		receiverFacility.save(failOnError:true)
//		
//		def receiverMoH = newUser("receiverMoH", true,true)
//		receiverMoH.userType = UserType.TECHNICIANDH
//		receiverMoH.location = Location.findByCode(RWANDA)
//		receiverMoH.save(failOnError:true)
//		def equipment = Equipment.findBySerialNumber(CODE(123))
//		
//		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
//		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
//		
//		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
//		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
//		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
//		setupSecurityManager(receiverFacility)
//		notificationWorkOrderController = new NotificationWorkOrderController()
//		
//		when://Get only those that are unread
//		notificationWorkOrderController.params.read = "false"
//		notificationWorkOrderController.params.to = Initializer.now()+1
//		notificationWorkOrderController.filter()
//		
//		then:
//		//There are 4 notifications because the creator of a workorder always gets a copy
//		Notification.count() == 4
//		notificationWorkOrderController.modelAndView.model.entities.size() == 2
//	}
	
//	def "can search notifications"(){
//		setup:
//		setupLocationTree()
//		setupEquipment()
//		def senderOne = newUser("senderOne", true,true)
//		senderOne.userType = UserType.TITULAIREHC
//		senderOne.location = DataLocation.findByCode(KIVUYE)
//		senderOne.save(failOnError:true)
//		
//		def senderTwo = newUser("senderTwo", true,true)
//		senderTwo.userType = UserType.TITULAIREHC
//		senderTwo.location = DataLocation.findByCode(KIVUYE)
//		senderTwo.save(failOnError:true)
//		
//		def receiverFacility = newUser("receiverFacility", true,true)
//		receiverFacility.userType = UserType.TECHNICIANDH
//		receiverFacility.location = DataLocation.findByCode(KIVUYE)
//		receiverFacility.save(failOnError:true)
//		
//		def receiverMoH = newUser("receiverMoH", true,true)
//		receiverMoH.userType = UserType.TECHNICIANDH
//		receiverMoH.location = Location.findByCode(RWANDA)
//		receiverMoH.save(failOnError:true)
//		def equipment = Equipment.findBySerialNumber(CODE(123))
//		
//		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
//		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
//		
//		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
//		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,false)
//		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
//		setupSecurityManager(receiverFacility)
//		notificationWorkOrderController = new NotificationWorkOrderController()
//		
//		when://Get only those that are unread
//		notificationWorkOrderController.params.q = "one"
//		notificationWorkOrderController.search()
//		
//		then:
//		notificationWorkOrderController.modelAndView.model.entities.size() == 1
//	}
}