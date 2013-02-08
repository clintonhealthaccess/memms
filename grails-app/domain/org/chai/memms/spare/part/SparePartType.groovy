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

import javax.persistence.Transient;

import groovy.transform.EqualsAndHashCode;
import org.chai.memms.inventory.Provider;
import org.chai.memms.spare.part.SparePartStatus.Status;
import org.chai.memms.spare.part.SparePart;

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
@EqualsAndHashCode(includes="code")
class SparePartType {
	

	String code
	String names
	String partNumber
	String descriptions
	Date discontinuedDate
	Date dateCreated
	Date lastUpdated
	Provider manufacturer
	
	static i18nFields = ["descriptions","names"]
	static hasMany = [spareParts: SparePart]	
	static constraints = {
		code nullable: false, unique :true
		names nullable: true, blank: true
		descriptions nullable: true, blank: true
		partNumber nullable: false
		discontinuedDate nullable: true
		manufacturer nullable: true
	}
	
	static mapping = {
		table "memms_spare_part_type"
		version false
		cache true
	}
	
	@Transient
	def getInStockSpareParts(){
		List<SparePart> inStockSpareParts =  []
		if(!spareParts==null && !spareParts.isEmpty()){
			for(SparePart sparePart: spareParts)
				if(sparePart.usedOnEquipment == null && sparePart.currentStatus.equals(Status.INSTOCK))
					inStockSpareParts.add(sparePart)
		}
		return inStockSpareParts
	}
	
	@Transient
	def getPendingSpareParts(){
		List<SparePart> pendingSpareParts=[]
		if(!spareParts==null && !spareParts.isEmpty()){
			for(SparePart sparePart:spareParts)
			if(sparePart.usedOnEquipment == null && sparePart.currentStatus.equals(Status.PENDINGORDER))
			pendingSpareParts.add(sparePart)
			}
		return pendingSpareParts
	}
	
	@Override
	public String toString() {
		return "SparePartType [code=" + code + ", partNumber=" + partNumber + "]";
	}
	
}
