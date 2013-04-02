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

import groovy.transform.EqualsAndHashCode;

import javax.persistence.Transient;

import org.chai.location.DataLocation;
import org.chai.memms.Period;
import org.chai.memms.Warranty;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.Provider;
import org.chai.memms.security.User;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePartStatus;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.lang.math.RandomUtils;
import i18nfields.I18nFields;

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
@EqualsAndHashCode(includes="code")
public class SparePart {
	
	enum StockLocation{
		
		NONE('none'),
		MMC("mmc"),
		FACILITY("facility")
		
		String messageCode = "stock.location"
		final String name
		StockLocation(String name){ this.name=name }
		String getKey() { return name() }
	}
	enum SparePartPurchasedBy{
		
		NONE('none'),
		BYMOH("by.moh"),
		BYMMC("by.mmc"),
		BYFACILITY("by.facility"),
		BYPARTNER("by.partner"),
		
		String messageCode = "spare.part.purchased"

		final String name
		SparePartPurchasedBy(String name){ this.name=name }
		String getKey() { return name() }
		
	}
	
	String code
	String names
	String descriptions
	String currency
	String serialNumber
	String model
	Double purchaseCost
	Period warrantyPeriod
	Period expectedLifeTime
	Equipment usedOnEquipment
	Boolean sameAsManufacturer = false
	Provider supplier
	Warranty warranty
	SparePartPurchasedBy sparePartPurchasedBy
	DataLocation dataLocation
	Date purchaseDate
	Date dateCreated
	Date lastUpdated
	Date manufactureDate
	StatusOfSparePart statusOfSparePart
	User addedBy
	User lastModified

	StockLocation stockLocation
	String room
	String shelve

	static belongsTo = [type: SparePartType]

	
	static hasMany = [status: SparePartStatus]	
	static i18nFields = ["descriptions","names"]
	static embedded = ["warranty","warrantyPeriod","expectedLifeTime"]
	
	static constraints = {
		code nullable: false, unique :true
		names nullable: true, blank: true
		room nullable: true, blank: true
		shelve nullable: true, blank: true
		descriptions nullable: true, blank: true
		serialNumber nullable: true, unique: true, validator: { val, obj ->
			if(!obj.statusOfSparePart.equals(StatusOfSparePart.PENDINGORDER)) return (val!=null)
		}
		purchaseDate nullable: true
		purchaseCost nullable: true, validator: {val, obj ->
		if(obj.currency!=null) return (val!=null)
		}		
		stockLocation  nullable: true,validator:{
			if(it!=null) it in [StockLocation.MMC, StockLocation.FACILITY]
		}
		type nullable: false
		
		dataLocation nullable: true, validator: {val,obj ->
			if(obj.stockLocation.equals(StockLocation.FACILITY)) return (val!=null)
		}
		addedBy nullable: false
		usedOnEquipment nullable: true, validator:{val,obj ->
			if(obj.statusOfSparePart.equals(StatusOfSparePart.OPERATIONAL)) return (val!=null)
		}
		lastModified nullable:true, validator:{ val, obj ->
			if (val != null) return (obj.lastUpdated != null)
		}	
		warranty nullable:true, validator:{ val, obj ->
			if(val!=null) return (val.startDate.after(obj.purchaseDate) || val.startDate.compareTo(obj.purchaseDate)==0)
		}
		warrantyPeriod nullable: true, validator:{val, obj ->
			if (obj.warranty!=null) return (val!=null) && (val.numberOfMonths >= 0)
		}
		sparePartPurchasedBy nullable: false, inList:[SparePartPurchasedBy.BYFACILITY,SparePartPurchasedBy.BYMOH, SparePartPurchasedBy.BYMMC, SparePartPurchasedBy.BYPARTNER]
		
		currency  nullable: true, blank: true, inList: ["RWF","USD","EUR"], validator:{ val, obj ->
			if(obj.purchaseCost != null) return (val != null)
		}
		statusOfSparePart nullable:true,validator:{
			if(it!=null) return it in [StatusOfSparePart.OPERATIONAL,StatusOfSparePart.INSTOCK,StatusOfSparePart.PENDINGORDER,StatusOfSparePart.DISPOSED]
		}
		model nullable: true
		supplier nullable: true
		expectedLifeTime nullable: true, validator:{
			if(it==null) return true
			else return (it.numberOfMonths >= 0)
		}
		manufactureDate nullable: true, blank: false, validator:{it <= new Date()}
		
	}
	
	static mapping = {
		table "memms_spare_part"
		version false
		cache true
	}
	
	@Transient
	def getTimeBasedStatus(){
		if(!status) return null
		SparePartStatus currentState = status.asList()[0]
		for(SparePartStatus state : status){
			currentState.dateOfEvent.clearTime()
			state.dateOfEvent.clearTime()
			if(state.dateOfEvent.after(currentState.dateOfEvent)){
				currentState= state;
			}
			if(state.dateOfEvent.compareTo(currentState.dateOfEvent)==0){
				if(state.dateCreated.after(currentState.dateCreated))
					currentState = state
				if(state.dateCreated.compareTo(currentState.dateCreated)==0)
					currentState = (currentState.id > state.id)?currentState:state
			}
			
		}
		return currentState
	}
	def beforeUpdate(){
		lastUpdated = new Date()
	}
	
	def beforeValidate(){
		this.getGenerateAndSetCode()
	}
	
	@Transient
	def getGenerateAndSetCode(){
		if(!code){
			def randomInt = RandomUtils.nextInt(99999)
			def now = new Date()
			def sparePartCode = "${randomInt}-${now.month+1}-${now.year+1900}"
			if(log.isDebugEnabled()) log.debug("Generated code:" + sparePartCode)
			if(SparePart.findByCode(sparePartCode.toString()) == null) code = sparePartCode
			else getGenerateAndSetCode()
		}
	}
	
	String toString() {
		return "SparePart [id= " + id + " code= "+code+" serialNumber= "+serialNumber+" currentState= "+statusOfSparePart+"]";

	}
}
