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
import org.chai.memms.security.User.UserType;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePart.StockLocation;
import org.chai.memms.spare.part.SparePartType;
import org.chai.memms.spare.part.SparePart.SparePartStatus;
import org.chai.memms.spare.part.SparePart.SparePartStatusChange;
import org.chai.memms.util.Utils.ReportType;
import org.chai.memms.util.Utils.ReportSubType;
import org.chai.memms.util.Utils;
import org.joda.time.DateTime;
import org.chai.memms.maintenance.MaintenanceOrder;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;


/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartListingReportService {
	def sparePartService
	def userService
	def today = new Date()

	public def getGeneralReportOfSpareParts(User user,SparePartType type,Map<String, String> params) {
		def dataLocations = []
		def criteria = SparePart.createCriteria();
		if(user.userType.equals(UserType.ADMIN) || user.userType.equals(UserType.TECHNICIANMMC) || user.userType.equals(UserType.SYSTEM) || user.userType.equals(UserType.TECHNICIANDH))
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(type!=null)
					eq("type",type)
			}
		else{
			if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
			else{
				dataLocations = []
				dataLocations.add(user.location as DataLocation)
				if(userService.canViewManagedSpareParts(user)) dataLocations.addAll(((DataLocation)user.location).manages)
			}
			return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList("dataLocation",dataLocations)
			}
		}
	}

	public def getPendingOrderSparePartsReport(User user,SparePartType type, Map<String, String> params) {
		def dataLocations = []
		def criteria = SparePart.createCriteria();
		if(user.userType.equals(UserType.ADMIN) || user.userType.equals(UserType.TECHNICIANMMC) || user.userType.equals(UserType.SYSTEM) || user.userType.equals(UserType.TECHNICIANDH))
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(type!=null)
					eq("type",type)
					eq ("status",SparePartStatus.PENDINGORDER)
			}
		else{
			if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
			else{
				dataLocations = []
				dataLocations.add(user.location as DataLocation)
				if(userService.canViewManagedSpareParts(user)) dataLocations.addAll(((DataLocation)user.location).manages)
			}
			return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				if(dataLocations)
					inList("dataLocation",dataLocations)
					eq ("status",SparePartStatus.PENDINGORDER)
			}
		}
	}

	public def getCustomReportOfSpareParts(User user, def customSparePartsParams, Map<String, String> params) {

		def reportType = customSparePartsParams.get('reportType')
		def reportSubType = customSparePartsParams.get('reportSubType')

		def dataLocations = customSparePartsParams.get('dataLocations')
		def sparePartTypes = customSparePartsParams.get('sparePartTypes')
		def showAtMMC = customSparePartsParams.get('showAtMmc')

		def sparePartCriteria = SparePart.createCriteria()

		if(reportSubType == ReportSubType.INVENTORY){
			def sparePartStatus = customSparePartsParams.get('sparePartStatus')
			def fromAcquisitionPeriod = customSparePartsParams.get('fromAcquisitionPeriod')
			def toAcquisitionPeriod = customSparePartsParams.get('toAcquisitionPeriod')
			def noAcquisitionPeriod = customSparePartsParams.get('noAcquisitionPeriod')

			return sparePartCriteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){

				//Mandatory property
				or {
					inList("dataLocation", dataLocations)
					if(showAtMMC != null)
						eq("stockLocation",StockLocation.MMC)
				}
				//Mandatory property
				inList ("type", sparePartTypes)

				if(sparePartStatus!=null && !sparePartStatus.empty)
					inList ("status",sparePartStatus)
				
				or{
					if(fromAcquisitionPeriod != null)
						gt ("purchaseDate", fromAcquisitionPeriod)
					if(toAcquisitionPeriod != null)
						lt ("purchaseDate", toAcquisitionPeriod)	
					if(noAcquisitionPeriod != null)
						eq ("purchaseDate", null)
				}
			}
		}

		if(reportSubType == ReportSubType.STATUSCHANGES){
			def sparePartStatusChanges = customSparePartsParams.get('statusChanges')
			def fromStatusChangesPeriod = customSparePartsParams.get('fromStatusChangesPeriod')
			def toStatusChangesPeriod = customSparePartsParams.get('toStatusChangesPeriod')
			def fromAcquisitionPeriod = customSparePartsParams.get('fromAcquisitionPeriod')
			def toAcquisitionPeriod = customSparePartsParams.get('toAcquisitionPeriod')
			def noAcquisitionPeriod = customSparePartsParams.get('noAcquisitionPeriod')
			def sparePartStatus = customSparePartsParams.get('sparePartStatus')

			return sparePartCriteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				
				//Mandatory property
				or {
					inList("dataLocation", dataLocations)
					if(showAtMMC != null)
						eq("stockLocation",StockLocation.MMC)
				}
				//Mandatory property
				inList ("type", sparePartTypes)

				//Status changes
				if(sparePartStatusChanges != null || !sparePartStatusChanges.empty){
					if(sparePartStatusChanges.size() > 1){
						or {
							if(sparePartStatusChanges.contains(SparePartStatusChange.NEWPENDINGORDER)){
								and {
									isNotNull("purchaseDate")
									isNull("deliveryDate")
									eq("status",SparePartStatus.PENDINGORDER)
								}
							}
							if(sparePartStatusChanges.contains(SparePartStatusChange.PENDINGORDERARRIVED)){
								and {
									isNotNull("purchaseDate")
									or{
										isNotNull("deliveryDate")
										eq("status",SparePartStatus.INSTOCK)
									}
								}
							}
						}
					}
					else{
						if(sparePartStatusChanges.contains(SparePartStatusChange.NEWPENDINGORDER)){
							and {
								isNotNull("purchaseDate")
								isNull("deliveryDate")
								eq("status",SparePartStatus.PENDINGORDER)
							}
						}
						if(sparePartStatusChanges.contains(SparePartStatusChange.PENDINGORDERARRIVED)){
							and {
								isNotNull("purchaseDate")
								or{
									isNotNull("deliveryDate")
									eq("status",SparePartStatus.INSTOCK)
								}
							}
						}
					}
				}
			}
		}
		if(reportSubType == ReportSubType.USERATE){
			DateTime todayDateTime = new DateTime(today)
			def lastYearDateTimeFromNow = todayDateTime.minusDays(365)
			def lastYearDateFromNow = lastYearDateTimeFromNow.toDate()
			
			return sparePartCriteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				createAlias("type","t")
				//Mandatory property
				or {
					inList("dataLocation", dataLocations)
					if(showAtMMC != null)
						eq("stockLocation",StockLocation.MMC)
				}
				//Mandatory property
				inList ("type", sparePartTypes)
					
					
				eq ("status",SparePartStatus.INSTOCK)
				//gt("deliveryDate", lastYearDateFromNow)
				//projections{
					//property("t.id")
					//groupProperty("t.id")
					//rowCount("inStockQuantity")
				//}
			}
		}

		if(reportSubType == ReportSubType.STOCKOUT){
			DateTime todayDateTime = new DateTime(today)
			def lastYearDateTimeFromNow = todayDateTime.minusDays(365)
			def lastYearDateFromNow = lastYearDateTimeFromNow.toDate()
			
			return sparePartCriteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			def sparePartCount=	sparePartCriteria.get{
				//projections{
					//property("type")
					//groupProperty("t.id")
					//rowCount("inStockQuantity")
				//}
				//Mandatory property
				or {
					inList("dataLocation", dataLocations)
					if(showAtMMC != null)
						eq("stockLocation",StockLocation.MMC)
				}
				//Mandatory property
				inList ("type", sparePartTypes)
					
				eq ("status",SparePartStatus.INSTOCK)
				//gt("deliveryDate", lastYearDateFromNow)
			}
			}
		}
	}

	public def saveSparePartReportParams(User user, def customSparePartParams, Map<String, String> params){
		def sparePartReport = new SparePartReport()

		def reportName = customSparePartParams.get('customizedReportName')
		def reportType = customSparePartParams.get('reportType')
		def reportSubType = customSparePartParams.get('reportSubType')

		def dataLocations = customSparePartParams.get('dataLocations')
		def sparePartTypes = customSparePartParams.get('sparePartTypes')

		def fromAcquisitionPeriod = customSparePartParams.get('fromAcquisitionPeriod')
		def toAcquisitionPeriod = customSparePartParams.get('toAcquisitionPeriod')
		def noAcquisitionPeriod = customSparePartParams.get('noAcquisitionPeriod')
		def sparePartStatus = customSparePartParams.get('sparePartStatus')
		def listingReportDisplayOptions = customSparePartParams.get('reportTypeOptions')

		//TODO AR add status changes and status changes period to saved report
		if (log.isDebugEnabled()) log.debug("PARAMS TO BE SAVED ON SPARE PART CUSTOM REPORT: SPARE PART STATUS :"+sparePartStatus)
		sparePartReport.sparePartStatus=sparePartStatus
		sparePartReport.noAcquisitionPeriod=noAcquisitionPeriod=="on"?true:false
		sparePartReport.toDate=toAcquisitionPeriod
		sparePartReport.fromDate=fromAcquisitionPeriod
		sparePartReport.sparePartTypes=sparePartTypes
		sparePartReport.dataLocations=dataLocations
		sparePartReport.reportSubType=reportSubType
		sparePartReport.reportType=reportType
		sparePartReport.reportName=reportName
		sparePartReport.savedBy=user
		sparePartReport.displayOptions=listingReportDisplayOptions
		sparePartReport.save(failOnError:true)
		if (log.isDebugEnabled()) log.debug("PARAMS TO BE SAVED ON SPARE PART CUSTOM REPORT SAVED CORRECTLY. THE REPORT ID IS :"+ sparePartReport.id)

		return sparePartReport
	}
	
	def getQuantityOfEachTypeOfSparePartUsedLastYear(User user, Map<SparePart, Integer> usedSpareParts, Map<String, String> params){
		DateTime todayDateTime = new DateTime(today)
		def lastYearDateTimeFromNow = todayDateTime.minusDays(365)
		def lastYearDateFromNow = lastYearDateTimeFromNow.toDate()
		
		def dataLocations = []
		
		def criteria = WorkOrder.createCriteria();
		if(user.userType.equals(UserType.ADMIN) || user.userType.equals(UserType.TECHNICIANMMC) || user.userType.equals(UserType.SYSTEM) || user.userType.equals(UserType.TECHNICIANDH))
			return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				createAlias("equipment","equip")
				if(usedSpareParts.size()!=0 && (OrderStatus.CLOSEDFIXED ||OrderStatus.CLOSEDFORDISPOSAL))
					//eq("equip.type",usedSpareParts.get(SparePart.type))
					eq ("status",SparePartStatus.INSTOCK)
					gt ("closedOn",lastYearDateFromNow)
			}
		else{
			if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
			else{
				dataLocations = []
				dataLocations.add(user.location as DataLocation)
				if(userService.canViewManagedSpareParts(user)) dataLocations.addAll(((DataLocation)user.location).manages)
			}
			return  criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
				createAlias("equipment","equip")
				if(dataLocations)
					//eq("equip.type",usedSpareParts.get(SparePart.type))
					inList("dataLocation",dataLocations)
					gt ("closedOn",lastYearDateFromNow)
			}
		}
	}
}
