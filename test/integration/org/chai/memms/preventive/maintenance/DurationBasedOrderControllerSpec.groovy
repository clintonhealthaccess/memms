package org.chai.memms.preventive.maintenance

import org.chai.memms.IntegrationTests
import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.Equipment;
import org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;
import net.sf.json.JSONArray;
import org.chai.location.DataLocation

class DurationBasedOrderControllerSpec extends IntegrationTests {
	
	def durationBasedOrderController
	
	def "can create and save a duration based order"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		durationBasedOrderController = new DurationBasedOrderController()
		when:
		durationBasedOrderController.params.equipment = equipment
		durationBasedOrderController.params.addedBy = addedBy
		durationBasedOrderController.params.type = PreventiveOrderType.DURATIONBASED
		durationBasedOrderController.params.status = PreventiveOrderStatus.CLOSED
		durationBasedOrderController.params.description = "test"
		durationBasedOrderController.params.preventionResponsible = PreventionResponsible.SERVICEPROVIDER
		durationBasedOrderController.params.firstOccurenceOn = Initializer.newTimeDate(Initializer.now(),"24:60:60")
		durationBasedOrderController.params.occurency = OccurencyType.DAILY
		durationBasedOrderController.save()
		then:
		DurationBasedOrder.count() == 1
	}
	
	def "projection works with first occurence between range"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		durationBasedOrderController = new DurationBasedOrderController()
		
		when:
		def order = newDurationBasedOrder(equipment, addedBy, Initializer.newTimeDate(Initializer.now(),"24:60:60"), OccurencyType.DAILY)
		durationBasedOrderController.params['dataLocation.id'] = DataLocation.findByCode(KIVUYE).id
		durationBasedOrderController.params.start = (Initializer.now() - 1).getTime() / 1000L
		durationBasedOrderController.params.end = (Initializer.now() + 1).getTime() / 1000L
		durationBasedOrderController.request.format = 'json' 
		durationBasedOrderController.projection()
		
		def response = durationBasedOrderController.response.contentAsString
		def jsonResult = JSONArray.fromObject(response)
		
		then:
		jsonResult.size() == 1
		jsonResult[0].id == order.id
		
	}
	
	def "projection works with first occurence after range"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		durationBasedOrderController = new DurationBasedOrderController()
		
		when:
		def order = newDurationBasedOrder(equipment, addedBy, Initializer.newTimeDate(Initializer.now(),"24:60:60"), OccurencyType.DAILY)
		durationBasedOrderController.params['dataLocation.id'] = DataLocation.findByCode(KIVUYE).id
		durationBasedOrderController.params.start = (Initializer.now() - 2).getTime() / 1000L
		durationBasedOrderController.params.end = (Initializer.now() - 1).getTime() / 1000L
		durationBasedOrderController.request.format = 'json' 
		durationBasedOrderController.projection()
		
		def response = durationBasedOrderController.response.contentAsString
		def jsonResult = JSONArray.fromObject(response)
		
		then:
		jsonResult.size() == 0
		
	}
	
	def "projection works with first occurence before range"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		durationBasedOrderController = new DurationBasedOrderController()
		
		when:
		def order = newDurationBasedOrder(equipment, addedBy, Initializer.newTimeDate(Initializer.now(),"24:60:60"), OccurencyType.DAILY)
		durationBasedOrderController.params['dataLocation.id'] = DataLocation.findByCode(KIVUYE).id
		durationBasedOrderController.params.start = (Initializer.now() + 1).getTime() / 1000L
		durationBasedOrderController.params.end = (Initializer.now() + 2).getTime() / 1000L
		durationBasedOrderController.request.format = 'json' 
		durationBasedOrderController.projection()
		
		def response = durationBasedOrderController.response.contentAsString
		def jsonResult = JSONArray.fromObject(response)
		
		then:
		jsonResult.size() == 1
		jsonResult[0].id == order.id
		
	}
	
	def "projection works with first occurence before range and more than once"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		durationBasedOrderController = new DurationBasedOrderController()
		
		when:
		def order = newDurationBasedOrder(equipment, addedBy, Initializer.newTimeDate(Initializer.now(),"24:60:60"), OccurencyType.DAILY)
		durationBasedOrderController.params['dataLocation.id'] = DataLocation.findByCode(KIVUYE).id
		durationBasedOrderController.params.start = (Initializer.now() + 1).getTime() / 1000L
		durationBasedOrderController.params.end = (Initializer.now() + 3).getTime() / 1000L
		durationBasedOrderController.request.format = 'json' 
		durationBasedOrderController.projection()
		
		def response = durationBasedOrderController.response.contentAsString
		def jsonResult = JSONArray.fromObject(response)
		
		then:
		jsonResult.size() == 2
		jsonResult[0].id == order.id
		jsonResult[1].id == order.id		
	}
	
}
