/**
 * Copyright (c) 2012, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.memms.corrective.maintenance

import javax.persistence.Transient;

import org.chai.memms.Notification;
import org.chai.memms.TimeSpend;
import org.chai.memms.corrective.maintenance.CorrectiveProcess.ProcessType;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.maintenance.MaintenanceOrder;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.security.User;
import org.chai.memms.spare.part.SparePart

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
public class WorkOrder extends MaintenanceOrder{

	enum Criticality{
		NONE("none"),
		NORMAL("normal"),
		LOW("low"),
		HIGH("high")
		String messageCode = "work.order.criticality"
		String name
		Criticality(String name){this.name=name}
		String getKey(){ return name() }
	}
	enum FailureReason{
		NOTSPECIFIED("not.specified"),
		MISUSE("misuse"),
		SPAREPARTBROKEN("spare.part.broken"),
		OTHER("other")
		String messageCode = "work.failure.reason"
		String name
		FailureReason(String name){ this.name=name }
		String getKey() { return name() }
	}

	String currency
	String failureReasonDetails
	String testResultsDescriptions
	String returnedTo
	
	
	User receivedBy
	User fixedBy
	
	Date openOn
	Date returnedOn
	
	Double estimatedCost
	TimeSpend workTime
	TimeSpend travelTime
	
	OrderStatus currentStatus
	Criticality criticality
	FailureReason failureReason
	Map<SparePart,Integer> usedSpareParts =  [:]
	
	
	static belongsTo = [equipment: Equipment]
	static i18nFields = ["failureReasonDetails","testResultsDescriptions"]
	static hasMany = [status: WorkOrderStatus, comments: Comment, notifications: NotificationWorkOrder, processes: CorrectiveProcess]
	static embedded = ["workTime","travelTime"]

	static constraints = {
		importFrom MaintenanceOrder, exclude:["closedOn","lastUpdated"]
		
		lastUpdated nullable: true, validator:{ val, obj ->
			if(val!=null) return ((val <= new Date()) && (val.after(obj.openOn) || (val.compareTo(obj.openOn)==0)))
		}
		
		openOn nullable: false, validator:{it <= new Date()}
		receivedBy nullable: true
		fixedBy nullable: true
		
		failureReasonDetails nullable: true
		testResultsDescriptions nullable: true
		workTime nullable: true, validator:{ 
			if(it!=null) return (it.numberOfMinutes > 0) 	
		}
		travelTime nullable: true, validator:{ 
			if(it!=null) return (it.numberOfMinutes > 0)	
		}
		description nullable: false, blank: false
		returnedTo nullable: true, blank: true
		
		returnedOn nullable: true, validator:{it <= new Date()}
		
		closedOn nullable: true, validator:{ val, obj ->
			boolean valid = true
			if(val==null){
				if(obj.currentStatus==OrderStatus.CLOSEDFORDISPOSAL || obj.currentStatus==OrderStatus.CLOSEDFIXED)
					valid = false
			}else{
				if(obj.currentStatus!=OrderStatus.CLOSEDFORDISPOSAL && obj.currentStatus!=OrderStatus.CLOSEDFIXED)
					valid = false
				if((val > new Date()) || val.before(obj.openOn) )
				 	valid = false
			}
			return valid	
		}
		
		currentStatus nullable: false, inList:[OrderStatus.OPENATFOSA,OrderStatus.OPENATMMC,OrderStatus.CLOSEDFIXED,OrderStatus.CLOSEDFORDISPOSAL]
		criticality nullable: false, blank: false, inList: [Criticality.LOW,Criticality.HIGH,Criticality.NORMAL]
		failureReason nullable:false, blank:false, inList:[FailureReason.NOTSPECIFIED,FailureReason.SPAREPARTBROKEN,FailureReason.MISUSE,FailureReason.OTHER]
		
		estimatedCost nullable: true, blank: true, validator:{ val, obj ->
			if(val == null && obj.currency != null) return false
			if(val!=null && val<=0) return false
		}
		currency  nullable: true, blank: true, inList: ["RWF","USD","EUR"], validator:{ val, obj ->
			if(val == null && obj.estimatedCost != null) return false
		}
		
	}
	
	@Transient
	def getActions(){
		List<CorrectiveProcess> actions = []
		for(CorrectiveProcess process: processes)
			if(process.type.equals(ProcessType.ACTION))
				actions.add(process)
		return actions;
	}
	@Transient
	def getMaterials(){
		List<CorrectiveProcess> materials = []
		for(CorrectiveProcess process: processes)
			if(process.type.equals(ProcessType.MATERIAL))
				materials.add(process)
		return materials;
	}
	//Return true if this order is currently escalated
	@Transient
	def getEscalated(){
		if(currentStatus.equals(OrderStatus.OPENATMMC))
			return true
		return false
	}
		
	@Transient
	def getTimeBasedStatus(){
		WorkOrderStatus currentState = status.asList()[0]
		for(WorkOrderStatus state : status)
			if(state.dateCreated.after(currentState.dateCreated))
				currentState= state;
		return currentState
	}


	static mapping = {
		table "memms_work_order"
		version false
	}
	@Transient
	def getNotificationsReceivedByUser(def user){
		return notifications.findAll{it.receiver == user}
	}
	@Transient
	def getNotificationsSentByUser(def user){
		return notifications.findAll{it.sender == user}
	}
	@Transient
	def getUnReadNotificationsForUser(def user){
		return getNotificationsReceivedByUser(user).findAll{!it.read}
	}

	@Override
	public String toString() {
		return "WorkOrder [id= " + id + " currentStatus= "+currentStatus+"]";
	}

}
