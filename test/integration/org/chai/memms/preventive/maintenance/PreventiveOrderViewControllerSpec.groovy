package org.chai.memms.preventive.maintenance

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

class PreventiveOrderViewControllerSpec extends IntegrationTests {
	def preventiveOrderViewController
	
	//TODO why does it pass even when the system user is not set??
	def "redirects to listing when accessing summary page by a user with a datalocation"(){
		setup:
		setupLocationTree()
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
		setupSecurityManager(newSystemUser("user", "user", DataLocation.findByCode(BUTARO)))
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.summaryPage()
		then:
		preventiveOrderViewController.response.redirectedUrl == '/preventiveOrderView/list?dataLocation.id=' + DataLocation.findByCode(BUTARO).id
	}

	
	def "sends a list of null maintenance when accessing summaryPage by a user with a location without specifying the location"(){
		setup:
		setupLocationTree()
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
		setupSecurityManager(newSystemUser("user", "user", Location.findByCode(GITARAMA)))
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.summaryPage()
		then:
		preventiveOrderViewController.response.redirectedUrl == null
		preventiveOrderViewController.modelAndView.model.maintenances == null
	}
	
	def "sends a list of maintenances by location when accessing summaryPage by a user with a location"(){
		setup:
		setupLocationTree()
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
		setupSecurityManager(newSystemUser("user", "user", Location.findByCode(SOUTH)))
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.params.location = Location.findByCode(GITARAMA).id + ""
		preventiveOrderViewController.summaryPage()
		then:
		preventiveOrderViewController.response.redirectedUrl == null
		preventiveOrderViewController.modelAndView.model.entityCount == 2
	}
	
	def "can't search without ajax"(){
		setup:
		setupLocationTree()
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
		setupSecurityManager(newSystemUser("user", "user", Location.findByCode(SOUTH)))
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.params.q = "description"
		preventiveOrderViewController.search()
		then:
		preventiveOrderViewController.response.status == 404
	}
	
	def "can search with ajax"(){
		setup:
		setupLocationTree()
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
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.params.q = "testing"
		preventiveOrderViewController.request.makeAjaxRequest()
		preventiveOrderViewController.search()
		then:
		preventiveOrderViewController.response.json.results[0].contains("one")
		!preventiveOrderViewController.response.json.results[0].contains("three")
	}
	
	def "can list equipments - ajax by datalocation"(){
		setup:
		setupLocationTree()
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
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.params."dataLocation.id" = DataLocation.findByCode(KIVUYE).id
		preventiveOrderViewController.request.makeAjaxRequest()
		preventiveOrderViewController.list()
		then:
		preventiveOrderViewController.response.json.results[0].contains("one")
		preventiveOrderViewController.response.json.results[0].contains("two")
		!preventiveOrderViewController.response.json.results[0].contains("three")
	}
	
	def "can list equipments - ajax by equipment"(){
		setup:
		setupLocationTree()
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
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.params."equipment.id" = equipmentDurationBasedOrderOne.id
		preventiveOrderViewController.request.makeAjaxRequest()
		preventiveOrderViewController.list()
		then:
		preventiveOrderViewController.response.json.results[0].contains("one")
		preventiveOrderViewController.response.json.results[0].contains("two")
		!preventiveOrderViewController.response.json.results[0].contains("three")
	}
	
	def "can list equipments - none ajax by datalocation"(){
		setup:
		setupLocationTree()
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
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.params."dataLocation.id" = DataLocation.findByCode(KIVUYE).id
		preventiveOrderViewController.list()
		then:
		preventiveOrderViewController.modelAndView.model.entityCount == 4
	}
	
	def "can list equipments - none ajax by equipment"(){
		setup:
		setupLocationTree()
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
		preventiveOrderViewController = new PreventiveOrderViewController()
		when:
		preventiveOrderViewController.params."equipment.id" = equipmentDurationBasedOrderOne.id
		preventiveOrderViewController.list()
		then:
		preventiveOrderViewController.modelAndView.model.entityCount == 2
	}
}
