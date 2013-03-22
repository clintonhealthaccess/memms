package org.chai.memms.preventive.maintenance

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.Equipment;
import org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;
import org.chai.location.DataLocation
import grails.validation.ValidationException

class DurationBasedOrderSpec extends IntegrationTests {
	
	def "can create and save a durration based order"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		//TODO the firstOccurenceOn time needs to have a "24:60:60", otherwise the condition would fail at times later than these
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save(failOnError:true)
		
		then:
		DurationBasedOrder.count() == 1		
	}
	
	def "if occurence type is DAYS_OF_WEEK, then a list of days must be specified"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		when:
		//TODO the firstOccurenceOn time needs to have a "24:60:60", otherwise the condition would fail at times later than these
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAYS_OF_WEEK)
		order.save(failOnError: true)
			
		then:
		thrown ValidationException

		when:
		order.occurDaysOfWeek = [1, 3]
		order.save(failOnError: true)
		
		then:
		DurationBasedOrder.count() == 1
	}
	
	def "if lastModifiedBy is not null, last updated should not be null too"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def lastModifiedByUser = newUser("lastModifiedByUser", CODE(124))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		when:
		def orderWrong  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastModifiedBy:lastModifiedByUser,
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save()
		
		def orderRight  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastModifiedBy:lastModifiedByUser,lastUpdated:Initializer.now()-1,
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save(failOnError:true)
		then:
		DurationBasedOrder.count() == 1
		orderWrong == null
	}
	
	def "date last updated should be less than today"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def orderFail  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastUpdated:Initializer.now()+1,
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save()
		def orderPass  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastUpdated:Initializer.now(),
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save(failOnError:true)

		then:
		DurationBasedOrder.count() == 1
		//orderFail.errors.hasFieldErrors("lastUpdated") == true
		orderFail == null
	}
	
	def "date firstOccurenceOn should be less than today"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		//TODO the firstOccurenceOn time needs to have a "24:60:60", otherwise the condition would fail at times later than these
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save(failOnError:true)
		then:
		DurationBasedOrder.count() == 1
	}
	
	def "prevention order type cannot be none"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.NONE,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save()
		then:
		DurationBasedOrder.count() == 0
		order == null
	}
	
	def "preventionResponsible cannot be none"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.NONE, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save()
		then:
		DurationBasedOrder.count() == 0
		order == null
	}
	
	def "closedOn validations"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
				closedOn:Initializer.now(),
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save(failOnError:true)
		def orderFailsOne  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
				closedOn:Initializer.now()+1,
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save()
		def orderFailsTwo  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.OPEN,description: "test",
				closedOn:Initializer.now(),
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save()

		then:
		DurationBasedOrder.count() == 1
		orderFailsOne == null
		orderFailsTwo == null
	}
	
	def "technicianInCharge validations"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.ASSISTANTTECHHOSP)
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.OPEN,description: "test",
				technicianInCharge:techInCharge,
				preventionResponsible: PreventionResponsible.HCTECHNICIAN, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save(failOnError:true)
		def orderFails  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.OPEN,description: "test",
				preventionResponsible: PreventionResponsible.HCTECHNICIAN, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.DAILY).save()

		then:
		//As 
		DurationBasedOrder.count() == 1
		orderFails == null
	}
	
	def "occurency cannot be null"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		when:
		def order  = new DurationBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: OccurencyType.NONE).save()
		then:
		DurationBasedOrder.count() == 0
		order == null
	}
}
