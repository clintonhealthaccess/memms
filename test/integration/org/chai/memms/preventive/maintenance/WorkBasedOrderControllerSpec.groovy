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

class WorkBasedOrderControllerSpec extends IntegrationTests {
	def workBasedOrderController
	def "can create and save a workBased based order"() {
		setup:
		setupLocationTree()
		setupEquipment()
		def addedBy = newUser("addedBy", CODE(123))
		def equipment = Equipment.findBySerialNumber(CODE(123))
		setupSecurityManager(newSystemUser("user", "user", DataLocation.findByCode(BUTARO)))
		workBasedOrderController = new WorkBasedOrderController()
		when:
		workBasedOrderController.params.equipment = equipment
		workBasedOrderController.params.addedBy = addedBy
		workBasedOrderController.params.type = PreventiveOrderType.DURATIONBASED
		workBasedOrderController.params.status = PreventiveOrderStatus.CLOSED
		workBasedOrderController.params.description = "test"
		workBasedOrderController.params.preventionResponsible = PreventionResponsible.SERVICEPROVIDER
		workBasedOrderController.params.firstOccurenceOn = Initializer.newTimeDate(Initializer.now(),"24:60:60")
		workBasedOrderController.params.occurency = WorkIntervalType.NONE
		workBasedOrderController.save()
		then:
		WorkBasedOrder.count() == 1
	}
}
