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

import org.chai.memms.security.User

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

/**
 * @author Jean Kahigiso M.
 *
 */

@i18nfields.I18nFields
class EquipmentStatus {
	
	enum Status{
		
		NONE("none"),
		OPERATIONAL("operational"),
		PARTIALLYOPERATIONAL("partially.operational"),
		INSTOCK("in.stock"),
		UNDERMAINTENANCE("under.maintenance"),
		FORDISPOSAL("for.disposal"),
		DISPOSED("disposed")
		
		String messageCode = "equipment.status"
		
		final String name
		Status(String name){ this.name=name }
		String getKey() { return name() }
	}

	enum EquipmentStatusChange{
		NEWORDER("newEquipment", 
			['previous':[Status.NONE], 'current':(Status.values()-[Status.NONE])]),
		DISPOSEDEQUIPMENT("disposedEquipment", 
			[
				'previous':(Status.values()-[Status.FORDISPOSAL,Status.DISPOSED]), 
				'current':[Status.FORDISPOSAL,Status.DISPOSED]
			]),
		FROMSTOCKTOOPERATIONAL("equipmentFromStockToOperational", 
			['previous':[Status.INSTOCK], 'current':[Status.OPERATIONAL,Status.PARTIALLYOPERATIONAL]]),
		FORMAINTENANCE("equipmentForMaintenance", 
			[
				'previous':(Status.values()-Status.UNDERMAINTENANCE), 
				'current':[Status.UNDERMAINTENANCE]
			])

		String messageCode = "reports.inventory.statusChanges"
		final String name
		final Map<String,List<Status>> statusChange
		EquipmentStatusChange(String name, Map<String,List<Status>> statusChange) {
		   this.name=name
		   this.statusChange=statusChange
		 }
		 String getKey() { return name }
		 Map<String,List<Status>> getStatusChange() { return statusChange }
	}
	
	Date dateOfEvent
	Date dateCreated
	User changedBy
	Status status
	String reasons
	
	static belongsTo = [equipment: Equipment]
	static i18nFields = ["reasons"]
	
	static constraints = {
		dateOfEvent nullable:false, validator:{ val, obj ->
			//TODO be uncomment after first data collection
			return (val <= new Date()) //&&  (val.after(obj.equipment.purchaseDate) || (val.compareTo(obj.equipment.purchaseDate)==0))
		} 
		changedBy nullable: false 
		status blank: false, nullable: false, inList:[Status.OPERATIONAL,Status.PARTIALLYOPERATIONAL,Status.INSTOCK,Status.UNDERMAINTENANCE,Status.FORDISPOSAL,Status.DISPOSED]
		reasons nullable: true, blank: true
	}
	
	static mapping = {
		version false
		table "memms_equipment_status"
	}

	@Override
	public String toString() {
		return "EquipmentStatus [dateOfEvent=" + dateOfEvent + ", changedBy="+ changedBy + ", status=" + status + " dateCreated=" + dateCreated + "]";
	}	
}
