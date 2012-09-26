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
package org.chai.memms.maintenance

import org.chai.memms.equipment.Equipment;
import org.chai.memms.maintenance.MaintenanceProcess.ProcessType;
import org.chai.memms.security.User;

/**
 * @author Jean Kahigiso M.
 *
 */
class WorkOrder {
	
    enum Criticality{
		NORMAL("normal"),
		LOW("low"),
		HIGH("high")
		String messageCode = "work.order.criticality"
		String name
		Criticality(String name){this.name=name}
		String getKey(){ return name() }
	}
	
	enum OrderStatus{
		OPEN("open"),
		CLOSEDFIXED("closedfixed"),
		CLOSEDFORDISPOSAL("closedfordisposal")
		String messageCode = "work.order.status"
		String name
		OrderStatus(String name){ this.name=name }
		String getKey() { return name() }
	}
	
	String currency
	String description
	User addedBy
	User lastModifiedBy
	Long estimatedCost
	Date openOn
	Date lastModifiedOn
	Date closedOn
	Boolean assistaceRequested
	
	Criticality criticality
	OrderStatus status
	 
	static belongsTo = [equipment: Equipment]
	static hasMany = [comments: Comment,notifications: Notification,notificationGroup: User,processes: MaintenanceProcess]
	
	static constraints = {
		addedBy nullable: false
		assistaceRequested nullable: false
		description nullable: false, blank: false
		openOn nullable: false, validation:{it <= new Date()}
		lastModifiedOn nullable: true, validation:{ val, obj ->
			if(val!=null)
			return ((val <= new Date()) && (val.after(obj.openOn) || (val.compareTo(obj.openOn)==0)))
			else return true
		}
		closedOn nullable: true, validation:{ val, obj ->
			if(val!=null)
				return ((val <= new Date()) && (val.after(obj.openOn) || (val.compareTo(obj.openOn)==0)))
			else return true
		}
		lastModifiedBy nullable: true
		criticality nullable: false, blank: false, inList: [Criticality.LOW,Criticality.HIGH,Criticality.NORMAL]
		status nullable: false, blank: false, inList:[OrderStatus.OPEN,OrderStatus.CLOSEDFIXED,OrderStatus.CLOSEDFORDISPOSAL]
		estimatedCost nullable: true, blank: true, validator:{ val, obj ->
			if(val == null && obj.currency != null) return false
		}
		currency  nullable: true, blank: true, inList: ["RWF","USD","EUR"], validator:{ val, obj ->
			if(val == null && obj.estimatedCost != null) return false
		}
	}
	
	def getActions(){
		List<MaintenanceProcess> actions = []
		for(MaintenanceProcess process: processes)
			if(process.type.equals(ProcessType.ACTION))
				actions.add(process)
		return actions;
	}
	
	def getMaterials(){
		List<MaintenanceProcess> materials = []
		for(MaintenanceProcess process: processes)
			if(process.type.equals(ProcessType.MATERIAL))
				materials.add(process)
		return materials;
	}
	
	static mapping = {
		table "memms_work_order"
		version false
	}
	
	def getNotificationsForUser(def user){
		notifications.findAll{it.sender == user}
	}
	
	def getUnReadNotificationsForUser(def user){
		getNotificationsForUser(user).findAll{!it.read}
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












