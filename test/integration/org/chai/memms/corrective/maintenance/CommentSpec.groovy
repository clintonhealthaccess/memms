package org.chai.memms.corrective.maintenance

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.corrective.maintenance.Comment;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;

class CommentSpec  extends IntegrationTests{
	def "can create a comment"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		new Comment(content:"comment test", writtenOn:new Date().previous(), writtenBy:user, workOrder:workOrder).save(failOnError:true)
		then:
		Comment.count() == 1
	}

	def "all required fields needed on a comment"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def commentWithErrors = new Comment()
		def expectedFieldErrors = ["content","writtenBy","workOrder"]

		when://All required fields in
		commentWithErrors.save()
		then:
		commentWithErrors.errors.fieldErrorCount == 3
		expectedFieldErrors.each{commentWithErrors.errors.hasFieldErrors(it)}
		Comment.count() == 0
	}
}
