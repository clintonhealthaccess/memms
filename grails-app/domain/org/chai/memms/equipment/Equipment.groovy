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
package org.chai.memms.equipment

import org.chai.memms.Contact
import org.chai.memms.Warranty;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.location.DataLocation;

import i18nfields.I18nFields

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
public class Equipment {
	
	String serialNumber
	String purchaseCost
	String currency
	String descriptions
	String model
	String room
	
	Integer expectedLifeTime
	Boolean donation
	Boolean obsolete
	
	Provider manufacturer
	Provider supplier
	Warranty warranty
	
	Date manufactureDate
	Date purchaseDate
	Date registeredOn
	
	static hasMany = [status: EquipmentStatus]
	static belongsTo = [dataLocation: DataLocation, department: Department, type: EquipmentType]
	static i18nFields = ["observations","descriptions"]
	static embedded = ["warranty"]
	
	static constraints = {
		importFrom Contact
		supplier nullable: false
		manufacturer nullable: false
		warranty nullable: true
		
		serialNumber nullable: false, blank: false,  unique: true
		purchaseCost nullable: true, blank: true, validator:{ val, obj ->
			if(val == null && obj.donation == null) return false
			if(obj.donation == true) return ((val == null) && (obj.currency==null))
		}
		currency  nullable: true, blank: true, inList: ["RWF","USD","EUR"], validator:{ val, obj ->
			if(val == null && obj.donation == null) return false
		}
		expectedLifeTime nullable: false, blank: false
		room nullable: true, blank: true
		
		manufactureDate nullable: false, blank: false, validator:{it <= new Date()}
		purchaseDate nullable: false, blank: false, validator:{ val, obj ->
			return  ((val <= new Date()) && val.after(obj.manufactureDate) || (val.compareTo(obj.manufactureDate)==0))
			}
		registeredOn nullable: false, blank:false
		descriptions nullable: true, blank: true
		donation nullable: false
		obsolete nullable: false
	}
	
	static mapping = {
		table "memms_equipment"
		version false
		descriptions_en type: 'text'
		descriptions_fr type: 'text'
		
	}
	
	def getCurrentState() {
		if(status){
			for(EquipmentStatus state : status)
				if(state.isCurrent().is(getCurrentStatusBasedOnTime())) return state 
		 setCurrentStatus()
		 return getCurrentStatusBasedOnTime();
		}
		return null
	}
	def setCurrentStatus(){
		if(status){
			def state = getCurrentStatusBasedOnTime()
			status.each{ stat ->
				if(!stat.is(state)){
					stat.current = false
					stat.save(flush:true)
				}
			}
			state.current = true
			state.save(flush:true)
		}
	}

	def getCurrentStatusBasedOnTime(){
		EquipmentStatus currentStatus = status.asList()[0]
		for(EquipmentStatus state : status){
			if(state.dateOfEvent.after(currentStatus.dateOfEvent)){
				currentStatus= state;
			}else if(state.dateOfEvent.compareTo(currentStatus.dateOfEvent)==0){
				if(state.statusChangeDate.after(currentStatus.statusChangeDate))
					currentStatus = state
			}
		}
		return currentStatus
	}
	
	
	String toString() {
		return "Equipment[id=" + id + "]";
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
		Equipment other = (Equipment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}
