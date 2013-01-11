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

class PreventionSpec extends IntegrationTests {
	def "can create and save a prevention"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def durationBasedOrder = Initializer.newDurationBasedOrder(equipment,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVIDEPROVIDER,null,["en":"test"],"description",
			Initializer.now()+1,null,OccurencyType.DAILY,true,1,1,null)
		def workBasedOrder = Initializer.newWorkBasedOrder( equipment, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVIDEPROVIDER,null,["en":"test"],"description",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
			
		when:
		//TODO scheduledOn seems to take a past date, and yet is supposed to be a future event
		def preventionDurationBased = new Prevention(order:durationBasedOrder,addedBy:addedBy,scheduledOn:Initializer.newTimeDate(Initializer.now()-1, "01:00:00") ,eventDate:Initializer.now()).save(failOnError:true)
		def preventionWorkBased = new Prevention(order:workBasedOrder,addedBy:addedBy,scheduledOn:Initializer.newTimeDate(Initializer.now()-1, "01:00:00") ,eventDate:Initializer.now()).save(failOnError:true)
		then:
		Prevention.count() == 2
	}
}
