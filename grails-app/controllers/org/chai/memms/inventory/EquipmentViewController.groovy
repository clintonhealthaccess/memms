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

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.memms.AbstractController;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.Provider;
import org.chai.location.DataLocation;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.task.EquipmentExportFilter;

/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentViewController extends AbstractController {
	
	def providerService
	def equipmentService
	def inventoryService
	def grailsApplication
	def equipmentStatusService

	def getLabel() {
		return "equipment.label";
	}

	def getEntityClass() {
		return Equipment.class;
	}
	
	def getEquipmentClueTipsAjaxData = {
		def equipment = Equipment.get(params.long("equipment.id"))
		def html = g.render(template:"/templates/equipmentClueTip",model:[equipment:equipment])
		render(contentType:"text/plain", text:html)
	}
	
	def view ={
		Equipment equipment = Equipment.get(params.int("equipment.id"))
		if(equipment == null)	response.sendError(404)
		else render(view:"/entity/equipment/summary", model:[equipment: equipment])		
	}
	
	def list={
		def dataLocation = DataLocation.get(params.int('dataLocation.id'))
		def equipments
		adaptParamsForList()
		
		if (dataLocation != null){
			if(!user.canAccessCalculationLocation(dataLocation)) response.sendError(404)
			equipments = equipmentService.getEquipmentsByDataLocationAndManages(dataLocation,params)
		}
		else equipments = equipmentService.getMyEquipments(user,params)
		
		if(request.xhr){
			 this.ajaxModel(equipments,dataLocation,"")
		 }else{
			render(view:"/entity/list", model: model(equipments, dataLocation) << [
				template:"equipment/equipmentList",
				filterTemplate:"equipment/equipmentFilter",
				listTop:"equipment/listTop"
			])
		}
	}
	
	//TODO don't think we need ajax for this
	def selectFacility = {
		adaptParamsForList()
		def dataLocations = []
		dataLocations.add(user.location as DataLocation)
		if((user.location as DataLocation).manages)
			dataLocations.addAll((user.location as DataLocation).manages)
			
		render(view:"/entity/list", model:[
			listTop:"equipment/listTop",
			template:"equipment/selectFacility",
			dataLocations:dataLocations
		])
	}
	
	def search = {
		DataLocation dataLocation = DataLocation.get(params.int("dataLocation.id"))
		if (dataLocation != null && !user.canAccessCalculationLocation(dataLocation))
			response.sendError(404)
		else{
			adaptParamsForList()
			def equipments = equipmentService.searchEquipment(params['q'],user,dataLocation,params)
			if(!request.xhr)
				render(view:"/entity/list", model: model(equipments, dataLocation) << [
					template:"equipment/equipmentList",
					filterTemplate:"equipment/equipmentFilter",
					listTop:"equipment/listTop"
				])
			else
				this.ajaxModel(equipments,dataLocation,params['q'])
		}
	}
	
	def filter = { FilterCommand cmd ->
		if (log.isDebugEnabled()) log.debug("equipments.filter, command "+cmd)
		if (cmd.dataLocation != null && !user.canAccessCalculationLocation(cmd.dataLocation))
			response.sendError(404)
		else{
			adaptParamsForList()
			def equipments = equipmentService.filterEquipment(user,cmd.dataLocation,cmd.supplier,cmd.manufacturer,cmd.serviceProvider,cmd.equipmentType,cmd.purchaser,cmd.donor,cmd.obsolete,cmd.status,params)
			if(request.xhr)
				this.ajaxModel(equipments,cmd.dataLocation,"")
			else {
				render(view:"/entity/list", model: model(equipments, cmd.dataLocation) << [
					template:"equipment/equipmentList",
					filterTemplate:"equipment/equipmentFilter",
					listTop:"equipment/listTop"
				])
			}
		}
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
		def listHtml = g.render(template:"/entity/equipment/equipmentList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}

	def summaryPage = {
		if(user.location instanceof DataLocation) redirect(controller:"equipmentView",action:"list")

		def location = Location.get(params.long('location'))
		def dataLocationTypesFilter = getLocationTypes()
		def template = null
		def inventories = null

		adaptParamsForList()

		def locationSkipLevels = inventoryService.getSkipLocationLevels()

		if (location != null) 
			inventories = inventoryService.getInventoryByLocation(location,dataLocationTypesFilter,params)
	
		render (view: '/inventorySummaryPage/summaryPage', model: [
					inventories:inventories?.inventoryList,
					currentLocation: location,
					currentLocationTypes: dataLocationTypesFilter,
					template: "/inventorySummaryPage/sectionTable",
					entityCount: inventories?.totalCount,
					locationSkipLevels: locationSkipLevels
				])
	}

	def generalExport = { ExportFilterCommand cmd ->

		Set<DataLocation> dataLocationTypes = new HashSet<DataLocationType>()
		params.list('dataLocationTypeids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def dataLocationType = DataLocationType.get(id)
				if (dataLocationType != null && !dataLocationTypes.contains(dataLocationType)) 
				dataLocationTypes.add(dataLocationType);
			}
		}
		cmd.dataLocationTypes = dataLocationTypes
		
		Set<CalculationLocation> calculationLocations = new HashSet<CalculationLocation>()
		params.list('calculationLocationids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def calculationLocation = CalculationLocation.get(id)
				if (CalculationLocation != null && !calculationLocations.contains(calculationLocation))
				calculationLocations.add(calculationLocation);
			}
		}
		cmd.calculationLocations = calculationLocations

		Set<EquipmentType> equipmentTypes = new HashSet<EquipmentType>()
		params.list('equipmentTypeids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def equipmentType = EquipmentType.get(id)
				if (equipmentType != null && !equipmentTypes.contains(equipmentType)) 
				equipmentTypes.add(equipmentType);
			}
		}
		cmd.equipmentTypes = equipmentTypes

		Set<Provider> manufacturers = new HashSet<Provider>()
		params.list('manufacturerids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def manufacturer = Provider.get(id)
				if (manufacturer != null && !manufacturers.contains(manufacturer)) 
				manufacturers.add(manufacturer);
			}
		}
		cmd.manufacturers = manufacturers

		Set<Provider> suppliers = new HashSet<Provider>()
		params.list('supplierids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def supplier = Provider.get(id)
				if (supplier != null && !suppliers.contains(supplier)) 
				suppliers.add(supplier);
			}
		}
		cmd.suppliers = suppliers
		
		Set<Provider> serviceProviders = new HashSet<Provider>()
		params.list('serviceProviderids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def serviceProvider = Provider.get(id)
				if (serviceProvider != null && !serviceProviders.contains(serviceProvider)) serviceProviders.add(serviceProvider);
			}
		}
		cmd.serviceProviders = serviceProviders

		if (log.isDebugEnabled()) log.debug("equipments.export, command="+cmd+", params"+params)

		if(params.exported != null){
			def equipmentExportTask = new EquipmentExportFilter(dataLocationTypes:cmd.dataLocationTypes,calculationLocations:cmd.calculationLocations,
					equipmentTypes:cmd.equipmentTypes,serviceProviders:cmd.serviceProviders,manufacturers:cmd.manufacturers,suppliers:cmd.suppliers,equipmentStatus:cmd.equipmentStatus, donor:cmd.donor,
					purchaser:cmd.purchaser,obsolete:cmd.obsolete).save(failOnError: true,flush: true)
			params.exportFilterId = equipmentExportTask.id
			params.class = "EquipmentExportTask"
			params.targetURI = "/equipmentView/generalExport"
			redirect(controller: "task", action: "create", params: params)
		}
		adaptParamsForList()
		render(view:"/entity/equipment/equipmentExportPage", model:[
					template:"/entity/equipment/equipmentExportFilter",
					filterCmd:cmd,
					dataLocationTypes:DataLocationType.list(),
					code: getLabel()
				])
	}

	def export = { FilterCommand cmd ->
		if (log.isDebugEnabled()) log.debug("equipments.export, command "+cmd)
		def dataLocation = DataLocation.get(params.int('dataLocation.id'))
		adaptParamsForList()
		def equipments = equipmentService.filterEquipment(user,dataLocation,cmd.supplier,cmd.manufacturer,cmd.serviceProvider,cmd.equipmentType,cmd.purchaser,cmd.donor,cmd.obsolete,cmd.status,[:])
		if (log.isDebugEnabled()) log.debug("EQUIPMENTS TO BE EXPORTED IN SIZE "+equipments.size())
		File file = equipmentService.exporter(dataLocation?:user.location,equipments)

		response.setHeader "Content-disposition", "attachment; filename=${file.name}.csv"
		response.contentType = 'text/csv'
		response.outputStream << file.text
		response.outputStream.flush()
	}

	def updateObsolete = {
		if (log.isDebugEnabled()) log.debug("updateObsolete equipment.obsolete "+params['equipment.id'])
		Equipment equipment = Equipment.get(params.int(['equipment.id']))
		def property = params['field'];
		if (equipment == null || property ==null)
			response.sendError(404)
		else {
			def value= false; def entity = null;
			if(property.equals("obsolete")){
				if(equipment.obsolete) equipment.obsolete = false
				else {
					equipment.lastModifiedBy = user
					equipment.obsolete = true
				}
				entity = equipment.save(flush:true)

			}
			if(entity!=null) value=true
			render(contentType:"text/json") { results = [value]}
		}
	}

	def getAjaxData = {
		def dataLocation =null
		if(params['dataLocation']) dataLocation = DataLocation.get(params.int('dataLocation'))
		List<Equipment> equipments = equipmentService.searchEquipment(params['term'],user,dataLocation,[:])
		render(contentType:"text/json") {
			elements = array {
				equipments.each { equipment ->
					elem (
							key: equipment.id,
							value: "["+equipment.serialNumber +"] - ["+equipment.type.names+"] - ["+equipment.dataLocation.names+"]"					
						)
				}
			}
			htmls = array {
				equipments.each { equipment ->
					elem (
							key: equipment.id,
							html: g.render(template:"/templates/equipmentFormSide",model:[equipment:equipment,cssClass:"form-aside-hidden",field:"equipment"])
							)
				}
			}
		}

	}

}
class FilterCommand {
	DataLocation dataLocation
	EquipmentType equipmentType
	Provider manufacturer
	Provider supplier
	Provider serviceProvider
	Status status = Status.NONE
	PurchasedBy purchaser
	String obsolete
	Donor donor

	public boolean getObsoleteStatus(){
		if(obsolete) return null
		else if(obsolete.equals("true")) return true
		else if(obsolete.equals("false")) return false
	}

	static constraints = {

		equipmentType nullable:true
		manufacturer nullable:true
		serviceProvider nullable: true
		supplier nullable:true
		status nullable:true
		purchaser nullable:true
		obsolete nullable:true
		donor nullable:true

		dataLocation nullable:false, validator:{val, obj ->
			return (obj.equipmentType != null || obj.manufacturer != null || obj.serviceProvider != null || obj.supplier != null || (obj.status != null && obj.status != Status.NONE) || obj.purchaser || obj.obsolete)?true:"select.atleast.one.value.text"
		}
	}

	String toString() {
		return "FilterCommand[DataLocation="+dataLocation+", EquipmentType="+equipmentType+
		", Manufacturer="+manufacturer+", Supplier="+supplier+", ServiceProvider="+serviceProvider+", Status="+status+", purchaser="+purchaser+", obsolete="+obsolete+
		", donor=" + donor + "]"
	}
}

class ExportFilterCommand {
	Set<DataLocationType> dataLocationTypes
	Set<CalculationLocation> calculationLocations
	Set<EquipmentType> equipmentTypes
	Set<Provider> manufacturers
	Set<Provider> suppliers
	Set<Provider> serviceProviders
	Status equipmentStatus
	PurchasedBy purchaser
	String obsolete
	Donor donor

	public boolean getObsoleteStatus(){
		if(obsolete) return null
		else if(obsolete.equals("true")) return true
		else if(obsolete.equals("false")) return false
	}

	static constraints = {
		equipmentTypes nullable:true
		manufacturers nullable:true
		suppliers nullable:true
		serviceProviders nullable:true
		equipmentStatus nullable:true
		purchaser nullable:true
		obsolete nullable:true
		donor nullable:true
		dataLocationTypes nullable:true
		calculationLocations nullable:true
	}

	String toString() {
		return "ExportFilterCommand[ DataLocationTypes="+dataLocationTypes+" ,CalculationLocations="+calculationLocations+" , EquipmentTypes="+equipmentTypes+
		", Manufacturers="+manufacturers+", Suppliers="+suppliers+", ServiceProviders="+serviceProviders+", Status="+equipmentStatus+", purchaser="+purchaser+", obsolete="+obsolete+
		", donor=" + donor + "]"
	}
}
