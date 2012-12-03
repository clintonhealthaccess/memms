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
		
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
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
		def sender = newOtherUserWithType("sender", "sender", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def receiverOne = newOtherUserWithType("receiverOne", "receiverOne", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)

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
		
		def senderTitulaire = newOtherUserWithType("senderTitulaire", "senderTitulaire", DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderDepartment = newOtherUserWithType("senderDepartment", "senderDepartment", DataLocation.findByCode(KIVUYE), UserType.HOSPITALDEPARTMENT)
		
		def receiverFacilityOne = newOtherUserWithType("receiverFacilityOne", "receiverFacilityOne",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverFacilityTwo = newOtherUserWithType("receiverFacilityTwo", "receiverFacilityTwo",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
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
		
		def senderTitulaire = newOtherUserWithType("senderTitulaire", "senderTitulaire",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderDepartment = newOtherUserWithType("senderDepartment", "senderDepartment",DataLocation.findByCode(KIVUYE), UserType.HOSPITALDEPARTMENT)
		
		def receiverFacilityOne = newOtherUserWithType("receiverFacilityOne", "receiverFacilityOne",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverFacilityTwo = newOtherUserWithType("receiverFacilityTwo", "receiverFacilityTwo",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
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
		
		def senderTitulaire = newOtherUserWithType("senderTitulaire", "senderTitulaire",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderDepartment = newOtherUserWithType("senderDepartment", "senderDepartment",DataLocation.findByCode(KIVUYE), UserType.HOSPITALDEPARTMENT)
		
		def receiverFacilityOne = newOtherUserWithType("receiverFacilityOne", "receiverFacilityOne",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverFacilityTwo = newOtherUserWithType("receiverFacilityTwo", "receiverFacilityTwo",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
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
	
	def "can list notification - all of them for the current user using ajax"(){
		setup:
		setupLocationTree()
		
		def senderTitulaire = newOtherUserWithType("senderTitulaire", "senderTitulaire",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderDepartment = newOtherUserWithType("senderDepartment", "senderDepartment",DataLocation.findByCode(KIVUYE), UserType.HOSPITALDEPARTMENT)
		
		def receiverFacilityOne = newOtherUserWithType("receiverFacilityOne", "receiverFacilityOne",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def receiverFacilityTwo = newOtherUserWithType("receiverFacilityTwo", "receiverFacilityTwo",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def equipmentManaged = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentNotManaged = newEquipment(CODE(133),DataLocation.findByCode(BUTARO))
		
		def workOrderOne = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderTitulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderTitulaire,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacilityOne,false)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderDepartment,false)
		
		setupSecurityManager(receiverFacilityOne)
		
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://list all for a given user
		notificationWorkOrderController = new NotificationWorkOrderController()
		notificationWorkOrderController.request.makeAjaxRequest()
		notificationWorkOrderController.list()
		
		then:
		NotificationWorkOrder.count() == 5
		notificationWorkOrderController.response.json.results[0].contains("Send for rapair, one")
		!notificationWorkOrderController.response.json.results[0].contains("Send for rapair, higher")
		notificationWorkOrderController.response.json.results[0].contains("Send for rapair, two")
	}
	
	def "can filter notifications"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newOtherUserWithType("senderOne", "senderOne",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def receiverFacility = newOtherUserWithType("receiverFacility", "receiverFacility",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def techMMC = newOtherUserWithType("techMMC", "techMMC",Location.findByCode(RWANDA),UserType.TECHNICIANMMC)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,true)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		
		setupSecurityManager(receiverFacility)
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://Get only those that are unread
		notificationWorkOrderController.params.read = "false"
		notificationWorkOrderController.params.to = Initializer.now()+1
		notificationWorkOrderController.filter()
		
		then:
		NotificationWorkOrder.count() == 5
		notificationWorkOrderController.response.json.results[0].contains("Send for rapair, one")
		!notificationWorkOrderController.response.json.results[0].contains("Send for rapair, higher")
		notificationWorkOrderController.response.json.results[0].contains("Send for rapair, two")
	}
	
	def "cannot filter notifications without using ajax"(){
		setup:
		setupLocationTree()
		setupEquipment()
		
		def senderOne = newOtherUserWithType("senderOne", "senderOne",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def receiverFacility = newOtherUserWithType("receiverFacility", "receiverFacility",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def techMMC = newOtherUserWithType("techMMC", "techMMC",Location.findByCode(RWANDA),UserType.TECHNICIANMMC)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,true)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://Get only those that are unread
		notificationWorkOrderController.params.read = "false"
		notificationWorkOrderController.params.to = Initializer.now()+1
		notificationWorkOrderController.filter()
		
		then:
		NotificationWorkOrder.count() == 5
		notificationWorkOrderController.response.status == 404
	}
	
	def "can search notifications"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def senderOne = newOtherUserWithType("senderOne", "senderOne",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def senderTwo = newOtherUserWithType("senderTwo", "senderTwo",DataLocation.findByCode(KIVUYE),UserType.TITULAIREHC)
		
		def receiverFacility = newOtherUserWithType("receiverFacility", "receiverFacility",DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		
		def techMMC = newOtherUserWithType("techMMC", "techMMC",Location.findByCode(RWANDA),UserType.TECHNICIANMMC)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def workOrderOne = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderTwo = Initializer.newWorkOrder(equipment, "Nothing yet",Criticality.NORMAL,senderOne,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, one",senderOne,false)
		notificationWorkOrderService.newNotification(workOrderOne, "Send for rapair, higher",receiverFacility,true)
		notificationWorkOrderService.newNotification(workOrderTwo, "Send for rapair, two",senderTwo,false)
		setupSecurityManager(receiverFacility)
		notificationWorkOrderController = new NotificationWorkOrderController()
		
		when://Get only those that are unread
		notificationWorkOrderController.params.q = "one"
		notificationWorkOrderController.search()
		
		then:
		NotificationWorkOrder.count() == 5
		notificationWorkOrderController.response.json.results[0].contains("Send for rapair, one")
		!notificationWorkOrderController.response.json.results[0].contains("Send for rapair, higher")
		!notificationWorkOrderController.response.json.results[0].contains("Send for rapair, two")
	}
}