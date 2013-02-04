package org.chai.memms

import java.util.Map;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.preventive.maintenance.DurationBasedOrder;
import org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType;
import org.chai.memms.preventive.maintenance.PreventiveOrder;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.preventive.maintenance.WorkBasedOrder;
import org.chai.memms.preventive.maintenance.WorkBasedOrder.WorkIntervalType;
import org.chai.memms.security.User.UserType;
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.DataLocation

class MaintenanceServiceSpec extends IntegrationTests{
	def maintenanceService
	def "can retrieve all location levels to skip"() {
		when:
		def skipLevels = maintenanceService.getSkipLocationLevels()

		then:
		skipLevels.size() == grailsApplication.config.location.sector.skip.level.size()
	}

	
	def "can search for an order"(){
		setup:
		setupLocationTree()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipmentDurationBasedOrderOne = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentWorkBasedOrderOne = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		
		def equipmentDurationBasedOrderTwo = newEquipment(CODE(125),DataLocation.findByCode(BUTARO))
		def equipmentWorkBasedOrderTwo = newEquipment(CODE(126),DataLocation.findByCode(BUTARO))
		//Duration based orders
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description two",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderTwo,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description three",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		//Work Based Orders
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderTwo, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
			
		when:
		def durationBasedOrdersSearchByEquipment = maintenanceService.searchOrder(DurationBasedOrder.class,"",null,equipmentDurationBasedOrderOne,[:])
		def workBasedOrdersSearchByEquipment = maintenanceService.searchOrder(WorkBasedOrder.class,"",null,equipmentWorkBasedOrderTwo,[:])
		
		def durationBasedOrdersSearchByDataLocation = maintenanceService.searchOrder(DurationBasedOrder.class,"",DataLocation.findByCode(KIVUYE),null,[:])
		def workBasedOrdersSearchByDataLocation = maintenanceService.searchOrder(WorkBasedOrder.class,"",DataLocation.findByCode(BUTARO),null,[:])
		
		def durationBasedOrdersSearchText = maintenanceService.searchOrder(DurationBasedOrder.class,"description",null,null,[:])
		def workBasedOrdersSearchByText = maintenanceService.searchOrder(WorkBasedOrder.class,"testing",null,null,[:])
		then:
		durationBasedOrdersSearchByEquipment.size() == 2
		workBasedOrdersSearchByEquipment.size() == 1
		
		durationBasedOrdersSearchByDataLocation.size() == 2
		workBasedOrdersSearchByDataLocation.size() == 1
		
		durationBasedOrdersSearchText.size() == 3
		workBasedOrdersSearchByText.size() == 2
	}
	
	def "can get maintenances by location"(){
		setup:
		setupLocationTree()
			
		when:
		def ordersOne = maintenanceService.getMaintenancesByLocation(PreventiveOrder.class,Location.findByCode(GITARAMA),null,[:])
		def ordersTwo = maintenanceService.getMaintenancesByLocation(PreventiveOrder.class,Location.findByCode(BURERA),null,[:])
		then:
		ordersOne.totalCount == 2
		ordersOne.maintenanceList.size() == 2
		
		ordersTwo.totalCount == 3
		ordersTwo.maintenanceList.size() == 3
	}
	
	def "can get maintenanceOrder by dataLocation and manages"(){
		setup:
		setupLocationTree()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipmentDurationBasedOrderOne = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentWorkBasedOrderOne = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		
		def equipmentDurationBasedOrderTwo = newEquipment(CODE(125),DataLocation.findByCode(BUTARO))
		def equipmentWorkBasedOrderTwo = newEquipment(CODE(126),DataLocation.findByCode(BUTARO))
		//Duration based orders
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description two",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderTwo,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description three",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		//Work Based Orders
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderTwo, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
			
		when:
		def ordersButaro = maintenanceService.getMaintenanceOrderByDataLocationAndManages(WorkBasedOrder.class,DataLocation.findByCode(BUTARO) ,[:])
		def ordersKivuye = maintenanceService.getMaintenanceOrderByDataLocationAndManages(DurationBasedOrder.class,DataLocation.findByCode(KIVUYE) ,[:])
		then:
		ordersButaro.size() == 3
		ordersKivuye.size() == 2
	}
	
	def "can get maintenanceOrder by dataLocation"(){
		setup:
		setupLocationTree()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipmentDurationBasedOrderOne = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentWorkBasedOrderOne = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		
		def equipmentDurationBasedOrderTwo = newEquipment(CODE(125),DataLocation.findByCode(BUTARO))
		def equipmentWorkBasedOrderTwo = newEquipment(CODE(126),DataLocation.findByCode(BUTARO))
		//Duration based orders
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description two",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderTwo,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description three",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		//Work Based Orders
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderTwo, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
			
		when:
		def ordersButaro = maintenanceService.getMaintenanceOrderByDataLocation(WorkBasedOrder.class,DataLocation.findByCode(BUTARO) ,[:])
		def ordersKIvuye = maintenanceService.getMaintenanceOrderByDataLocation(DurationBasedOrder.class,DataLocation.findByCode(KIVUYE) ,[:])
		then:
		ordersButaro.size() == 1
		ordersKIvuye.size() == 2
	}
	
	def "can get maintenanceOrder by equipment"(){
		setup:
		setupLocationTree()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipmentDurationBasedOrderOne = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentWorkBasedOrderOne = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		
		def equipmentDurationBasedOrderTwo = newEquipment(CODE(125),DataLocation.findByCode(BUTARO))
		def equipmentWorkBasedOrderTwo = newEquipment(CODE(126),DataLocation.findByCode(BUTARO))
		//Duration based orders
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description two",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderTwo,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description three",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		//Work Based Orders
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderTwo, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
			
		when:
		def ordersButaro = maintenanceService.getMaintenanceOrderByEquipment(WorkBasedOrder.class,equipmentWorkBasedOrderOne ,[:])
		def ordersKIvuye = maintenanceService.getMaintenanceOrderByEquipment(DurationBasedOrder.class,equipmentDurationBasedOrderTwo ,[:])
		then:
		ordersButaro.size() == 2
		ordersKIvuye.size() == 1
	}
	
	//This gets the managed orders too
	def "can get maintenanceOrder by CalculationLocation"(){
		setup:
		setupLocationTree()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipmentDurationBasedOrderOne = newEquipment(CODE(123),DataLocation.findByCode(KIVUYE))
		def equipmentWorkBasedOrderOne = newEquipment(CODE(124),DataLocation.findByCode(KIVUYE))
		
		def equipmentDurationBasedOrderTwo = newEquipment(CODE(125),DataLocation.findByCode(BUTARO))
		def equipmentWorkBasedOrderTwo = newEquipment(CODE(126),DataLocation.findByCode(BUTARO))
		//Duration based orders
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderOne,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description two",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		Initializer.newDurationBasedOrder(equipmentDurationBasedOrderTwo,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description three",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		//Work Based Orders
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderOne, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"testing"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		Initializer.newWorkBasedOrder( equipmentWorkBasedOrderTwo, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description one",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
			
		when:
		def ordersButaro = maintenanceService.getMaintenanceOrderByCalculationLocation(WorkBasedOrder.class,DataLocation.findByCode(BUTARO) ,[:])
		def ordersKIvuye = maintenanceService.getMaintenanceOrderByCalculationLocation(DurationBasedOrder.class,Location.findByCode(GITARAMA) ,[:])
		then:
		ordersButaro.size() == 3
		ordersKIvuye.size() == 3
	}
}
