package org.chai.memms.maintenance

import java.util.Date;
import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.equipment.Equipment;
import org.chai.memms.maintenance.WorkOrder.Criticality;
import org.chai.memms.maintenance.WorkOrder.FailureReason;
import org.chai.memms.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;

/**
 * @author Eugene
 *
 */
class WorkOrderServiceSpec  extends IntegrationTests{
	
	def workOrderService
	def workOrderNotificationService
	
	def "can search a work order"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def senderOne = newUser("senderOne", true,true)
		Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		when:
		
		//Search by description
		def workOrdersPassesDescription = workOrderService.searchWorkOrder("Nothing yet",null,null,adaptParamsForList())
		def workOrdersFailsDescription = workOrderService.searchWorkOrder("fails",null,null,adaptParamsForList())
		
		//search by DataLocation
		def workOrdersPassesDataLocation = workOrderService.searchWorkOrder("Nothing",DataLocation.findByCode(KIVUYE),null,[:])
		def workOrdersFailsDataLocation = workOrderService.searchWorkOrder("Nothing",DataLocation.findByCode(BUTARO),null,[:])
		
		//Search by Equipment
		def workOrdersPassesEquipment = workOrderService.searchWorkOrder("Nothing",null,equipment,[:])
		
		//Search by equipment serial number
		def workOrdersPassesEquipmentSerialnumber = workOrderService.searchWorkOrder(CODE(123),null,null,adaptParamsForList())
		
		//Search by equipment type
		def workOrdersPassesEquipmentType = workOrderService.searchWorkOrder("acce",null,null,adaptParamsForList())
		then:
		workOrdersFailsDescription.size() == 0
		workOrdersPassesDescription.size() == 1
		
		workOrdersFailsDataLocation.size() == 0
		workOrdersPassesDataLocation.size() == 1
		
		workOrdersPassesEquipment.size() == 1
		
		workOrdersPassesEquipmentSerialnumber.size() == 1
		
		workOrdersPassesEquipmentType.size() == 1
	}
	
	def "can filter workOrders"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def senderOne = newUser("senderOne", true,true)		
		Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne, Initializer.getDate(12, 9,2012),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.LOW,senderOne, Initializer.getDate(12, 9,2012),Initializer.getDate(18, 9,2012),FailureReason.NOTSPECIFIED,OrderStatus.CLOSEDFIXED)		
		when:
		def kivuye = DataLocation.findByCode(KIVUYE)
		def butaro = DataLocation.findByCode(BUTARO)
		//Filter by DataLocation
		def workOrdersPassesDataLocation = workOrderService.filterWorkOrders(kivuye,null,null,null,null,null,[:])
		def workOrdersFailsDataLocation = workOrderService.filterWorkOrders(butaro,null,null,null,null,null,[:])
		//Filter by Equipment
		def workOrdersEquipment = workOrderService.filterWorkOrders(null,equipment,null,null,null,null,[:])
		
		//Filter by openOn
		def workOrdersopenOn = workOrderService.filterWorkOrders(null,null,Initializer.getDate(12, 9,2012),null,null,null,[:])
		
		//Filter by closedOn
		def workOrdersclosedOn = workOrderService.filterWorkOrders(null,null,null,Initializer.getDate(18, 9,2012),null,null,[:])
		
		//Filter by criticality
		def workOrdersCriticality = workOrderService.filterWorkOrders(null,null,null,null,Criticality.LOW,null,[:])
		
		//Filter by status
		def workOrdersStatus = workOrderService.filterWorkOrders(null,null,null,null,null,OrderStatus.OPENATFOSA,[:])
		
		then:
		workOrdersPassesDataLocation.size() == 2
		workOrdersFailsDataLocation.size() == 0
		
		workOrdersEquipment.size() == 2
		
		workOrdersopenOn.size() == 2
		
		workOrdersclosedOn.size() == 1
				
		workOrdersCriticality.size() == 1
		
		workOrdersStatus.size() == 1
	}
	
	def "can get WorkOrders By Equipment"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def clerk = newUser("clerk", true,true)
		clerk.userType = UserType.DATACLERK
		clerk.location = DataLocation.findByCode(KIVUYE)
		clerk.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,clerk,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		workOrderNotificationService.newNotification(workOrder, "Send for rapair",clerk,false)
		when:
		def equipments = workOrderService.getWorkOrdersByEquipment(equipment,[:])
		then:
		equipments.size() == 1
	}
}
