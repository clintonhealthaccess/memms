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
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.corrective.maintenance.MaintenanceProcess.ProcessType;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.security.User;

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
public class WorkOrder {

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
	String description
	String failureReasonDetails
	String testResultsDescriptions
	String returnedTo
	
	User addedBy
	User lastModifiedBy
	User receivedBy
	User fixedBy
	
	Date openOn
	Date lastModifiedOn
	Date closedOn
	Date returnedOn
	
	Double estimatedCost
	Integer workTime
	Integer travelTime
	
	OrderStatus currentStatus
	Criticality criticality
	FailureReason failureReason
	
	static i18nFields = ["failureReasonDetails","testResultsDescriptions"]
	static belongsTo = [equipment: Equipment]
	static hasMany = [status: WorkOrderStatus, comments: Comment, notifications: NotificationWorkOrder, processes: MaintenanceProcess]
	

	static constraints = {
		addedBy nullable: false
		receivedBy nullable: true
		fixedBy nullable: true
		
		failureReasonDetails nullable: true
		testResultsDescriptions nullable: true
		workTime nullable: true, validator:{ 
			if(it!=null && it<=0) return false		
		}
		travelTime nullable: true, validator:{ 
			if(it!=null && it<=0) return false		
		}
		description nullable: false, blank: false
		returnedTo nullable: true, blank: true
		
		openOn nullable: false, validator:{it <= new Date()}
		returnedOn nullable: true, validator:{it <= new Date()}
		
		lastModifiedOn nullable: true, validator:{ val, obj ->
			if(val!=null)
				return ((val <= new Date()) && (val.after(obj.openOn) || (val.compareTo(obj.openOn)==0)))
			else return true
		}
		
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
		lastModifiedBy nullable: true
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
		List<MaintenanceProcess> actions = []
		for(MaintenanceProcess process: processes)
			if(process.type.equals(ProcessType.ACTION))
				actions.add(process)
		return actions;
	}
	@Transient
	def getMaterials(){
		List<MaintenanceProcess> materials = []
		for(MaintenanceProcess process: processes)
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
			if(state.changeOn.after(currentState.changeOn))
				currentState= state;
		return currentState
	}


	static mapping = {
		table "memms_work_order"
		version false
		description type:"text"
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
		return "WorkOrder [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkOrder other = (WorkOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
