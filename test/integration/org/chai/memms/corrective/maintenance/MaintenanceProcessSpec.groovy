package org.chai.memms.corrective.maintenance

import java.util.Date;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.corrective.maintenance.CorrectiveProcess;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.CorrectiveProcess.ProcessType;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;

class MaintenanceProcessSpec  extends IntegrationTests{
	def "can create a maintenance process"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		new CorrectiveProcess(name:"name test", addedOn:Initializer.now().previous(), addedBy:user, type:ProcessType.ACTION, workOrder:workOrder).save(failOnError:true)
		then:
		CorrectiveProcess.count() == 1
	}

	def "all required fields needed on maintenance process"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def maintenanceProcessWithErrors = new CorrectiveProcess()
		def expectedFieldErrors = ["name","addedBy","type","workOrder"]

		when://All required fields in
		maintenanceProcessWithErrors.save()
		then:
		maintenanceProcessWithErrors.errors.fieldErrorCount == 4
		expectedFieldErrors.each{maintenanceProcessWithErrors.errors.hasFieldErrors(it)}
		CorrectiveProcess.count() == 0
	}
}
