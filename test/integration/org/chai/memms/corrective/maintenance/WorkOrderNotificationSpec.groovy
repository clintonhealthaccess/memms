package org.chai.memms.corrective.maintenance

import java.util.Date;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.corrective.maintenance.NotificationWorkOrder;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;

class WorkOrderNotificationSpec  extends IntegrationTests{
	def "can create a notification"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("user", "user")
		def receiver = newUser("receiver", "receiver")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		new NotificationWorkOrder(sender:sender, receiver:receiver, writtenOn: new Date(), content:" check this out",read:true,workOrder:workOrder).save(failOnError:true)
		then:
		NotificationWorkOrder.count() == 1
	}

	def "all required fields needed on notification"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def sender = newUser("user", "user")
		def receiver = newUser("receiver", "receiver")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,sender, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def notificationWithErrors = new NotificationWorkOrder()
		def expectedFieldErrors = ["sender","receiver","writtenOn","content","workOrder"]

		when://All required fields in
		notificationWithErrors.save()
		then:
		notificationWithErrors.errors.fieldErrorCount == 5
		expectedFieldErrors.each{notificationWithErrors.errors.hasFieldErrors(it)}
		NotificationWorkOrder.count() == 0

		when://notification date should be before or equal to  today
		notificationWithErrors = new NotificationWorkOrder(sender:sender, receiver:receiver, writtenOn: Initializer.now()+1, content:" check this out",read:true,workOrder:workOrder)
		notificationWithErrors.save()
		then:
		notificationWithErrors.errors.hasFieldErrors("writtenOn") == true
		NotificationWorkOrder.count() == 0
	}
}