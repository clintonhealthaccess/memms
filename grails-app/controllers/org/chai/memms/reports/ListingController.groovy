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
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.memms.AbstractController;
import org.chai.memms.corrective.maintenance.WorkOrderStatus.OrderStatus
import org.chai.memms.inventory.Department
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.EquipmentType
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus
import org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart
import org.chai.memms.spare.part.SparePartType
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.memms.util.Utils
import org.chai.memms.util.Utils.ReportType
import org.chai.memms.util.Utils.ReportSubType

/**
 * @author Jean Kahigiso M.
 *
 */
class ListingController extends AbstractController{

	def equipmentListingReportService
	def workOrderListingReportService
	def preventiveOrderListingReportService
	def grailsApplication

	def getEntityClass() {
		return Equipment.class;
	}

	def getLabel() {
		return "equipment.listing.report.label";
	}

	def model(def entities, def dataLocation) {
		return [
			entities: entities,
			entityCount: entities.totalCount,
			dataLocation:dataLocation,
			entityClass:getEntityClass(),
			code: getLabel()
		]
	}

	def ajaxModel(def entities,def dataLocation,def searchTerm) {
		def model = model(entities, dataLocation) << [q:searchTerm]
		def listHtml = g.render(template:"/reports/listing/listing",model:model)
		render(contentType:"text/json") { results = [listHtml]}
	}

	def index ={
		redirect(action: "view", params: params)
	}

	def view ={
		redirect(action: "generalEquipmentsListing", params: params)
	}

	// inventory default and predefined reports

	def defaultEquipmentsView ={
		if (log.isDebugEnabled()) log.debug("listing.defaultEquipmentsView, params:"+params)

		render(view: '/reports/reports', model:
		[
			//Note: these 2 properties are required for any default or predefined reports
			reportType: ReportType.INVENTORY,
			reportSubType: ReportSubType.INVENTORY,
			template:"/reports/listing/listing"
		])
	}
	
	def generalEquipmentsListing={
		if (log.isDebugEnabled()) log.debug("listing.generalEquipmentsListing start, params:"+params)

		adaptParamsForList()
		def equipments = equipmentListingReportService.getGeneralReportOfEquipments(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", 
				model: model(equipments, "") << 
				[
					//Note: these 2 properties are required for any default or predefined reports
					reportType: ReportType.INVENTORY,
					reportSubType: ReportSubType.INVENTORY,
					template:"/reports/listing/listing"
				])
		
	}
	
	def disposedEquipments={
		if (log.isDebugEnabled()) log.debug("listing.generalEquipmentsListing start, params:"+params)

		adaptParamsForList()
		def equipments = equipmentListingReportService.getDisposedEquipments(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", 
				model: model(equipments, "") << 
				[
					//Note: these 2 properties are required for any default or predefined reports
					reportType: ReportType.INVENTORY,
					reportSubType: ReportSubType.INVENTORY,
					template:"/reports/listing/listing",
				])
		
	}
	
	def underMaintenanceEquipments={
		if (log.isDebugEnabled()) log.debug("listing.generalEquipmentsListing start, params:"+params)

		adaptParamsForList()
		def equipments = equipmentListingReportService.getUnderMaintenanceEquipments(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", 
				model: model(equipments, "") << 
				[
					//Note: these 2 properties are required for any default or predefined reports
					reportType: ReportType.INVENTORY,
					reportSubType: ReportSubType.INVENTORY,
					template:"/reports/listing/listing",
				])
		
	}
	
	def obsoleteEquipments={
		if (log.isDebugEnabled()) log.debug("listing.generalEquipmentsListing start, params:"+params)

		adaptParamsForList()
		def equipments = equipmentListingReportService.getObsoleteEquipments(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", 
				model: model(equipments, "") << 
				[
					//Note: these 2 properties are required for any default or predefined reports
					reportType: ReportType.INVENTORY,
					reportSubType: ReportSubType.INVENTORY,
					template:"/reports/listing/listing",
				])
		
	}

	def inStockEquipments={
		adaptParamsForList()
		def equipments = equipmentListingReportService.getInStockEquipments(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", 
				model: model(equipments, "") << 
				[
					//Note: these 2 properties are required for any default or predefined reports
					reportType: ReportType.INVENTORY,
					reportSubType: ReportSubType.INVENTORY,
					template:"/reports/listing/listing",
				])
	
	}

	def underWarrantyEquipments={
		adaptParamsForList()
		def displayableEquipments=[]
		def warrantyExpirationDate
		def equipments = equipmentListingReportService.getUnderWarrantyEquipments(user,params)
		for(Equipment equipment: equipments){
			if (equipment.warranty.startDate!=null && equipment.warrantyPeriod.numberOfMonths!=null && equipment.warrantyPeriod.months != null) {
				warrantyExpirationDate= (equipment.warranty.startDate).plus((equipment.warrantyPeriod.numberOfMonths))
				if (log.isDebugEnabled()) log.debug("CALCURATED DATE "+warrantyExpirationDate +"START DATE "+equipment.warranty.startDate +"WARRANTY PERIOD "+equipment.warrantyPeriod.months)
				if (warrantyExpirationDate > new Date())
					displayableEquipments.add(equipment)
			}
			warrantyExpirationDate=null
		}
		equipments=displayableEquipments
		if(!request.xhr)
			render(view:"/reports/reports", model: model(equipments, "") << [
				template:"/reports/listing/listing"
			])
	}
	//WorkOrders
	def lastMonthWorkOrders={
		adaptParamsForList()
		def workOrders = workOrderListingReportService.getWorkOrdersOfLastMonth(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", model: model(workOrders, "") << [
				template:"/reports/listing/listing",
			])
	}
	def workOrdersEscalatedToMMC={
		adaptParamsForList()
		def workOrders = workOrderListingReportService.getWorkOrdersEscalatedToMMC(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", model: model(workOrders, "") << [
				template:"/reports/listing/listing",
			])

	}

	//PreventiveOrders
	def equipmentsWithPreventionPlan={
		adaptParamsForList()
		def preventiveOrders = preventiveOrderListingReportService.getEquipmentsWithPreventionPlan(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", model: model(preventiveOrders, "") << [
				template:"/reports/listing/listing",
			])
	}
	//TODO see how to deal with periodic times either weekly, monthly, or any other
	def preventionsDelayed={
		adaptParamsForList()
		def preventiveOrders = preventiveOrderListingReportService.getEquipmentsWithPreventionPlan(user,params)
		if(!request.xhr)
			render(view:"/reports/reports", model: model(preventiveOrders, "") << [
				template:"/reports/listing/listing",
			])
	}
	
	//customized report wizard

	def step1 ={
		if (log.isDebugEnabled()) log.debug("listing.step1 start, params:"+params)

		def reportType = getReportType()
		def reportSubType = getReportSubType()
		// TODO keep some params ex. dataLocations ???
		// def dataLocations = getDataLocations()

		def step1Model = [
			reportType: reportType,
			reportSubType: reportSubType
			// TODO keep some params ex. dataLocations ???
			// dataLocations: dataLocations
		]

		def step1Params = [:]
    	step1Params.putAll params
    	step1Params.remove 'reportType'
    	step1Params.remove 'reportSubType'
    	// TODO keep some params ex. dataLocations ???
    	step1Model << [step1Params: step1Params]

    	if (log.isDebugEnabled()) log.debug("listing.step1 end, step1Model:"+step1Model)
		render(template:"/reports/listing/customizedReport/step1", 
			model: step1Model <<
			[
				reportType: reportType,
				reportSubType: reportSubType,
				dataLocations: dataLocations,
				step1Params: step1Params
			])
	}

	def step2 ={
		if (log.isDebugEnabled()) log.debug("listing.step2 start, params:"+params)

		def reportType = getReportType()
		def reportSubType = getReportSubType()
		// TODO keep some params ex. dataLocations ???
		// def dataLocations = getDataLocations()

		def step2Model = [
			reportType: reportType,
			reportSubType: reportSubType,
			// TODO keep some params ex. dataLocations ???
			// dataLocations: dataLocations
			currencies: grailsApplication.config.site.possible.currency
		]

	    def step2Params = [:]
	    step2Params.putAll step2Model
	    step2Model << [step2Params: step2Params]

	    if (log.isDebugEnabled()) log.debug("listing.step2 end, step2Model:"+step2Model)
		render(template:"/reports/listing/customizedReport/step2", model:step2Model)
	}

	def step3 ={
		if (log.isDebugEnabled()) log.debug("listing.step3 start, params:"+params)

		def reportType = getReportType()
		def reportSubType = getReportSubType()
		def dataLocations = getDataLocations()

		def step3Model = [
			reportType: reportType,
			reportSubType: reportSubType,
			dataLocations: dataLocations
		]

		// get report type params

		switch(reportType){
			case ReportType.INVENTORY:
			case ReportType.CORRECTIVE:
			case ReportType.PREVENTIVE:
				def departments = getDepartments()
				def equipmentTypes = getEquipmentTypes()
				def fromCost = params.get('fromCost')
				def toCost = params.get('toCost')
				def costCurrency = params.get('costCurrency')
				step3Model << [
					departments: departments,
					equipmentTypes: equipmentTypes,
					fromCost: fromCost,
					toCost: toCost,
					costCurrency: costCurrency
				]
				break;
			case ReportType.SPAREPARTS:
				def sparePartTypes = getSparePartTypes()
				step3Model << [
					sparePartTypes: sparePartTypes
				]
				break;
			default:
				break;
		}

		// get report sub type params

		switch(reportSubType){

			case ReportSubType.INVENTORY:
				def fromPeriod = getPeriod('fromAcquisitionPeriod')
				def toPeriod = getPeriod('toAcquisitionPeriod')
				step3Model << [
					fromAcquisitionPeriod: fromPeriod,
					toAcquisitionPeriod: toPeriod
				]
				if(reportType == ReportType.INVENTORY){
					def equipmentStatus = getInventoryStatus()
					def obsolete = params.get('obsolete')
					def warranty = params.get('warranty')
					step3Model << [
						equipmentStatus: equipmentStatus,
						obsolete: obsolete,
						warranty: warranty
					]	
				}
				if(reportType == ReportType.SPAREPARTS){
					def sparePartStatus = getSparePartStatus()
					step3Model << [sparePartStatus: sparePartStatus]
				}
				break;

			case ReportSubType.WORKORDERS:
				def fromPeriod = getPeriod('fromWorkOrderPeriod')
				def toPeriod = getPeriod('toWorkOrderPeriod')
				step3Model << [
					fromWorkOrderPeriod: fromPeriod,
					toWorkOrderPeriod: toPeriod
				]
				if(reportType == ReportType.CORRECTIVE){
					def workOrderStatus = getCorrectiveStatus()
					def warranty = params.get('warranty')
					step3Model << [
						workOrderStatus: workOrderStatus,
						warranty: warranty
					]
				}
				if(reportType == ReportType.PREVENTIVE) {
					def workOrderStatus = getPreventiveStatus()
					def whoIsResponsible = params.list('whoIsResponsible')
					step3Model << [
						workOrderStatus: workOrderStatus,
						whoIsResponsible: whoIsResponsible
					]
				}
				break;

			case ReportSubType.STATUSCHANGES:
				def fromPeriod = getPeriod('fromStatusChangesPeriod')
				def toPeriod = getPeriod('toStatusChangesPeriod')
				step3Model << [
					fromStatusChangesPeriod: fromPeriod,
					toStatusChangesPeriod: toPeriod
				]
				if(reportType == ReportType.INVENTORY){
					// TODO def statusChanges = getInventoryStatusChanges()
					// step3Model << [statusChanges: statusChanges]
				}
				if(reportType == ReportType.CORRECTIVE){
					// TODO def statusChanges = getCorrectiveStatusChanges()
					def warranty = params.get('warranty')
					step3Model << [
						// statusChanges: statusChanges,
						warranty: warranty
					]
				}
				if(reportType == ReportType.PREVENTIVE) {
					// TODO def statusChanges = getPreventiveStatusChanges()
					def doneByWho = params.list('doneByWho')
					step3Model << [
						// statusChanges: statusChanges,
						doneByWho: doneByWho
					]
				}
				if(reportType == ReportType.SPAREPARTS){
					// TODO def statusChanges = getSparePartsStatusChanges()
					// step3Model << [statusChanges: statusChanges]
				}
				break;

			case ReportSubType.STOCKOUT:
				def stockOut = params.get('stockOut')
				def stockOutMonths = params.get('stockOutMonths')
				step3Model << [
					stockOut: stockOut,
					stockOutMonths: stockOutMonths
				]
				break;

			case ReportSubType.USERATE:
				break;

			default:
				break;
		}

		if (log.isDebugEnabled()) log.debug("listing.step3 end, step3Model:"+step3Model)
		render(template:"/reports/listing/customizedReport/step3", model:step3Model)
	}

	def step4 ={
		//TODO Download vs Create vs Create and Save the customized report
	}

	def customizedListing ={
		if (log.isDebugEnabled()) log.debug("listing.customizedListing start, params:"+params)

		def reportType = getReportType()
		def reportSubType = getReportSubType()

		def customizedListingModel = [:]
		customizedListingModel.putAll params
		customizedListingModel << [customized:true]

		def reportTypeOptions = new HashSet<String>()
		switch(reportType){
			case ReportType.INVENTORY:
				reportTypeOptions = getReportTypeOptions('inventoryOptions')
				break;
			case ReportType.CORRECTIVE:
				reportTypeOptions = getReportTypeOptions('correctiveOptions')
				break;
			case ReportType.PREVENTIVE:
				reportTypeOptions = getReportTypeOptions('preventiveOptions')
				break;
			case ReportType.SPAREPARTS:
				reportTypeOptions = getReportTypeOptions('sparePartsOptions')
				break;
			default:
				break;
		}
		customizedListingModel << [reportTypeOptions:reportTypeOptions]

		if (log.isDebugEnabled()) log.debug("listing.customizedListing end, customizedListingModel:"+customizedListingModel)
		render(view: '/reports/reports',
			model: customizedListingModel << 
			[
				template:"/reports/listing/listing"
			])
	}

	def listing ={
		if (log.isDebugEnabled()) log.debug("listing start, params:"+params)

		def reportType = getReportType()
		def reportSubType = getReportSubType()
		// def dataLocations = getDataLocations()

		if (log.isDebugEnabled()) log.debug("listing end, params:"+params)
		render(view: '/reports/reports', model: 
			[
				template:"/reports/listing/listing",
				reportType: reportType,
				reportSubType: reportSubType
				// dataLocations: dataLocations
			])
	}

	def customizedReportSubType ={
		if (log.isDebugEnabled()) 
			log.debug("listing.step1.customizedReportSubType start, params:"+params)

		def reportType = getReportType()

		if (log.isDebugEnabled()) log.debug("listing.step1.customizedReportSubType end, reportType:"+reportType)
		render(template:"/reports/listing/customizedReport/customizedReportSubType",
			model: 
			[
				reportType: reportType
			])
	}

	// customized report wizard params

	// step 1 report type option params

	def getReportType(){
		ReportType reportType = params.get('reportType')
		if(log.isDebugEnabled()) 
			log.debug("abstract.reportType param:"+reportType+")")
		if(reportType == null) reportType = ReportType.INVENTORY
		return reportType
	}

	def getReportSubType(){
		ReportSubType reportSubType = params.get('reportSubType')
		if(log.isDebugEnabled()) 
			log.debug("abstract.reportSubType param:"+reportSubType+")")
		if(reportSubType == null) reportSubType = ReportSubType.INVENTORY
		return reportSubType
	}

	// step 2 report type option params

	public Set<DataLocation> getDataLocations() {
		if(log.isDebugEnabled()) log.debug("abstract.dataLocations params:"+params)
		Set<DataLocation> dataLocations = new HashSet<DataLocation>()
		if(params.get('allDataLocations')){
			// TODO dataLocations = user.location.collectDataLocations(null)
		}
		else if (params.list('dataLocations') != null && !params.list('dataLocations').empty) {
			def types = params.list('dataLocations')
			dataLocations.addAll(types.collect{ it ->
				if(log.isDebugEnabled()) 
					log.debug("abstract.dataLocations dataLocation:"+it+", isNumber:"+NumberUtils.isNumber(it as String))
				NumberUtils.isNumber(it as String) ? DataLocation.get(it) : null 
			} - null)
		}
		
		return dataLocations
	}

	public Set<Department> getDepartments() {
		if(log.isDebugEnabled()) log.debug("abstract.departments params:"+params)
		Set<Department> departments = new HashSet<Department>()
		if(params.get('allDepartments')){
			// TODO
			departments = Department.list()
		}
		else if (params.list('departments') != null && !params.list('departments').empty) {
			def types = params.list('departments')
			departments.addAll(types.collect{ it ->
				if(log.isDebugEnabled()) 
					log.debug("abstract.departments department:"+it+", isNumber:"+NumberUtils.isNumber(it as String))
				NumberUtils.isNumber(it as String) ? Department.get(it) : null 
			} - null)
		}
		
		return departments
	}
	
	public Set<EquipmentType> getEquipmentTypes() {
		if(log.isDebugEnabled()) log.debug("abstract.equipmentTypes params:"+params)
		Set<EquipmentType> equipmentTypes = new HashSet<EquipmentType>()
		if(params.get('allEquipmentTypes')){
			//TODO
			equipmentTypes = EquipmentType.list()
		}
		else if (params.list('equipmentTypes') != null && !params.list('equipmentTypes').empty) {
			def types = params.list('equipmentTypes')
			equipmentTypes.addAll(types.collect{ it ->
				if(log.isDebugEnabled()) 
					log.debug("abstract.equipmentTypes equipmentType:"+it+", isNumber:"+NumberUtils.isNumber(it as String))
				NumberUtils.isNumber(it as String) ? EquipmentType.get(it) : null 
			} - null)
		}
		return equipmentTypes
	}

	public Set<SparePartType> getSparePartTypes() {
		if(log.isDebugEnabled()) log.debug("abstract.equipmentTypes params:"+params)
		Set<SparePartType> sparePartTypes = new HashSet<SparePartType>()
		if(params.get('allSparePartTypes')){
			//TODO
			sparePartTypes = SparePartType.list()
		}
		else if (params.list('sparePartTypes') != null && !params.list('sparePartTypes').empty) {
			def types = params.list('sparePartTypes')
			sparePartTypes.addAll(types.collect{ it ->
				if(log.isDebugEnabled()) 
					log.debug("abstract.sparePartTypes sparePartType:"+it+", isNumber:"+NumberUtils.isNumber(it as String))
				NumberUtils.isNumber(it as String) ? SparePartType.get(it) : null 
			} - null)
		}
		return sparePartTypes
	}

	public Date getPeriod(String periodParam){
		def period = params.get(periodParam);
		if(log.isDebugEnabled()) 
			log.debug("abstract.getPeriod param:"+periodParam+", value:"+period+")")
		if(period != null && !period.empty) period = Date.parse('MM/dd/yyyy', period)
		else period = null
		return period
	}

	public Set<EquipmentStatus> getInventoryStatus(){
		if(log.isDebugEnabled()) log.debug("abstract.inventoryStatus params:"+params)
		Set<EquipmentStatus> inventoryStatus = new HashSet<EquipmentStatus>()
		if (params.list('equipmentStatus') != null && !params.list('equipmentStatus').empty) {
			def types = params.list('equipmentStatus')
			types.each{ it ->
				if(log.isDebugEnabled()) log.debug("abstract.equipmentStatus equipmentStatus:"+it)
				inventoryStatus.add(EquipmentStatus.get(it)-null)
			}
		}
		return inventoryStatus
	}

	public Set<OrderStatus> getCorrectiveStatus(){
		if(log.isDebugEnabled()) log.debug("abstract.correctiveStatus params:"+params)
		Set<OrderStatus> correctiveStatus = new HashSet<OrderStatus>()
		if (params.list('workOrderStatus') != null && !params.list('workOrderStatus').empty) {
			def types = params.list('workOrderStatus')
			types.each{ it ->
				if(log.isDebugEnabled()) log.debug("abstract.workOrderStatus workOrderStatus:"+it)
				correctiveStatus.add(OrderStatus.get(it)-null)
			}
		}
		return correctiveStatus
	}

	public Set<PreventiveOrderStatus> getPreventiveStatus(){
		if(log.isDebugEnabled()) log.debug("abstract.preventiveStatus params:"+params)
		Set<PreventiveOrderStatus> preventiveStatus = new HashSet<PreventiveOrderStatus>()
		if (params.list('workOrderStatus') != null && !params.list('workOrderStatus').empty) {
			def types = params.list('workOrderStatus')
			types.each{ it ->
				if(log.isDebugEnabled()) log.debug("abstract.workOrderStatus workOrderStatus:"+it)
				preventiveStatus.add(PreventiveOrderStatus.get(it)-null)
			}
		}
		return preventiveStatus
	}

	public Set<StatusOfSparePart> getSparePartStatus(){
		if(log.isDebugEnabled()) log.debug("abstract.sparePartStatus params:"+params)
		Set<StatusOfSparePart> sparePartStatus = new HashSet<StatusOfSparePart>()
		if (params.list('sparePartStatus') != null && !params.list('sparePartStatus').empty) {
			def types = params.list('sparePartStatus')
			types.each{ it ->
				if(log.isDebugEnabled()) log.debug("abstract.sparePartStatus sparePartStatus:"+it)
				sparePartStatus.add(StatusOfSparePart.get(it)-null)
			}
		}
		return sparePartStatus
	}

	// step 3 report type option params

	public Set<String> getReportTypeOptions(String reportTypeOptionParam){
		if(log.isDebugEnabled()) log.debug("abstract.reportTypeOptions params:"+params)
		Set<String> reportTypeOptions = new HashSet<String>()
		if (params.list(reportTypeOptionParam) != null && !params.list(reportTypeOptionParam).empty) {
			def options = params.list(reportTypeOptionParam)
			options.each{ it ->
				if(log.isDebugEnabled()) log.debug("abstract.reportTypeOption reportTypeOption:"+it)
				if(it != null) 
					reportTypeOptions.add(it)
			}
		}
		return reportTypeOptions
	}
}
