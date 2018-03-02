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

import javax.persistence.Transient;

import org.chai.memms.Contact;
import org.chai.memms.Period;
import org.chai.memms.Warranty;
import org.chai.memms.inventory.Provider;
import org.chai.memms.inventory.Department;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentStatus.EquipmentStatusChange;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.preventive.maintenance.PreventiveOrder;
import org.chai.memms.security.User;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.location.DataLocation;
import org.joda.time.DateTime;
import org.joda.time.Days;

import org.apache.commons.lang.math.RandomUtils;
import groovy.time.TimeCategory;

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields
import java.util.Date

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
@EqualsAndHashCode(includes="code")
public class Equipment {
	
	enum PurchasedBy{
		
		NONE('none'),
		BYMOH("by.moh"),
		BYFACILITY("by.facility"),
		BYDONOR("by.donor"),
		BYSPIU("by.spiu"),
		BYRBC("by.rbc")
		
		String messageCode = "equipment.purchased"
		
		final String name
		PurchasedBy(String name){ this.name=name }
		String getKey() { return name() }
		
	}
	
	enum Donor{
		
		NONE('none'),
		MOHPARTNER("moh.partner"),
		OTHERNGO("other.ngo"),
		INDIVIDUAL("individual"),
		OTHERS("others")
		
		String messageCode = "equipment.donor"
		
		final String name
		Donor(String name){ this.name=name }
		String getKey() { return name() }
		
	}
	
	String serialNumber
	String oldTagNumber
	String currency
	String descriptions
	String model
	String room
	String code
	String donorName
	Double purchaseCost
	
	Period expectedLifeTime
	Period serviceContractPeriod
	Period warrantyPeriod
	Boolean obsolete
	
	Provider manufacturer
	Provider supplier
	Provider serviceProvider
	Warranty warranty
	
	Status currentStatus
	PurchasedBy purchaser
	Donor donor
	
	Date manufactureDate
	Date purchaseDate
	Date serviceContractStartDate
	Date dateCreated
	Date lastUpdated
	Date warrantyEndDate
	
	User addedBy
	User lastModifiedBy
	Date installationDate
	
	
	static hasMany = [status: EquipmentStatus, workOrders: WorkOrder,preventiveOrders: PreventiveOrder]

	static belongsTo = [dataLocation: DataLocation, department: Department, type: EquipmentType]
	static i18nFields = ["observations","descriptions"]
	static embedded = ["warranty","serviceContractPeriod","expectedLifeTime","warrantyPeriod"]
	
	static constraints = {
		importFrom Warranty, exclude:["startDate"]
		code nullable: false, blank:false, unique:true
		addedBy nullable: false
		
		lastModifiedBy nullable:true, validator:{ val, obj ->
			if (val != null) return (obj.lastUpdated != null) 
		}
		lastUpdated nullable:true, validator:{ val, obj ->
			if(val != null) return (obj.lastUpdated!=null && (obj.lastUpdated.after(obj.dateCreated) || obj.lastUpdated.compareTo(obj.dateCreated)==0))
		}
		model nullable: true
		currentStatus nullable:true,validator:{
			if(it!=null) return it in [Status.OPERATIONAL,Status.PARTIALLYOPERATIONAL,Status.INSTOCK,Status.UNDERMAINTENANCE,Status.FORDISPOSAL,Status.DISPOSED]
		}
		//TODO nullable has to be false, but it is true for first iteration
		supplier nullable: true
		//TODO nullable has to be false, but it is true for first iteration
		manufacturer nullable: true
		serviceProvider nullable: true, validator:{val, obj ->
			if(val == null) return (obj.serviceContractStartDate==null && obj.serviceContractPeriod==null)
		}

		warranty nullable:true, validator:{ val, obj ->
			if(val!=null && val.startDate != null && obj.purchaseDate != null) return (val.startDate?.after(obj.purchaseDate) || val.startDate?.compareTo(obj.purchaseDate)==0)
		}
		warrantyEndDate nullable: true
		warrantyPeriod nullable: true, validator:{val, obj ->
			if (obj.warranty!=null) return (val!=null) && (val.numberOfMonths >= 0)
		}

		//TODO nullable has to be false, but it is true for first iteration
		serialNumber nullable: true,  unique: true ///blank: false 
		
		oldTagNumber nullable: true

		purchaseCost nullable: true, blank: true, validator:{ if(it!=null) return (it>0) }
		//TODO nullable has to be false, but it is true for first iteration
		//The value none have to be removed from valid answer
		purchaser nullable: false, inList:[PurchasedBy.NONE,PurchasedBy.BYFACILITY,PurchasedBy.BYMOH,PurchasedBy.BYDONOR,PurchasedBy.BYSPIU,PurchasedBy.BYRBC]
		donor nullable:true,inList:[Donor.OTHERNGO,Donor.MOHPARTNER,Donor.OTHERS,Donor.INDIVIDUAL], validator:{ val, obj ->
			if(obj.purchaser == PurchasedBy.BYDONOR) return (val!=null)
		}
		donorName nullable:true,blank:true, validator:{val, obj ->
			if(obj.purchaser == PurchasedBy.BYDONOR || obj.donor !=null) return (val!=null && val!="")
		}
		currency  nullable: true, blank: true, inList: ["RWF","USD","EUR","GBP"], validator:{ val, obj ->
			if(obj.purchaseCost != null) return (val != null)
		}

		//TODO nullable has to be false, but it is true for first iteration
		expectedLifeTime nullable: true, validator:{
			if(it==null) return true 
			else return (it.numberOfMonths >= 0)
		}
		serviceContractPeriod nullable: true, validator:{ val, obj ->
			if(val==null) return (obj.serviceContractStartDate == null && obj.serviceProvider == null)
			if(val!=null) return (val.numberOfMonths >= 0)
		}
		serviceContractStartDate nullable: true, blank: true, validator:{ val, obj ->
			if(val!=null) return (val<=new Date() && (val.after(obj.purchaseDate) || (val.compareTo(obj.purchaseDate)==0)))
			if(val==null) return (obj.serviceContractPeriod==null && obj.serviceProvider==null)
		}
		room nullable: true, blank: true
		//TODO nullable has to be false, but it is true for first iteration
		manufactureDate nullable: true, blank: false, validator:{it <= new Date()}
		//TODO nullable has to be false, but it is true for first iteration
		purchaseDate (nullable: true, blank: false, validator:{ val, obj ->
			if (val!=null && obj.manufactureDate !=null){
				return  val.compareTo(obj.manufactureDate)>=0
			}
			return true
		})
		
		descriptions nullable: true, blank: true
		obsolete nullable: false
		type nullable: false 
		installationDate nullable: true
	}
	
	static mapping = {
		table "memms_equipment"
		version false
		cache true
	}

	def beforeUpdate(){
		lastUpdated = new Date()
	}
	
	def beforeValidate(){
		this.genarateAndSetEquipmentCode()
		this.generateWarrantyEndDate()
	}

	@Transient
	def getCurrentValueOfThisEquipment(){
		def today=new Date()
		DateTime todayDateTime = new DateTime(today)
		
		if(id!=null && expectedLifeTime!=null && purchaseDate!=null && purchaseCost!=null){	
			DateTime purchaseDateTime = new DateTime(purchaseDate)
			Days d = Days.daysBetween(purchaseDateTime, todayDateTime)
			int days = d.getDays()
			//C-((B/A)*C)
		 return purchaseCost-((days/purchaseCost)*((expectedLifeTime.numberOfMonths)*30))
		}else 
			return null
	}
	
	@Transient
	def generateWarrantyEndDate(){
		use(TimeCategory){
		if(warranty!=null && warrantyPeriod!=null)
			warrantyEndDate = warranty.startDate + (warrantyPeriod.numberOfMonths).months
		else
			warrantyEndDate = null
		}
	}
	
	@Transient
	def getWarrantyPeriodRemaining(){
		def today=new Date()
		DateTime todayDateTime = new DateTime(today)
		if(warrantyEndDate!=null){
			DateTime warrantyEndDateTime = new DateTime(warrantyEndDate)
			Days d = Days.daysBetween(warrantyEndDateTime, todayDateTime)
			int days = d.getDays()
			int months =days/30
			//org.joda.time.Period period = new Period(todayDateTime, warrantyEndDateTime)
			return months
		}else {
		return null
		}	
	}
	
	@Transient
	def genarateAndSetEquipmentCode() {
		if(!code){
			def randomInt = RandomUtils.nextInt(99999)
			def now = new Date()
			def equipmentCode = "${dataLocation.code}-${randomInt}-${now.month+1}-${now.year+1900}"
			if(log.isDebugEnabled()) log.debug("Generated code:" + equipmentCode)
			if(Equipment.findByCode(equipmentCode.toString()) == null) code = equipmentCode 
			else genarateAndSetEquipmentCode()
		}
	}
	
	@Transient
	def getTimeBasedStatus(){
		EquipmentStatus currentState = null
		if(!status) return currentState
		else if(status.size() == 0) return currentState
		else{
			//only compare date not time
			status.each{ it -> it.dateOfEvent.clearTime() }
			//first check the date of event, if date of event is the same, check the date created, if date created is the same, check the id
			List<EquipmentStatus> sortedStatus = status.sort{ a,b -> (a.dateOfEvent <=> b.dateOfEvent) ?: (a.dateCreated <=> b.dateCreated) ?: (a.id <=> b.id) }
			currentState = sortedStatus[-1]
			if(currentState != currentStatus) 
				currentStatus = currentState.status
		}
		return currentState
	}
	
	@Transient
	def getTimeBasedPreviousStatus(){
		EquipmentStatus previousState = null
		if(!status) return previousState
		else if(status.size() == 0) return previousState
		else if(status.size() == 1) return previousState
		else{
			//only compare date not time
			status.each{ it -> it.dateOfEvent.clearTime() }
			//first check the date of event, if date of event is the same, check the date created, if date created is the same, check the id
			List<EquipmentStatus> sortedStatus = status.sort{ a,b -> (a.dateOfEvent <=> b.dateOfEvent) ?: (a.dateCreated <=> b.dateCreated) ?: (a.id <=> b.id) }
			previousState = sortedStatus[-2]
		}
		return previousState
	}

	String toString() {
		return "Equipment[id= " + id + " code= "+code+" dataLocationId= "+dataLocation.id+" serialNumber= "+serialNumber+" currentState= "+currentStatus+", previousState= "+getTimeBasedPreviousStatus()?.status+"]";

	}
}
