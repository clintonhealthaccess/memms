package org.chai.memms.equipment

import org.apache.shiro.SecurityUtils;
import org.chai.memms.AbstractEntityController;
import org.chai.memms.Contact
import org.chai.memms.Initializer;
import org.chai.memms.equipment.EquipmentStatus.Status;
import org.chai.memms.security.User;
import org.chai.memms.equipment.Provider
import org.chai.memms.location.DataLocation;
import org.chai.memms.location.CalculationLocation;


class EquipmentController extends AbstractEntityController{
	
	def providerService
	def equipmentService
    def getEntity(def id) {
		return Equipment.get(id);
	}

	def createEntity() {
		return new Equipment();
	}

	def getTemplate() {
		return "/entity/equipment/createEquipment";
	}

	def getLabel() {
		return "equipment.label";
	}
	
	def deleteEntity(def entity) {
		
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
		def status = params['status']
		status = Status."$status"
		def currentStatus = newEquipmentStatus(new Date(),getUser(),status,entity,true,params["dateOfEvent"])
	}
	 
	def getModel(def entity) {
		[
			equipment:entity,
			departments:Department.list(),
			manufactures: providerService.getManufacturesAndBoth(),
			suppliers: providerService.getSuppliersAndBoth(),
			types: EquipmentType.list(),
			dataLocations: DataLocation.list()
		]
	}

	def list={
		adaptParamsForList()
		def equipments		
		if(SecurityUtils.subject.isPermitted("*:*")){
			equipments = Equipment.list(params)
		}else if(SecurityUtils.subject.isPermitted("equipment:list")){
			def user = getUser()
			if(user != null){
				equipments = equipmentService.getEquipmentsByDataLocation(user.location)
			}
		}
		render(view:"/entity/list", model:[
			template:"equipment/equipmentList",
			entities: equipments,
			entityCount: Equipment.count(),
			code: getLabel(),
			entityClass: getEntityClass()
			])
	}
	def search = {
		adaptParamsForList()
		List<Equipment> equipments = equipmentService.searchEquipment(params['q'], params)	
		render (view: '/entity/list', model:[
			template:"equipment/equipmentList",
			entities: equipments,
			entityCount: equipmentService.countEquipment(params['q']),
			code: getLabel()
		])
		
	}
	def filter = {
		
	}
	
	def export = {
		
	}
	def importer = {
		
	}
	static def newEquipmentStatus(def statusChangeDate,def changedBy,def value, def equipment,def current,def dateOfEvent){
		return new EquipmentStatus(statusChangeDate:statusChangeDate,changedBy:changedBy,value:value,equipment:equipment,current:current,dateOfEvent:dateOfEvent).save(failOneError:true,flush:true)
	}
	
}
