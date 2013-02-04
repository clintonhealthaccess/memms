package org.chai.memms.preventive.maintenance

import org.chai.memms.IntegrationTests
import org.chai.memms.inventory.Equipment;
import org.chai.memms.preventive.maintenance.DurationBasedOrder.OccurencyType;
import org.chai.memms.preventive.maintenance.Prevention
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible;
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus;
import org.chai.memms.preventive.maintenance.WorkBasedOrder.WorkIntervalType;
import org.chai.memms.Initializer
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils;
import org.chai.location.DataLocation

class PreventionControllerSpec extends IntegrationTests {
	def preventionController
	def "can create and save a prevention - preventionDurationBased"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def durationBasedOrder = Initializer.newDurationBasedOrder(equipment,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		setupSecurityManager(newSystemUser("user", "user", DataLocation.findByCode(BUTARO)))
		preventionController = new PreventionController()
		when:
		preventionController.params.order = durationBasedOrder
		preventionController.params.addedBy = addedBy
		preventionController.params.scheduledOn = Initializer.newTimeDate(Initializer.now()-1, "01:00:00")
		preventionController.params.eventDate = Initializer.now()
		preventionController.save()
		then:
		Prevention.count() == 1
	}
	
	def "can create and save a prevention - preventionWorkBased"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workBasedOrder = Initializer.newWorkBasedOrder( equipment, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		setupSecurityManager(newSystemUser("user", "user", DataLocation.findByCode(BUTARO)))
		preventionController = new PreventionController()
		when:
		preventionController.params.order = workBasedOrder
		preventionController.params.addedBy = addedBy
		preventionController.params.scheduledOn = Initializer.newTimeDate(Initializer.now()-1, "01:00:00")
		preventionController.params.eventDate = Initializer.now()
		preventionController.save()
		then:
		Prevention.count() == 1
	}
}
