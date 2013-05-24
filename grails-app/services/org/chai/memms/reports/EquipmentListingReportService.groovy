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
package org.chai.memms.reports

import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.security.User;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.Equipment.Donor;

/**
 * @author Aphrodice Rwagaju
 *
 */
class EquipmentListingReportService {
	
	def userService
	
	public def getGeneralReportOfEquipments(User user,Map<String, String> params) {
	def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll(((DataLocation)user.location).manages)
		}
	
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList('dataLocation',dataLocations)
		}
	}

	public def getDisposedEquipments(User user,Map<String, String> params) {
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll(((DataLocation)user.location).manages)
		}
		
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList('dataLocation',dataLocations)
					eq ("currentStatus",Status.DISPOSED)
		}
	}
	public def getObsoleteEquipments(User user,Map<String, String> params) {
		Boolean obsolete
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll(((DataLocation)user.location).manages)
		}
		
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList('dataLocation',dataLocations)
					eq ("obsolete", (obsolete.equals('true'))?true:false)
		}
	}
	public def getUnderMaintenanceEquipments(User user,Map<String, String> params) {
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll(((DataLocation)user.location).manages)
		}
		
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList('dataLocation',dataLocations)
					eq ("currentStatus",Status.UNDERMAINTENANCE)
		}
	}
	public def getInStockEquipments(User user,Map<String, String> params) {
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll(((DataLocation)user.location).manages)
		}
		
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList('dataLocation',dataLocations)
					eq ("currentStatus",Status.INSTOCK)
		}
	}
	public def getUnderWarrantyEquipments(User user,Map<String, String> params) {
		def dataLocations = []
		
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
			if(userService.canViewManagedEquipments(user)) dataLocations.addAll(((DataLocation)user.location).manages)
		}
		
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList('dataLocation',dataLocations)
		}
	}

	public def getCustomReportOfEquipments(User user,def customEquipmentParams, Map<String, String> params) {
		
		def equipmentStatus = customEquipmentParams.get('equipmentStatus')
		def dataLocations = customEquipmentParams.get('dataLocations')
		def departments = customEquipmentParams.get('departments')
		def equipmentTypes = customEquipmentParams.get('equipmentTypes')
		def lowerLimitCost = customEquipmentParams.('fromCost')
		def upperLimitCost = customEquipmentParams.('toCost')
		def currency = customEquipmentParams.get('costCurrency')
		def obsolete = customEquipmentParams.get('obsolete')

		// def fromAcquisitionPeriod = customEquipmentParams.get('fromAcquisitionPeriod')
		// def toAcquisitionPeriod = customEquipmentParams.get('toAcquisitionPeriod')
		// def noAcquisitionPeriod = customEquipmentParams.get('noAcquisitionPeriod')
	
		def criteria = Equipment.createCriteria();
		
		return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList('dataLocation',dataLocations)
				if(departments != null)
					inList ("department", departments)
				if(equipmentTypes != null)
					inList ("type", equipmentTypes)
				if(currency !=null)
					eq ("currency",currency)
				if(obsolete)
					eq ("obsolete", (obsolete.equals('true'))?true:false)
				if(equipmentStatus!=null && !equipmentStatus.empty)
					inList ("currentStatus",equipmentStatus)
				if(lowerLimitCost!=null)
					gt ("purchaseCost", lowerLimitCost)
				if(upperLimitCost!=null)
					lt ("purchaseCost", upperLimitCost)	
				// if(fromAcquisitionPeriod != null)
				// 	gt ("purchaseDate", fromAcquisitionPeriod)
				// if(toAcquisitionPeriod != null)
				// 	lt ("purchaseDate", toAcquisitionPeriod)
		}
	}

}
