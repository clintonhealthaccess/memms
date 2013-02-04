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

class PreventiveProcessSpec extends IntegrationTests {
	def "can create and save a preventive process"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def techInCharge = newOtherUserWithType("techInCharge", "techInCharge", DataLocation.findByCode(BUTARO),UserType.TECHNICIANDH)
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def durationBasedOrder = Initializer.newDurationBasedOrder(equipment,addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description",
			Initializer.now()+1,null,OccurencyType.DAILY,1,null)
		def workBasedOrder = Initializer.newWorkBasedOrder( equipment, addedBy,PreventiveOrderStatus.OPEN,PreventionResponsible.SERVICEPROVIDER,null,["en":"test"],"description",
			Initializer.now()+1,null,WorkIntervalType.NONE,1)
		def preventionDurationBased = Initializer.newPrevention(durationBasedOrder,addedBy,Initializer.newTimeDate(Initializer.now()-1, "01:00:00"),Initializer.now(),null,["en":"descriptions"], null)
		def preventionWorkBased = Initializer.newPrevention(workBasedOrder,addedBy,Initializer.newTimeDate(Initializer.now()-1, "01:00:00"),Initializer.now(),null,["en":"descriptions"], null)
		when:
		def preventiveProcessDurationBased = new PreventiveProcess(dateCreated:Initializer.now(),name:"test date",addedBy:addedBy,prevention:preventionDurationBased).save(failOnError:true)
		def preventiveProcessWorkBased = new PreventiveProcess(dateCreated:Initializer.now(),name:"test date",addedBy:addedBy,prevention:preventionWorkBased).save(failOnError:true)
		then:
		PreventiveProcess.count() == 2
	}
}
