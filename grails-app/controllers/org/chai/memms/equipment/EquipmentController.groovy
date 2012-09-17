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
package org.chai.memms.equipment

import org.apache.shiro.SecurityUtils;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.Contact
import org.chai.memms.Initializer;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.memms.task.EquipmentExportFilter;
import org.chai.memms.equipment.Provider
import org.chai.location.DataLocation;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.apache.commons.lang.math.NumberUtils

import java.util.HashSet;
import java.util.Set

/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentController extends AbstractEntityController{
	
	def providerService
	def equipmentService
	def inventoryService
	def grailsApplication
	
	def index = {
		redirect(action: "summaryPage", params: params)
	}
	
    def getEntity(def id) {
		return Equipment.get(id);
	}

	def createEntity() {
		def entity = new Equipment();
		if(!params["dataLocation.id"]) entity.dataLocation = DataLocation.get(params.int("location"));
		return entity;
	}

	def getTemplate() {
		return "/entity/equipment/createEquipment";
	}

	def getLabel() {
		return "equipment.label";
	}
		
	def getEntityClass() {
		return Equipment.class;
	}
	def bindParams(def entity) {
		if(log.isDebugEnabled()) log.debug("Equipment params: before bind "+params)
		if(!entity.id){
			entity.registeredOn=new Date()
		}else{
			if(params["donation"]=="on"){
				params["purchaseCost"] = ""
				params["currency"] = ""
			}
			if(params["warranty.sameAsSupplier"]=="on"){
				params["warranty.contact.contactName"]=""
				params["warranty.contact.email"]=""
				params["warranty.contact.phone"]=""
				params["warranty.contact.poBox"]=""
				params["warranty.contact.city"]=""
				params["warranty.contact.country"]=""
				grailsApplication.config.i18nFields.locales.each{loc ->
					params["warranty.contact.addressDescriptions_"+loc] = ""
				}
				entity.warranty.contact=null
			}
		}
		bindData(entity,params,[exclude:["status","dateOfEvent"]])
		if(log.isDebugEnabled()) log.debug("Equipment params: after bind  "+entity)
	}
	
	def validateEntity(def entity) {
		boolean valid = true
		if(entity.id==null){
			valid = (!params.cmd.hasErrors())
			if(log.isDebugEnabled()) log.debug("Rejecting status: "+params.cmd.errors)
		}
		return (valid & entity.validate())
	}
	
	def saveEntity(def entity) {
		def currentStatus 
		if(entity.id==null)
			currentStatus = newEquipmentStatus(new Date(),getUser(),params.cmd.status,entity,true,params.cmd.dateOfEvent)
		entity.save()
		(!currentStatus)?:currentStatus.save()
		
	}

	def save ={StatusCommand cmd ->
		params.cmd = cmd
		super.saveWithoutTokenCheck()
	}
	 
	def getModel(def entity) {
		def manufacturers = []; def suppliers = []; def departments = []; def types = []; def dataLocations = [];
		if (entity.manufacturer != null) manufacturers << entity.manufacturer
		if (entity.supplier != null) suppliers << entity.supplier
		if (entity.department!=null) departments << entity.department
		if (entity.type!=null) types << entity.type
		if (entity.dataLocation!=null) dataLocations << entity.dataLocation
		[
			equipment: entity,
			departments: departments,
			manufacturers: manufacturers,
			suppliers: suppliers,
			types: types,
			dataLocations: dataLocations,
			numberOfStatusToDisplay: grailsApplication.config.status.to.display.on.equipment.form,
			cmd:params.cmd,
			currencies: grailsApplication.config.site.possible.currency

		]
	}

	def search = {
		adaptParamsForList()
		def location = DataLocation.get(params.int("location"));
		if (location == null)
			response.sendError(404)
		else{
			List<Equipment> equipments = equipmentService.searchEquipment(params['q'],location,params)	
			render (view: '/entity/list', model:[
				template:"equipment/equipmentList",
				entities: equipments,
				entityCount: equipments.totalCount,
				code: getLabel(),
				q:params['q'],
				dataLocation:location
			])
		}
	}
	
	def summaryPage = {
		if(user.location instanceof DataLocation) redirect(uri: "/equipment/list/" + user.location.id)
		
		def location = Location.get(params.int('location'))
		def dataLocationTypesFilter = getLocationTypes()
		def template = null
		def inventories = null
		
		adaptParamsForList()
		
		def locationSkipLevels = inventoryService.getSkipLocationLevels()
		
		
		if (location != null) {
			template = '/inventory/sectionTable'
			inventories = inventoryService.getInventoryByLocation(location,dataLocationTypesFilter,params)
		}
		
		render (view: '/inventory/summaryPage', model: [
			inventories:inventories?.inventoryList,
			currentLocation: location,
			currentLocationTypes: dataLocationTypesFilter,
			template: template,
			entityCount: inventories?.totalCount,
			locationSkipLevels: locationSkipLevels,
			entityClass: getEntityClass()
		])
	}
	
	def list={ 
		def dataLocation = DataLocation.get(params.int('location'))
		if (dataLocation == null)
			response.sendError(404)
			
		adaptParamsForList()
		def equipments = equipmentService.getEquipmentsByDataLocation(dataLocation,params)								
		render(view:"/entity/list", model:[
				template:"equipment/equipmentList",
				filterTemplate:"equipment/equipmentFilter",
				dataLocation:dataLocation,
				entities: equipments,
				entityCount: equipments.totalCount,
				code: getLabel(),
				entityClass: getEntityClass()
				])


	}
	
	def getCalculationLocationAjaxData = {
		List<CalculationLocation> calculationLocations = locationService.searchLocation(CalculationLocation.class, params['term'], [:])
		render(contentType:"text/json") {
			elements = array {
				calculationLocations.each { calculationLocation ->
					elem (
						key: calculationLocation.id,
						value: calculationLocation.getNames(languageService.getCurrentLanguage())
					)
				}
			}
		}
		
	}
	
	
	def generalExport={ExportFilterCommand cmd ->

		// we do this because automatic data binding does not work with polymorphic elements
		Set<CalculationLocation> calculationLocations = new HashSet<CalculationLocation>()
		params.list('calculationLocationids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def calculationLocation = CalculationLocation.get(id)
				if (calculationLocation != null && !calculationLocations.contains(calculationLocation)) calculationLocations.add(calculationLocation);
			}
		}
		cmd.calculationLocations = calculationLocations

		Set<DataLocationType> dataLocationTypes = new HashSet<DataLocationType>()
		params.list('dataLocationTypeids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def dataLocationType = DataLocationType.get(id)
				if (dataLocationType != null && !dataLocationTypes.contains(dataLocationType)) dataLocationTypes.add(dataLocationType);
			}
		}
		cmd.dataLocationTypes = dataLocationTypes

		Set<EquipmentType> equipmentTypes = new HashSet<EquipmentType>()
		params.list('equipmentTypeids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def equipmentType = EquipmentType.get(id)
				if (equipmentType != null && !equipmentTypes.contains(equipmentType)) equipmentTypes.add(equipmentType);
			}
		}
		cmd.equipmentTypes = equipmentTypes

		Set<Provider> manufacturers = new HashSet<Provider>()
		params.list('manufacturerids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def manufacturer = Provider.get(id)
				if (manufacturer != null && !manufacturers.contains(manufacturer)) manufacturers.add(manufacturer);
			}
		}
		cmd.manufacturers = manufacturers

		Set<Provider> suppliers = new HashSet<Provider>()
		params.list('supplierids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def supplier = Provider.get(id)
				if (supplier != null && !suppliers.contains(supplier)) suppliers.add(supplier);
			}
		}
		cmd.suppliers = suppliers

		if (log.isDebugEnabled()) log.debug("equipments.export, command="+cmd+", params"+params)

		
		if(params.exported != null){
			def equipmentExportTask = new EquipmentExportFilter(calculationLocations:cmd.calculationLocations,dataLocationTypes:cmd.dataLocationTypes,
				equipmentTypes:cmd.equipmentTypes,manufacturers:cmd.manufacturers,suppliers:cmd.suppliers,equipmentStatus:cmd.equipmentStatus,
				donated:cmd.donated,obsolete:cmd.obsolete).save(failOnError: true,flush: true)
			params.exportFilterId = equipmentExportTask.id
			params.class = "EquipmentExportTask"
			params.targetURI = "/equipment/generalExport"
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

	def filter = { FilterCommand cmd ->
		if (log.isDebugEnabled()) log.debug("equipments.filter, command "+cmd)
		if (cmd.dataLocation == null)
			response.sendError(404)

		adaptParamsForList()
		def equipments = equipmentService.filterEquipment(cmd.dataLocation,cmd.supplier,cmd.manufacturer,cmd.equipmentType,cmd.donated,cmd.obsolete,cmd.status,params)
		render (view: '/entity/list', model:[
					template:"equipment/equipmentList",
					filterTemplate:"equipment/equipmentFilter",
					dataLocation:cmd.dataLocation,
					entities: equipments,
					entityCount: equipments.totalCount,
					code: getLabel(),
					entityClass: getEntityClass(),
					filterCmd:cmd,
					q:params['q']
				])

	}
	
	def export = {
		def dataLocation = DataLocation.get(params.int('location'))
		if (dataLocation == null)
			response.sendError(404)
		adaptParamsForList()
		def equipments = equipmentService.getEquipmentsByDataLocation(dataLocation,params)	
		
		File file = equipmentService.exporter(dataLocation,equipments)
		
		response.setHeader "Content-disposition", "attachment; filename=${file.name}.csv"
		response.contentType = 'text/csv'
		response.outputStream << file.text
		response.outputStream.flush()
	}
	
	def updateObsolete = {
		if (log.isDebugEnabled()) log.debug("equipment.donation "+params['equipment.id'])
		def equipment = Equipment.get(params.int(['equipment.id']))
		def property = params['field'];
		if (equipment == null || property ==null)
			response.sendError(404)
		else {
			def value= false; def entity = null; def error = ""
			if(property.equals("obsolete")){
				if(equipment.obsolete) equipment.obsolete = false
				else equipment.obsolete = true
				entity = equipment.save(flush:true)
				
			}
			if(property.equals("donation")){
				if(equipment.donation) equipment.donation = false
				else equipment.donation = true
				entity = equipment.save(flush:true)
			}
			
			if(entity!=null) value=true 
			render(contentType:"text/json") { results = [value]}
		}
	}
	
	static def newEquipmentStatus(def statusChangeDate,def changedBy,def value, def equipment,def current,def dateOfEvent){
		return new EquipmentStatus(statusChangeDate:statusChangeDate,changedBy:changedBy,status:value,equipment:equipment,current:current,dateOfEvent:dateOfEvent)
	}
	
}
class StatusCommand {
	Status status
	Date dateOfEvent
	
	static constraints = {
		status nullable: false, inList: [Status.DISPOSED,Status.FORDISPOSAL,Status.INSTOCK,Status.OPERATIONAL,Status.UNDERMAINTENANCE]
		dateOfEvent nullable: false 
	}
	
	String toString(){
		return "StatusCommand [Status "+status+" dateOfEvent: "+dateOfEvent+"]";
	}
}

class FilterCommand {
	DataLocation dataLocation
	EquipmentType equipmentType
	Provider manufacturer
	Provider supplier
	Status status
	String donated
	String obsolete
	
	public boolean getDonationStatus(){
		if(donated) return null
		else if(donated.equals("true")) return true
		else if(donated.equals("false")) return false
	}
	
	public boolean getObsoleteStatus(){
		if(obsolete) return null
		else if(obsolete.equals("true")) return true
		else if(obsolete.equals("false")) return false
	}

	static constraints = {
		
		equipmentType nullable:true
		manufacturer nullable:true 
		supplier nullable:true 
		status nullable:true 
		donated nullable:true 
		obsolete nullable:true
		
		dataLocation nullable:false, validator:{val, obj ->
			return (obj.equipmentType != null || obj.manufacturer != null || obj.supplier != null || (obj.status != null && obj.status != Status.NONE) || obj.donated || obj.obsolete)?true:"select.atleast.one.value.text"
			}
	}
	
	String toString() {
		return "FilterCommand[DataLocation="+dataLocation+", EquipmentType="+equipmentType+
		", Manufacturer="+manufacturer+", Supplier="+supplier+", Status="+status+", donated="+donated+", obsolete="+obsolete+
		", Absolote status=" + getObsoleteStatus() + ", Donation status=" + getDonationStatus() + "]"
	}
}

class ExportFilterCommand {
	Set<CalculationLocation> calculationLocations
	Set<DataLocationType> dataLocationTypes
	Set<EquipmentType> equipmentTypes
	Set<Provider> manufacturers
	Set<Provider> suppliers
	Status equipmentStatus
	String donated
	String obsolete
	
	public boolean getDonationStatus(){
		if(donated) return null
		else if(donated.equals("true")) return true
		else if(donated.equals("false")) return false
	}
	
	public boolean getObsoleteStatus(){
		if(obsolete) return null
		else if(obsolete.equals("true")) return true
		else if(obsolete.equals("false")) return false
	}

	static constraints = {
		calculationLocations nullable:true
		dataLocationTypes nullable:true
		equipmentTypes nullable:true
		manufacturers nullable:true
		suppliers nullable:true
		equipmentStatus nullable:true
		donated nullable:true
		obsolete nullable:true
	}
	
	String toString() {
		return "ExportFilterCommand[ CalculationLocations="+calculationLocations+", DataLocationTypes="+dataLocationTypes+" , EquipmentTypes="+equipmentTypes+
		", Manufacturers="+manufacturers+", Suppliers="+suppliers+", Status="+equipmentStatus+", donated="+donated+", obsolete="+obsolete+
		", Absolote status=" + getObsoleteStatus() + ", Donation status=" + getDonationStatus() + "]"
	}
}
