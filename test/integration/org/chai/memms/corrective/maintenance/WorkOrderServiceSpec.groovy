package org.chai.memms.corrective.maintenance

import java.util.Date;
import java.util.Map;

import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.Equipment;
import org.chai.memms.MaintenanceService
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.WorkOrderStatusChange;
import org.chai.memms.corrective.maintenance.WorkOrderService;

import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;

/**
 * @author Eugene
 *
 */
class WorkOrderServiceSpec  extends IntegrationTests{
	
	def maintenanceService
	def workOrderService
	def notificationWorkOrderService
	
	def "can search a work order"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def senderOne = newUser("senderOne", true,true)
		Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		
		when:
		
		//Search by description
		def workOrdersPassesDescription = maintenanceService.searchOrder(WorkOrder.class,"Nothing yet",null,null,[:])
		def workOrdersFailsDescription = maintenanceService.searchOrder(WorkOrder.class,"fails",null,null,[:])
		
		//search by DataLocation
		def workOrdersPassesDataLocation = maintenanceService.searchOrder(WorkOrder.class,"Nothing",DataLocation.findByCode(KIVUYE),null,[:])
		def workOrdersFailsDataLocation = maintenanceService.searchOrder(WorkOrder.class,"Nothing",DataLocation.findByCode(BUTARO),null,[:])
		
		//Search by Equipment
		def workOrdersPassesEquipment = maintenanceService.searchOrder(WorkOrder.class,"Nothing",null,equipment,[:])
		
		//Search by equipment serial number
		def workOrdersPassesEquipmentSerialnumber = maintenanceService.searchOrder(WorkOrder.class,CODE(123),null,null,[:])
		
		//Search by equipment type
		def workOrdersPassesEquipmentType = maintenanceService.searchOrder(WorkOrder.class,"acce",null,null,[:])
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
		def musanze = DataLocation.findByCode(MUSANZE)
		//Filter by DataLocation
		def workOrdersPassesDataLocation = workOrderService.filterWorkOrders(kivuye,null,null,null,null,null,[:])
		def workOrdersFailsDataLocation = workOrderService.filterWorkOrders(musanze,null,null,null,null,null,[:])
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
		clerk.userType = UserType.TITULAIREHC
		clerk.location = DataLocation.findByCode(KIVUYE)
		clerk.save(failOnError:true)
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,clerk,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		notificationWorkOrderService.newNotification(workOrder, "Send for rapair",clerk,false)
		when:
		def equipments = maintenanceService.getMaintenanceOrderByEquipment(WorkOrder.class,equipment,[:])
		then:
		equipments.size() == 1
	}
	
	def "can get workOrders by calculationLocation"(){
		setup:
		setupLocationTree()
		setupSystemUser()
		def equipment = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def senderOne = newUser("senderOne", true,true)
		Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,senderOne, Initializer.getDate(12, 9,2012),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.LOW,senderOne, Initializer.getDate(12, 9,2012),Initializer.getDate(18, 9,2012),FailureReason.NOTSPECIFIED,OrderStatus.CLOSEDFIXED)
		when:
		//TODO to be fixed
		def workOrdersPassesDataLocation = maintenanceService.getMaintenanceOrderByCalculationLocation(WorkOrder.class,DataLocation.findByCode(KIVUYE),[:])
		def workOrdersFailsDataLocation = maintenanceService.getMaintenanceOrderByCalculationLocation(WorkOrder.class,CalculationLocation.findByCode(BURERA),[:])
		
		then:
		workOrdersPassesDataLocation.size() == 2
		workOrdersFailsDataLocation.size() == 0
	}

	def "can escalate a WorkOrders"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def titulaire = newUser("clerk", true,true)
		titulaire.userType = UserType.TITULAIREHC
		titulaire.location = DataLocation.findByCode(KIVUYE)
		titulaire.save(failOnError:true)
		
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,titulaire,Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		workOrderService.escalateWorkOrder( workOrder,"needs further review", titulaire)
		then:
		workOrder.status.size() == 1
		workOrder.status.escalation
	}
}
