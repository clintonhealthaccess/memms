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
import org.chai.memms.equipment.Provider
import org.chai.memms.location.DataLocation;
import org.chai.memms.location.CalculationLocation;
import org.chai.memms.location.DataLocationType;
import org.chai.memms.location.Location
import org.chai.memms.location.LocationLevel

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
		if(!entity.id)
			entity.registeredOn=new Date()
		bindData(entity,params, [exclude:['status','dateOfEvent']])
	}
	
	def validateEntity(def entity) {
		boolean valid = true
		if(entity.id!=null && params["status"].equals("NONE") && params["dateOfEvent"]){
			valid = false
			entity.errors.rejectValue("status","equipment.status.notselected", "You have to select a status.");
		}
		return (valid & entity.validate())
	}
	
	def saveEntity(def entity) {
		entity.save()
		if(params['status'] && params["dateOfEvent"]){
			def status = params['status']
			status = Status."$status"
			def currentStatus = newEquipmentStatus(new Date(),getUser(),status,entity,true,params["dateOfEvent"])
		}
	}
	 
	def getModel(def entity) {
		def manufactures = []; def suppliers = []; def departments = []; def types = []; def dataLocations = [];
		if (entity.manufacture != null) manufactures << entity.manufacture
		if (entity.supplier != null) suppliers << entity.supplier
		if (entity.department!=null) departments << entity.department
		if (entity.type!=null) types << entity.type
		if (entity.dataLocation!=null) dataLocations << entity.dataLocation
		[
			equipment: entity,
			departments: departments,
			manufactures: manufactures,
			suppliers: suppliers,
			types: types,
			dataLocations: dataLocations,
			numberOfStatusToDisplay: grailsApplication.config.status.to.display.on.equipment.form

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
		if (dataLocation == null){
			response.sendError(404)
		}
		adaptParamsForList()
		
		def equipments = equipmentService.getEquipmentsByDataLocation(dataLocation,params)
												
			render(view:"/entity/list", model:[
				template:"equipment/equipmentList",
				filterTemplate:"equipment/equipmentFilter",
				dataLocation:dataLocation,
				entities: equipments,
				//filterCmd: filterCommand,
				entityCount: equipments.totalCount,
				code: getLabel(),
				entityClass: getEntityClass()
				])

	}

	def filter = { FilterCommand cmd ->
		if (log.isDebugEnabled()) log.debug("equipments.filter, command "+cmd)

		if (cmd.dataLocation == null){
			response.sendError(404)
		}

		adaptParamsForList()
		def equipments = []

		equipments = equipmentService.filterEquipment(
				cmd.dataLocation,cmd.supplier,cmd.manufacturer,cmd.equipmentType,cmd.donated,cmd.obsolete,cmd.status,params)

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
		
	}
	def importer = {
		
	}
	static def newEquipmentStatus(def statusChangeDate,def changedBy,def value, def equipment,def current,def dateOfEvent){
		return new EquipmentStatus(statusChangeDate:statusChangeDate,changedBy:changedBy,status:value,equipment:equipment,current:current,dateOfEvent:dateOfEvent).save(failOneError:true,flush:true)
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
		
		dataLocation(nullable:false, validator:{val, obj ->
			return (obj.equipmentType != null || obj.manufacturer != null || obj.supplier != null || (obj.status != null && obj.status != Status.NONE) || obj.donated || obj.obsolete)?true:"select.atleast.one.value.text"
			})
	}
	
	String toString() {
		return "FilterCommand[DataLocation="+dataLocation+", EquipmentType="+equipmentType+
		", Manufacturer="+manufacturer+", Supplier="+supplier+", Status="+status+", donated="+donated+", obsolete="+obsolete+
		", Absolote status=" + getObsoleteStatus() + ", Donation status=" + getDonationStatus() + "]"
	}
}
