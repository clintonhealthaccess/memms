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
package org.chai.memms.spare.part

import java.util.Date;

import org.chai.memms.security.User;
import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields;

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
class SparePartStatus {
	
	enum StatusOfSparePart{
		
		NONE("none"),
		INSTOCK("in.stock"),
		OPERATIONAL("operational"),
		PENDINGORDER("pending.order"),
		DISPOSED("disposed")
		
		String messageCode = "spare.part.status"
		
		final String name
		StatusOfSparePart(String name){ this.name=name }
		String getKey() { return name() }
	}

	enum StatusOfSparePartChange{
		NEWPENDINGORDER("newPendingOrder",
			['previous':[StatusOfSparePart.NONE], 'current':[StatusOfSparePart.PENDINGORDER]], true),
		PENDINGORDERARRIVED("pendingOrderArrived",
			['previous':[StatusOfSparePart.PENDINGORDER], 'current':[StatusOfSparePart.INSTOCK]], false),
		SPAREPARTSASSOCIATEDTOEQUIPMENT("sparePartsAssociatedToEquipment",
			[
				'previous':(StatusOfSparePart.values()-[StatusOfSparePart.OPERATIONAL,StatusOfSparePart.DISPOSED]), 
				'current':[StatusOfSparePart.OPERATIONAL]
			], true),
		DISPOSEDSPAREPARTS("disposedSpareParts",
			[
				'previous':(StatusOfSparePart.values()-StatusOfSparePart.DISPOSED), 
				'current':[StatusOfSparePart.OPERATIONAL]
			], true)

		String messageCode = "reports.spareParts.statusChanges"
		final String name
		final Map<String,List<StatusOfSparePart>> statusChange
		final String performed
		StatusOfSparePartChange(String name, Map<String,List<StatusOfSparePart>> statusChange, Boolean performed) {
		   this.name=name
		   this.statusChange=statusChange
		   this.performed=performed
		 }
		 String getKey() { return name }
		 Map<String,List<StatusOfSparePart>> getStatusChange() { return statusChange }
		 Boolean getPerformed() { return performed }
	}

	Date dateOfEvent
	Date dateCreated
	User changedBy
	StatusOfSparePart statusOfSparePart
	String reasons
	
	static belongsTo = [sparePart: SparePart]
	static i18nFields = ["reasons"]
	
	static constraints = {
		dateOfEvent nullable:false, validator:{ val, obj ->
			return (val <= new Date()) &&  (val.after(obj.sparePart.purchaseDate) || (val.compareTo(obj.sparePart.purchaseDate)==0))
		}
		changedBy nullable: false
		statusOfSparePart blank: false, nullable: false, inList:[StatusOfSparePart.OPERATIONAL,StatusOfSparePart.INSTOCK,StatusOfSparePart.PENDINGORDER, StatusOfSparePart.DISPOSED]
		reasons nullable: true, blank: true
	}
	
	static mapping = {
		version false
		table "memms_spare_part_status"
	}
	
	@Override
	public String toString() {
		return "SparePartStatus [dateOfEvent=" + dateOfEvent + ", changedBy="+ changedBy + ", statusOfSparePart=" + statusOfSparePart + " dateCreated=" + dateCreated + "]";
	}	

}
