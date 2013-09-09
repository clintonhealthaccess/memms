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
import org.chai.memms.corrective.maintenance.WorkOrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrder;
import org.chai.memms.security.User;
import org.chai.memms.util.Utils;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.WorkOrderStatusChange;
import org.chai.memms.util.Utils.ReportType
import org.chai.memms.util.Utils.ReportSubType
import org.joda.time.DateTime

/**
 * @author Aphrodice Rwagaju
 *
 */
class WorkOrderListingReportService {

	def workOrderService
	def today = new Date()

	def getAllWorkOrders(User user , Map<String, String> params){
		
		def criteria = WorkOrder.createCriteria();
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
		}
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
		}	
	}

	def getWorkOrdersEscalatedToMMC(User user,Map<String, String> params) {

		def criteria = WorkOrder.createCriteria();
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
		}
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
			eq ("currentStatus",OrderStatus.OPENATMMC)
		}
	}

	def getWorkOrdersOfLastMonth(User user,Map<String, String> params) {	
		DateTime todayDateTime = new DateTime(today)
		def lastMonthDateTimeFromNow = todayDateTime.minusDays(30)
		def lastMonthDateFromNow = lastMonthDateTimeFromNow.toDate()

		def criteria = WorkOrder.createCriteria();
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations = []
			dataLocations.add(user.location as DataLocation)
		}
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
			or{
				eq ("currentStatus",OrderStatus.OPENATMMC)
				eq ("currentStatus",OrderStatus.OPENATFOSA)
			}
			ge ("openOn",lastMonthDateFromNow)
		}
	}

	def getCustomReportOfWorkOrders(User user, def customWorkOrderParams, Map<String, String> params) {

		def reportType = customWorkOrderParams.get('reportType')
		def reportSubType = customWorkOrderParams.get('reportSubType')

		def dataLocations = customWorkOrderParams.get('dataLocations')
		def departments = customWorkOrderParams.get('departments')
		def equipmentTypes = customWorkOrderParams.get('equipmentTypes')
		def lowerLimitCost = customWorkOrderParams.('fromCost')
		def upperLimitCost = customWorkOrderParams.('toCost')
		def currency = customWorkOrderParams.get('costCurrency')
		def noCost = customWorkOrderParams.get('noCost')

		if(reportSubType == ReportSubType.WORKORDERS){
			def workOrderStatus = customWorkOrderParams.get('workOrderStatus')
			def fromWorkOrderPeriod = customWorkOrderParams.get('fromWorkOrderPeriod')
			def toWorkOrderPeriod = customWorkOrderParams.get('toWorkOrderPeriod')
			def warranty = customWorkOrderParams.get('warranty')
			
			def workOrderCriteria = WorkOrder.createCriteria();

			return workOrderCriteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){

				createAlias("equipment","equip")

				//Mandatory property
				inList("equip.dataLocation", dataLocations)
				//Mandatory property
				inList ("equip.department", departments)
				//Mandatory property
				inList ("equip.type", equipmentTypes)
				or{
					if(lowerLimitCost && lowerLimitCost!=null)
						gt ("equip.purchaseCost", lowerLimitCost)
					if(upperLimitCost && upperLimitCost!=null)
						lt ("equip.purchaseCost", upperLimitCost)
					if(noCost != null && noCost)
						isNull ("equip.purchaseCost")
				}
				if(currency && currency !=null)
					eq ("equip.currency",currency)

				if(workOrderStatus!=null && !workOrderStatus.empty)
					inList ("currentStatus",workOrderStatus)
				if(fromWorkOrderPeriod && fromWorkOrderPeriod != null)
				 	ge ("openOn", fromWorkOrderPeriod)
				if(toWorkOrderPeriod && toWorkOrderPeriod != null)
				 	le ("openOn", toWorkOrderPeriod)
				if(warranty!=null && warranty)
					lt ("equip.warrantyEndDate",today)
			}
		}

		if(reportSubType == ReportSubType.STATUSCHANGES){
			def workOrderStatusChanges = customWorkOrderParams.get('statusChanges')
			def fromStatusChangesPeriod = customWorkOrderParams.get('fromStatusChangesPeriod')
			def toStatusChangesPeriod = customWorkOrderParams.get('toStatusChangesPeriod')
			
			def workOrderStatusCriteria= WorkOrderStatus.createCriteria();

			return workOrderStatusCriteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){

				createAlias("workOrder","wo")
				createAlias("wo.equipment","equipment")

				//Mandatory property
				inList("equipment.dataLocation", dataLocations)
				//Mandatory property
				inList ("equipment.department", departments)
				//Mandatory property
				inList ("equipment.type", equipmentTypes)
				or{
					if(lowerLimitCost && lowerLimitCost!=null)
						gt ("equipment.purchaseCost", lowerLimitCost)
					if(upperLimitCost && upperLimitCost!=null)
						lt ("equipment.purchaseCost", upperLimitCost)
					if(noCost != null && noCost)
						isNull ("equipment.purchaseCost")
				}
				if(currency && currency !=null)
					eq ("equipment.currency",currency)

				//Status changes
				if(workOrderStatusChanges != null || !workOrderStatusChanges.empty){
					or {
						workOrderStatusChanges.each{ workOrderStatusChange ->
							def previousStatus = workOrderStatusChange.statusChange['previous']
							def currentStatus = workOrderStatusChange.statusChange['current']
							and {
								inList("previousStatus", previousStatus)
								inList("status", currentStatus)
							}
						}
					}
				}

				if(fromStatusChangesPeriod && fromStatusChangesPeriod != null)
					gt ("dateCreated", fromStatusChangesPeriod)
						
				if(toStatusChangesPeriod && toStatusChangesPeriod != null)
					lt ("dateCreated", toStatusChangesPeriod)
			}
		}
	}

	public def saveWorkOrderReportParams(User user, def customWorkOrderParams, Map<String, String> params){
		def correctiveMaintenanceReport = new CorrectiveMaintenanceReport()

		def reportName = customWorkOrderParams.get('customizedReportName')
		def reportType = customWorkOrderParams.get('reportType')
		def reportSubType = customWorkOrderParams.get('reportSubType')

		def dataLocations = customWorkOrderParams.get('dataLocations')
		def departments = customWorkOrderParams.get('departments')
		def equipmentTypes = customWorkOrderParams.get('equipmentTypes')
		def lowerLimitCost = customWorkOrderParams.get('fromCost')
		def upperLimitCost = customWorkOrderParams.get('toCost')
		def currency = customWorkOrderParams.get('costCurrency')
		def noCost = customWorkOrderParams.get('noCost')
		
		def fromWorkOrderPeriod = customWorkOrderParams.get('fromWorkOrderPeriod')
		def toWorkOrderPeriod = customWorkOrderParams.get('toWorkOrderPeriod')
		def warranty = customWorkOrderParams.get('warranty')
		def workOrderStatus = customWorkOrderParams.get('workOrderStatus')
		def listingReportDisplayOptions = customWorkOrderParams.get('reportTypeOptions')
		
		def statusChanges = customWorkOrderParams.get('statusChanges')
		def fromStatusChangesPeriod = customWorkOrderParams.get('fromStatusChangesPeriod')
		def toStatusChangesPeriod = customWorkOrderParams.get('toStatusChangesPeriod')
		
		if (log.isDebugEnabled()) log.debug("PARAMS TO BE SAVED ON EQUIPMENT CUSTOM REPORT: WARRANTY :"+warranty)
		
		correctiveMaintenanceReport.underWarranty=warranty=="on"?true:false
		correctiveMaintenanceReport.toDate=toWorkOrderPeriod
		correctiveMaintenanceReport.fromDate=fromWorkOrderPeriod
		correctiveMaintenanceReport.currency=currency
		correctiveMaintenanceReport.upperLimitCost=upperLimitCost
		correctiveMaintenanceReport.lowerLimitCost=lowerLimitCost
		correctiveMaintenanceReport.equipmentTypes=equipmentTypes
		correctiveMaintenanceReport.departments=departments
		correctiveMaintenanceReport.dataLocations=dataLocations
		correctiveMaintenanceReport.reportSubType=reportSubType
		correctiveMaintenanceReport.reportType=reportType
		correctiveMaintenanceReport.reportName=reportName
		correctiveMaintenanceReport.workOrderStatus=workOrderStatus
		correctiveMaintenanceReport.savedBy=user
		correctiveMaintenanceReport.noCostSpecified=noCost=="on"?true:false
		correctiveMaintenanceReport.displayOptions=listingReportDisplayOptions
		correctiveMaintenanceReport.statusChanges=statusChanges
		correctiveMaintenanceReport.fromStatusChangesPeriod=fromStatusChangesPeriod
		correctiveMaintenanceReport.toStatusChangesPeriod=toStatusChangesPeriod

		correctiveMaintenanceReport.save(failOnError:true)
		if (log.isDebugEnabled()) log.debug("PARAMS TO BE SAVED ON EQUIPMENT CUSTOM REPORT SAVED CORRECTLY. THE REPORT ID IS :"+ correctiveMaintenanceReport.id)

		return correctiveMaintenanceReport
	}
	
	def getClosedWorkOrdersOfLastYear(User user,Map<String, String> params) {
		DateTime todayDateTime = new DateTime(today)
		def lastYaerDateTimeFromNow = todayDateTime.minusDays(365)
		def lastYearDateFromNow = lastYaerDateTimeFromNow.toDate()

		def criteria = WorkOrder.createCriteria();
		def dataLocations = []
		if(user.location instanceof Location) dataLocations.addAll(user.location.collectDataLocations(null))
		else{
			dataLocations.add(user.location as DataLocation)
		}
		return criteria.list(offset:params.offset,max:params.max,sort:params.sort ?:"id",order: params.order ?:"desc"){
			createAlias("equipment","equip")
			if(dataLocations)
				inList('equip.dataLocation',dataLocations)
			or{
				eq ("currentStatus",OrderStatus.CLOSEDFIXED)
				eq ("currentStatus",OrderStatus.CLOSEDFORDISPOSAL)
			}
			ge ("closedOn",lastYearDateFromNow)
		}
		
		if (log.isDebugEnabled()) log.debug("WORK ORDER SIZE: "+ criteria.size())
	}
}
