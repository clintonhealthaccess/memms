package org.chai.memms.corrective.maintenance

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

class NotificationWorkOrderServiceSpec  extends IntegrationTests{
	
	def notificationWorkOrderService
	
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
		
		def equipment = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED, OrderStatus.OPENATFOSA)
		when:
		notificationWorkOrderService.newNotification(workOrder, "Send for rapair",sender,false)
		then:
		Notification.count() == 2
	}
	
	def "can escalate notifications"() {
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
		
		def receiverMoHOne = newUser("receiverMoHOne", true,true)
		receiverMoHOne.userType = UserType.TECHNICIANMMC
		receiverMoHOne.location = Location.findByCode(RWANDA)
		receiverMoHOne.save(failOnError:true)
		
		def receiverMoHTwo = newUser("receiverMoHTwo", true,true)
		receiverMoHTwo.userType = UserType.TECHNICIANMMC
		receiverMoHTwo.location = Location.findByCode(RWANDA)
		receiverMoHTwo.save(failOnError:true)
		
		def equipmentManaged = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentNotManaged = newEquipment(CODE(133),DataLocation.findByCode(BUTARO))
		
		def workOrderManaged = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderTitulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderNotManaged = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		def sentNotificationsOne, sentNotificationsTwo
		when:
		sentNotificationsOne = notificationWorkOrderService.newNotification(workOrderManaged, "Send for rapair, higher",receiverFacilityOne,true)
		sentNotificationsTwo = notificationWorkOrderService.newNotification(workOrderNotManaged, "Send for rapair, higher",receiverFacilityTwo,true)
		then:
		sentNotificationsOne == 3
		sentNotificationsTwo == 3
		Notification.count() == 6
	}
	
	def "reading a notification sets it's read status to true"() {
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
		
		def equipment = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender,Initializer.now(),FailureReason.NOTSPECIFIED, OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrder, "Send for rapair",sender,false)
		
		def notificationToRead = Notification.list()[0]
		when:
		notificationWorkOrderService.setNotificationRead(notificationToRead)
		then:
		NotificationWorkOrder.count() == 2
		NotificationWorkOrder.get(notificationToRead.id).read == true
	}
	
	def "can filter notifications"() {
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
		
		def receiverMoHOne = newUser("receiverMoHOne", true,true)
		receiverMoHOne.userType = UserType.TECHNICIANMMC
		receiverMoHOne.location = Location.findByCode(RWANDA)
		receiverMoHOne.save(failOnError:true)
		
		def receiverMoHTwo = newUser("receiverMoHTwo", true,true)
		receiverMoHTwo.userType = UserType.TECHNICIANMMC
		receiverMoHTwo.location = Location.findByCode(RWANDA)
		receiverMoHTwo.save(failOnError:true)
		
		def equipmentManaged = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentNotManaged = newEquipment(CODE(133),DataLocation.findByCode(BUTARO))
		
		def workOrderManaged = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderTitulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderNotManaged = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		notificationWorkOrderService.newNotification(workOrderManaged, "Send for rapair, one",senderTitulaire,false)
		notificationWorkOrderService.newNotification(workOrderManaged, "Send for rapair, higher",receiverFacilityOne,true)
		notificationWorkOrderService.newNotification(workOrderNotManaged, "Send for rapair, two",senderDepartment,false)
		
		def workOrderNotifications = NotificationWorkOrder.list()
		
		notificationWorkOrderService.setNotificationRead(workOrderNotifications[0])
		notificationWorkOrderService.setNotificationRead(workOrderNotifications[1])
		
		when:
		def allNotifications = notificationWorkOrderService.filterNotifications(null,null, null,null,null, [:])
		def readNotifications = notificationWorkOrderService.filterNotifications(null,null, null,null,true, [:])
		def notificationsByWorkOrder = notificationWorkOrderService.filterNotifications(workOrderManaged,null, null,null,null, [:])
		def notificationsByWorkOrderTwo = notificationWorkOrderService.filterNotifications(workOrderNotManaged,null, null,null,null, [:])
		def notificationsByreceiverMoH = notificationWorkOrderService.filterNotifications(null,receiverMoHOne,null,null,null, [:])
		def notificationsByreceiverTechDhOne = notificationWorkOrderService.filterNotifications(null,receiverFacilityOne,null,null,null, [:])
		def notificationsByreceiverTechDhTwo = notificationWorkOrderService.filterNotifications(null,receiverFacilityTwo,null,null,null, [:])
		def jointFilter = notificationWorkOrderService.filterNotifications(workOrderManaged,receiverFacilityTwo,null,null,true, [:])
		def madeAfterToday = notificationWorkOrderService.filterNotifications(null,null, Initializer.now()+1,null,null, [:])
		def madeBeforeToday = notificationWorkOrderService.filterNotifications(null,null, null,Initializer.now()+1,null, [:])
		def madeBetweenYesterdayAndTomorrow = notificationWorkOrderService.filterNotifications(null,null, Initializer.now()-1,Initializer.now()+1,null, [:])
		def unreadNotifications = notificationWorkOrderService.filterNotifications(null,null, null,null,false, [:])
		
		then:
		allNotifications.size() == 7
		notificationsByreceiverTechDhOne.size() == 2
		notificationsByreceiverTechDhTwo.size() == 2
		readNotifications.size() == 2
		notificationsByWorkOrder.size() == 5
		notificationsByWorkOrderTwo.size() == 2
		notificationsByreceiverMoH.size() == 1
		jointFilter.size() == 1
		madeAfterToday.size() == 0
		madeBeforeToday.size() == 7
		madeBetweenYesterdayAndTomorrow.size() == 7
		unreadNotifications.size() == 5
	}
	
	def "can search notifications"() {
		setup:
		setupLocationTree()
		
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
		
		def workOrderManaged = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderNotManaged = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		
		notificationWorkOrderService.newNotification(workOrderManaged, "Send for rapair, one",senderDepartment,false)
		notificationWorkOrderService.newNotification(workOrderNotManaged, "Send for rapair, follow up",senderDepartment,false)
		
		when:
		def found = notificationWorkOrderService.searchNotificition("one",receiverFacilityTwo,workOrderManaged, null,[:])
		then:
		NotificationWorkOrder.count() == 4
		found.size() == 1
	}
	
	def "get unread notifications count"() {
		setup:
		setupLocationTree()
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
		
		def workOrderManaged = Initializer.newWorkOrder(equipmentManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def workOrderNotManaged = Initializer.newWorkOrder(equipmentNotManaged, "Nothing yet", Criticality.NORMAL,senderDepartment,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		
		notificationWorkOrderService.newNotification(workOrderManaged, "Send for rapair, one",senderDepartment,false)
		notificationWorkOrderService.newNotification(workOrderNotManaged, "Send for rapair, follow up",senderDepartment,false)
		
		def unreadNotifications = 0
		
		when:
		unreadNotifications = notificationWorkOrderService.getUnreadNotifications(receiverFacilityOne)
		then:
		NotificationWorkOrder.count() == 4
		unreadNotifications == 2
	}
}
