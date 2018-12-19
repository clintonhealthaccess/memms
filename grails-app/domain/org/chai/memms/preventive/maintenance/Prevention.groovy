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
package org.chai.memms.preventive.maintenance

import groovy.transform.EqualsAndHashCode;
import javax.persistence.Transient;

import javax.persistence.Transient;

import org.chai.memms.TimeSpend;
import org.chai.memms.security.User;
import org.chai.memms.TimeDate;
import org.chai.memms.inventory.PreventiveAction;
import org.chai.memms.corrective.maintenance.UsedSpareParts



/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
@EqualsAndHashCode
public class Prevention {
	
	TimeDate scheduledOn
	Date eventDate
	Date dateCreated
	Date lastUpdated
	TimeSpend timeSpend
	User addedBy

	
	String descriptions
	String actionNotDoneReason
	
	
	static i18nFields = ["descriptions","actionNotDoneReason"]
	static belongsTo = [order:  PreventiveOrder]
	static hasMany = [actions: PreventiveAction]
	static embedded = ["timeSpend","scheduledOn"]
	

	static mapping = {
		table "memms_prevention"
		version false
	}
	
	static constraints = {
		descriptions nullable: true, blank: true
		addedBy nullable: false
		timeSpend nullable: true
		scheduledOn nullable: false
		eventDate nullable: false, validator:{it <= new Date()}
		lastUpdated nullable: true, validator:{if(it != null) return (it <= new Date())}
		actionNotDoneReason nullable: true, blank: true
	}

	def beforeDelete(){
		UsedSpareParts.executeUpdate("delete from UsedSpareParts as sp where sp.maintenanceOrder = "+this.order.id+"")
	}
	
	@Transient
	def getUsedSpareParts(){
		def usedSpareParts = []
		if(UsedSpareParts.findAllByPrevention(this)) usedSpareParts = UsedSpareParts.findAllByPrevention(this)
		//Will send a empty() list in case there is not matching element
		return usedSpareParts;
	}

	@Transient
	def getSparePartTypeUsed(def sparePartType){
		return UsedSpareParts.findByPreventionAndSparePartType(this,sparePartType);
	}
	
	
	@Override
	public String toString() {
		return "Prevention [id="+id+" addedBy=" + addedBy + "]";
	}
	
}
