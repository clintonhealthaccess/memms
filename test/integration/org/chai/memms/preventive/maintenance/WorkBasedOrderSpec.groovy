package org.chai.memms.preventive.maintenance

import org.chai.memms.IntegrationTests
import org.chai.memms.Initializer;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderType;
import org.chai.memms.preventive.maintenance.WorkBasedOrder.WorkIntervalType;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;
import org.chai.location.DataLocation

class WorkBasedOrderSpec extends IntegrationTests {
	def "can create and save a workBased based order"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		//TODO the firstOccurenceOn time needs to have a "24:60:60", otherwise the condition would fail at times later than these
		def order  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency:WorkIntervalType.NONE).save(failOnError:true)
		then:
		WorkBasedOrder.count() == 1
	}
	
	def "if lastModifiedBy is not null, last updated should not be null too"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def lastModifiedByUser = newUser("lastModifiedByUser", CODE(124))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def orderWrong  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastModifiedBy:lastModifiedByUser,
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save()
		
		def orderRight  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastModifiedBy:lastModifiedByUser,lastUpdated:Initializer.now()-1,
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save(failOnError:true)
		then:
		WorkBasedOrder.count() == 1
		orderWrong == null
	}
	
	def "date last updated should be less than today"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		
		def orderFail  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastUpdated:Initializer.now()+1,
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save()
		def orderPass  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",lastUpdated:Initializer.now(),
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save(failOnError:true)

		then:
		WorkBasedOrder.count() == 1
		//orderFail.errors.hasFieldErrors("lastUpdated") == true
		orderFail == null
	}
	
	def "date firstOccurenceOn should be less than today"() {
//		when:
//		setupLocationTree()
//		setupEquipment()
//		def addedBy = newUser("addedBy", CODE(123))
//		def equipment = Equipment.findBySerialNumber(CODE(123))
//		//TODO the firstOccurenceOn time needs to have a "24:60:60", otherwise the condition would fail at times later than these
//		def order  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
//			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save(failOnError:true)
//		then:
//		WorkBasedOrder.count() == 1
	}
	
	def "prevention order type cannot be none"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def order  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.NONE,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save()
		then:
		WorkBasedOrder.count() == 0
		order == null
	}
	
	def "preventionResponsible cannot be none"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def order  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
			preventionResponsible: PreventionResponsible.NONE, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save()
		then:
		WorkBasedOrder.count() == 0
		order == null
	}
	
	def "closedOn validations"() {
		when:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def order  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
				closedOn:Initializer.now(),
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save(failOnError:true)
		def orderFailsOne  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.CLOSED,description: "test",
				closedOn:Initializer.now()+1,
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save()
		def orderFailsTwo  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.OPEN,description: "test",
				closedOn:Initializer.now(),
				preventionResponsible: PreventionResponsible.SERVICEPROVIDER, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save()

		then:
		WorkBasedOrder.count() == 1
		orderFailsOne == null
		orderFailsTwo == null
	}
	
	def "technicianInCharge validations"() {
		when:
		setupLocationTree()
		setupEquipment()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.ASSISTANTTECHHOSP)
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def order  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.OPEN,description: "test",
				technicianInCharge:techInCharge,
				preventionResponsible: PreventionResponsible.HCTECHNICIAN, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save(failOnError:true)
		def orderFails  = new WorkBasedOrder(equipment: equipment,addedBy: addedBy,type: PreventiveOrderType.DURATIONBASED,status: PreventiveOrderStatus.OPEN,description: "test",
				preventionResponsible: PreventionResponsible.HCTECHNICIAN, firstOccurenceOn: Initializer.newTimeDate(Initializer.now(),"24:60:60"),occurency: WorkIntervalType.NONE).save()

		then:
		WorkBasedOrder.count() == 1
		orderFails == null
	}
}
