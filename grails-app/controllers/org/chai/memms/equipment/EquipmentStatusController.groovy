package org.chai.memms.equipment

import org.chai.memms.AbstractEntityController;

class EquipmentStatusController extends AbstractEntityController{

    def getEntity(def id) {
		return EquipmentStatus.get(id);
	}

	def createEntity() {
		def entity = new EquipmentStatus();
		if(!params["equipment.id"]) entity.equipment = Equipment.get(params.int("equipment"))
		return entity;
	}

	def getTemplate() {
		return "/entity/equipmentStatus/createEquipmentStatus";
	}

	def getLabel() {
		return "equipment.status.label";
	}
	
	def deleteEntity(def entity) {
		if(Equipment.findByDepartment(entity)==null)
			flash.message = message(code: 'status.hasequipment', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Status {0} still has associated equipment.')
		else entity.delete()
	}
	
	def getEntityClass() {
		return EquipmentStatus.class;
	}
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def getModel(def entity) {
		[
			status:entity
		]
	}
	
	def list={
		adaptParamsForList()
		def equipmentStatus = EquipmentStatus.list(params)
		render(view:"/entity/list", model:[
			template:"equipmentStatus/equipmentStatusList",
			entities: equipmentStatus,
			entityCount: EquipmentStatus.count(),
			code: getLabel(),
			entityClass: getEntityClass()
			])
	}
}
