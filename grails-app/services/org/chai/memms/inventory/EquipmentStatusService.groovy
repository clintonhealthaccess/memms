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
package org.chai.memms.inventory

import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.security.User;
import org.chai.memms.util.Utils;
import org.grails.datastore.mapping.query.api.Criteria;

/**
 * @author Jean Kahigiso M.
 *
 */

class EquipmentStatusService {
	static transactional = true
	
	List<Equipment> getEquipmentStatusByEquipment(Equipment equipment, Map<String,String> params){
		def criteria = EquipmentStatus.createCriteria()
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"dateCreated",order: params.order ?:"desc"){
			eq("equipment",equipment)
		}
	}
	
	Equipment createEquipmentStatus(User changedBy,Status value, Equipment equipment,Date dateOfEvent, String disposalRefNumber, Map<String,String> reasons){
		def status = new EquipmentStatus(dateOfEvent:dateOfEvent,changedBy:changedBy,previousStatus:equipment?.getTimeBasedStatus()?.status,status:value)
		Utils.setLocaleValueInMap(status,reasons,"Reasons")
		equipment.currentStatus = value
		if(equipment.id){
			//When updating an equipment
			equipment.lastModifiedBy = changedBy
		}
		if(value.equals(Status.FORDISPOSAL)){
			status.disposalRefNumber = disposalRefNumber
		} else {
			status.disposalRefNumber = null
		}
		equipment.addToStatus(status)
		
		if(!equipment.currentStatus.equals(Status.DISPOSED))
			equipment.save(failOnError:true, flush:true)
		return equipment
	}
}

