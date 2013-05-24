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

import org.chai.memms.inventory.Equipment;
import org.chai.memms.maintenance.MaintenanceOrder;
import org.chai.memms.security.User;
import groovy.transform.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.chai.memms.TimeDate

/**
 * @author Jean Kahigiso M.
 *
 */
@i18nfields.I18nFields
public abstract class PreventiveOrder extends MaintenanceOrder {
	
	// fterrier: why this if there is a class to distinguish them?
	enum PreventiveOrderType{
		
		NONE("none"),
		DURATIONBASED("duration.based"),
		WORKBASED("work.based")
		
		String messageCode = "preventive.order.type"
		String name
		PreventiveOrderType(String name){this.name=name}
		String getKey(){ return name() }
	}
	
	enum PreventiveOrderStatus{
		
		OPEN("open"),
		CLOSED("closed")
		
		String messageCode = "preventive.order.status"
		String name
		PreventiveOrderStatus(String name){this.name=name}
		String getKey(){ return name() }
	}

	enum PreventiveOrderStatusChange{
		PREVENTIVEMAINTENANCEPERFORMED("preventiveMaintenancePerformed",
			['previous':[PreventiveOrderStatus.OPEN], 'current':[PreventiveOrderStatus.OPEN]], true),
		PREVENTIVEMAINTENANCENOTPERFORMED("preventiveMaintenanceNotPerformed",
			['previous':[PreventiveOrderStatus.OPEN], 'current':[PreventiveOrderStatus.OPEN]], false),
		CLOSEDSCHEDULEDPREVENTIVEMAINTENANCE("closedScheduledPreventiveMaintenance",
			['previous':[PreventiveOrderStatus.OPEN], 'current':[PreventiveOrderStatus.CLOSED]], null)

		String messageCode = "reports.preventive.statusChanges"
		final String name
		final Map<String,List<PreventiveOrderStatus>> statusChange
		final Boolean performed
		PreventiveOrderStatusChange(String name, Map<String,List<PreventiveOrderStatus>> statusChange, Boolean performed) {
		   this.name=name
		   this.statusChange=statusChange
		   this.performed=performed
		 }
		 String getKey() { return name }
		 Map<String,List<PreventiveOrderStatus>> getStatusChange() { return statusChange }
		 Boolean getPerformed() { return performed }
	}
	
	enum PreventionResponsible{
		
		NONE("none"),
		SERVICEPROVIDER("service.provider"),
		DEPARTMENTUSER("department.user"),
		HCTITULAIRE("hc.titulaire"),
		HCTECHNICIAN("hc.technician")
		
		String messageCode = "prevention.in.charge"
		String name
		PreventionResponsible(String name){this.name=name}
		String getKey(){ return name() }
		
	}
	String names
	PreventionResponsible  preventionResponsible
	
	User technicianInCharge
	PreventiveOrderType type
	PreventiveOrderStatus status
	TimeDate firstOccurenceOn
	Integer occurInterval = 1

	
	static belongsTo = [equipment: Equipment]
	static hasMany = [preventions: Prevention]
	static i18nFields = ["names"]
	static embedded = ["firstOccurenceOn"]

	
	static constraints = {
		importFrom MaintenanceOrder, exclude:["closedOn","lastUpdated"]
		lastUpdated nullable: true, validator:{ val, obj ->
			if(val!=null) return (val <= new Date())
		}
		occurInterval nullable: false, validator:{return (it >= 1)}
		firstOccurenceOn nullable: false, validator:{ return (it.timeDate >= new Date())}
		names nullable: true, blank: true
		type nullable: false, inList:[PreventiveOrderType.DURATIONBASED,PreventiveOrderType.WORKBASED]
		status nullable:false, inList:[PreventiveOrderStatus.OPEN,PreventiveOrderStatus.CLOSED]

		closedOn nullable:true, validator:{ val, obj ->
			if(val!=null) return (val<=new Date() && obj.status.equals(PreventiveOrderStatus.CLOSED))
		}
		preventionResponsible nullable:false,inList:[PreventionResponsible.HCTECHNICIAN,PreventionResponsible.HCTITULAIRE,PreventionResponsible.SERVICEPROVIDER,PreventionResponsible.DEPARTMENTUSER]
		technicianInCharge nullable:true, validator:{ val, obj ->
			if(obj.preventionResponsible.equals(PreventionResponsible.HCTECHNICIAN)) return (val!=null)
			else return (val==null)
		}
		
	}
	static mapping = {
		table "memms_preventive_order_abstract"
		tablePerHierarchy false 
		cache true
		version false

	}

	def getDurationMinutes() {
        return Minutes.minutesBetween(new DateTime(firstOccurenceOn.timeDate), getEndTimeForOrder()).minutes
    }

    def getEndTimeForOrder() {
        return new DateTime(firstOccurenceOn.timeDate).plusHours(1)
    }	
	
}
