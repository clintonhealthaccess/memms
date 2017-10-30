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
package org.chai.memms.reports.listing

import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.memms.security.User;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentStatus.EquipmentStatusChange;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.util.Utils.ReportType;
import org.chai.memms.util.Utils.ReportSubType;
import org.chai.memms.util.Utils;
import org.joda.time.DateTime;

/**
 * @author Aphrodice Rwagaju
 *
 */
class EquipmentListingReportService {

	def equipmentService
	def equipmentTypeService
	def userService
	def today =new Date()

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
				inList("dataLocation",dataLocations)
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
				inList("dataLocation",dataLocations)
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
				inList("dataLocation",dataLocations)
			eq ("obsolete", (obsolete.equals('true'))?false:true)
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
				inList("dataLocation",dataLocations)
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
				inList("dataLocation",dataLocations)
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
				inList("dataLocation",dataLocations)
		}
	}

	public def getCustomReportOfEquipments(User user,def customEquipmentParams, Map<String, String> params) {

		def reportType = customEquipmentParams.get('reportType')
		def reportSubType = customEquipmentParams.get('reportSubType')

		def dataLocations = customEquipmentParams.get('dataLocations')
		def departments = customEquipmentParams.get('departments')
		def equipmentTypes = customEquipmentParams.get('equipmentTypes')
		def lowerLimitCost = customEquipmentParams.get('fromCost')
		def upperLimitCost = customEquipmentParams.get('toCost')
		def currency = customEquipmentParams.get('costCurrency')
		def noCost = customEquipmentParams.get('noCost')

		if(reportSubType == ReportSubType.INVENTORY){
			if(dataLocations == null || dataLocations.empty || departments == null || departments.empty || equipmentTypes == null || equipmentTypes.empty){
				if (log.isDebugEnabled()) log.debug("getCustomReportOfEquipments.inventory Mandatory property criteria not satisfied")
				return []
			}

			def fromAcquisitionPeriod = customEquipmentParams.get('fromAcquisitionPeriod')
			def toAcquisitionPeriod = customEquipmentParams.get('toAcquisitionPeriod')
			def noAcquisitionPeriod = customEquipmentParams.get('noAcquisitionPeriod')
			def equipmentStatus = customEquipmentParams.get('equipmentStatus')
			def obsolete = customEquipmentParams.get('obsolete')
			def warranty = customEquipmentParams.get('warranty')

			def equipmentCriteria = Equipment.createCriteria()

			return equipmentCriteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){

				//Mandatory property
				inList("dataLocation", dataLocations)
				//Mandatory property
				inList ("department", departments)
				//Mandatory property
				inList ("type", equipmentTypes)
				or{	
					if(lowerLimitCost && lowerLimitCost!=null)
						gt ("purchaseCost", lowerLimitCost)
					if(upperLimitCost && upperLimitCost!=null)
						lt ("purchaseCost", upperLimitCost)
					if(noCost != null && noCost)
						isNull ("purchaseCost")
					if(currency && currency !=null && ((lowerLimitCost && lowerLimitCost!=null)||(upperLimitCost && upperLimitCost!=null)))
						eq ("currency", currency)
				}
				
				if(equipmentStatus!=null && !equipmentStatus.empty)
					inList ("currentStatus",equipmentStatus)
				if(obsolete != null && obsolete)
					eq ("obsolete", (obsolete.equals('true'))?true:false)
				if(warranty!=null && warranty)
					lt ("warrantyEndDate",today)
				or{
					if(fromAcquisitionPeriod && fromAcquisitionPeriod != null)
						gt ("purchaseDate", fromAcquisitionPeriod)
					if(toAcquisitionPeriod && toAcquisitionPeriod != null)
						lt ("purchaseDate", toAcquisitionPeriod)
					if(noAcquisitionPeriod != null && noAcquisitionPeriod)
						isNull ("purchaseDate")
				}
			}
		}

		if(reportSubType == ReportSubType.STATUSCHANGES){
			if(dataLocations == null || dataLocations.empty || departments == null || departments.empty || equipmentTypes == null || equipmentTypes.empty){
				if (log.isDebugEnabled()) log.debug("getCustomReportOfEquipments.statusChanges Mandatory property criteria not satisfied")
				return []
			}

			def equipmentStatusChanges = customEquipmentParams.get('statusChanges')
			def fromStatusChangesPeriod = customEquipmentParams.get('fromStatusChangesPeriod')
			def toStatusChangesPeriod = customEquipmentParams.get('toStatusChangesPeriod')

			def equipmentStatusCriteria = EquipmentStatus.createCriteria()

			return equipmentStatusCriteria.list(offset:params.offset,max:params.max, sort:params.sort ?:"id",order: params.order ?:"desc"){

				createAlias("equipment","equip")

				//Mandatory property
				inList("equip.dataLocation", dataLocations)
				//Mandatory property
				inList ("equip.department", departments)
				//Mandatory property
				inList ("equip.type", equipmentTypes)

				if(lowerLimitCost && lowerLimitCost!=null)
					gt ("equip.purchaseCost", lowerLimitCost)
				if(upperLimitCost && upperLimitCost!=null)
					lt ("equip.purchaseCost", upperLimitCost)
				if(currency && currency !=null)
					eq ("equip.currency",currency)
				if(noCost != null && noCost)
					isNull ("equip.purchaseCost")

				//Status changes
				if(equipmentStatusChanges != null && !equipmentStatusChanges.empty){
					or {
						equipmentStatusChanges.each{ equipmentStatusChange ->
							def previousStatus = equipmentStatusChange.statusChange['previous']
							def currentStatus = equipmentStatusChange.statusChange['current']
							if (log.isDebugEnabled()) log.debug("PARAMS STATUS CHANGES <<<<<<<<<<<<<<<<<<<<<< Previous :"+previousStatus+" Current :"+currentStatus)
							and {
								inList("previousStatus", previousStatus)
								inList("status", currentStatus)
							}
						}
					}
				}

				if(fromStatusChangesPeriod && fromStatusChangesPeriod != null)
					gt ("dateOfEvent", fromStatusChangesPeriod)
				if(toStatusChangesPeriod && toStatusChangesPeriod != null)
					lt ("dateOfEvent", toStatusChangesPeriod)
			}
		}
	}

	public def saveEquipmentReportParams(User user, def customEquipmentParams, Map<String, String> params){
		def equipmentReport = new EquipmentReport()

		def reportName = customEquipmentParams.get('customizedReportName')
		def reportType = customEquipmentParams.get('reportType')
		def reportSubType = customEquipmentParams.get('reportSubType')

		def dataLocations = customEquipmentParams.get('dataLocations')
		def departments = customEquipmentParams.get('departments')
		def equipmentTypes = customEquipmentParams.get('equipmentTypes')
		def lowerLimitCost = customEquipmentParams.get('fromCost')
		def upperLimitCost = customEquipmentParams.get('toCost')
		def currency = customEquipmentParams.get('costCurrency')
		
		def fromAcquisitionPeriod = customEquipmentParams.get('fromAcquisitionPeriod')
		def toAcquisitionPeriod = customEquipmentParams.get('toAcquisitionPeriod')
		def noAcquisitionPeriod = customEquipmentParams.get('noAcquisitionPeriod')
		def equipmentStatus = customEquipmentParams.get('equipmentStatus')
		def obsolete = customEquipmentParams.get('obsolete')
		def warranty = customEquipmentParams.get('warranty')
		def noCost = customEquipmentParams.get('noCost')
		def listingReportDisplayOptions = customEquipmentParams.get('reportTypeOptions')
		def equipmentStatusChanges = customEquipmentParams.get('statusChanges')
		def fromStatusChangesPeriod = customEquipmentParams.get('fromStatusChangesPeriod')
		def toStatusChangesPeriod = customEquipmentParams.get('toStatusChangesPeriod')
		
		if (log.isDebugEnabled()) log.debug("PARAMS TO BE SAVED ON EQUIPMENT CUSTOM REPORT: LOWER COST :"+lowerLimitCost+" UPPER COST :"+upperLimitCost)
		
		equipmentReport.underWarranty=warranty=='on'?true:false
		equipmentReport.obsolete=obsolete=='on'?true:false
		equipmentReport.equipmentStatus=equipmentStatus
		equipmentReport.noAcquisitionPeriod = noAcquisitionPeriod=='on'?true:false
		equipmentReport.toDate=toAcquisitionPeriod
		equipmentReport.fromDate=fromAcquisitionPeriod
		equipmentReport.currency=currency
		equipmentReport.upperLimitCost=upperLimitCost
		equipmentReport.lowerLimitCost=lowerLimitCost
		equipmentReport.equipmentTypes=equipmentTypes
		equipmentReport.departments=departments
		equipmentReport.dataLocations=dataLocations
		equipmentReport.reportSubType=reportSubType
		equipmentReport.reportType=reportType
		equipmentReport.reportName=reportName
		equipmentReport.savedBy=user
		equipmentReport.noCostSpecified=noCost=="on"?true:false
		equipmentReport.displayOptions=listingReportDisplayOptions
		equipmentReport.statusChanges=equipmentStatusChanges
		equipmentReport.fromStatusChangesPeriod=fromStatusChangesPeriod
		equipmentReport.toStatusChangesPeriod=toStatusChangesPeriod
		
		equipmentReport.save(failOnError:true)
		if (log.isDebugEnabled()) log.debug("PARAMS TO BE SAVED ON EQUIPMENT CUSTOM REPORT SAVED CORRECTLY. THE REPORT ID IS :"+ equipmentReport.id+" >>>> STATUS CHANGES: "+equipmentReport.statusChanges)

		return equipmentReport
	}
}
