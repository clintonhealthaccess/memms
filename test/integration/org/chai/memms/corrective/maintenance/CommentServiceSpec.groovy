package org.chai.memms.corrective.maintenance

import java.util.Date;

import org.chai.memms.Initializer;
import org.chai.memms.IntegrationTests
import org.chai.memms.corrective.maintenance.Comment;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.security.User;
import org.chai.memms.corrective.maintenance.WorkOrder.Criticality;
import org.chai.memms.corrective.maintenance.WorkOrder.FailureReason;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;

class CommentServiceSpec  extends IntegrationTests{
	def commentService
	def "can create a comment by calling createComment"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		when:
		commentService.createComment(workOrder,user,"Content")
		then:
		Comment.count() == 1
	}
		
	def "can delete a comment by calling removeComment"(){
		setup:
		setupLocationTree()
		setupEquipment()
		def user = newUser("user", "user")
		def equipment = Equipment.findBySerialNumber(CODE(123))
		def workOrder = Initializer.newWorkOrder(equipment, "Nothing yet", Criticality.NORMAL,user, Initializer.now(),FailureReason.NOTSPECIFIED,OrderStatus.OPENATFOSA)
		def createdComment = commentService.createComment(workOrder,user,"Content")
		when:
		def sizeOfCommentBeforeDelete = workOrder.comments.size()
		workOrder = commentService.deleteComment(workOrder.comments.asList()[0],user)
		then:
		Comment.count()==0
		sizeOfCommentBeforeDelete == 1
		workOrder.comments.size() ==0
	}
}
