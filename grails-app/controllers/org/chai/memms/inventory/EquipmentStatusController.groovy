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

import org.chai.memms.AbstractEntityController;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentStatus;

/**
 * @author Jean Kahigiso M.
 *
 */

class EquipmentStatusController extends AbstractEntityController{
	
	def equipmentStatusService
	def equipmentService
	
    def getEntity(def id) {
		return EquipmentStatus.get(id);
	}
	def createEntity() {
		return new EquipmentStatus();
	}

	def getTemplate() {
		return "/entity/equipmentStatus/createEquipmentStatus";
	}

	def getLabel() {
		
		return "equipment.status.label";

	}
	
	def getEntityClass() {
		return EquipmentStatus.class;
	}
	def deleteEntity(def entity) {
		def equipment = entity.equipment
		if(equipment.status && equipment.status.size()==1)
			flash.message = message(code: "equipment.without.status", args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Status {0} cannot be deleted')
		else{
			equipment.status.remove(entity)
			super.deleteEntity(entity);
			equipmentService.updateCurrentEquipmentStatus(equipment,null,user)
		}
	}
	
	def bindParams(def entity) {
		if(log.isDebugEnabled()) log.debug("Equipment status params: "+params)
		//Make sure we cannot edit a status
		if (entity.id != null) 
			response.sendError(404)
		else{
			entity.changedBy = user
		}
		if(!entity.equipment?.currentStatus?.equals(Status.DISPOSED))
			entity.properties = params
	}
	
	def saveEntity(def entity) {
		if(!entity.equipment?.currentStatus?.equals(Status.DISPOSED))
			equipmentService.updateCurrentEquipmentStatus(entity.equipment,entity,user)
		else flash.message = message(code: "error.cannot.modify.disposed.equipment", default: 'Cannot modify a disposed equipment, please reactivate it')
	}
	
	def getModel(def entity) {
		
		[
			status:entity,
			equipment:entity.equipment,
			numberOfStatusToDisplay: grailsApplication.config.status.to.display.on.equipment.form,
			now:now
		]
	}
	
	def list={
		adaptParamsForList()
		def equipment = Equipment.get(params.int("equipment.id"))
		
		if (equipment == null) 
			response.sendError(404)
		else{
			def equipmentStatus  = equipmentStatusService.getEquipmentStatusByEquipment(equipment,params)
			if(request.xhr)
				this.ajaxModel(equipment,equipmentStatus)
			else{
				render(view:"/entity/list", model:[
					template: "equipmentStatus/equipmentStatusList",
					listTop:"equipmentStatus/listTop",
					equipment: equipment,
					entities: equipmentStatus,
					entityCount: equipmentStatus.totalCount,
					code: getLabel(),
					entityClass: getEntityClass()
					])
			}
		}
	}
	def ajaxModel(def equipment,def entities) {
		def model = [entities: entities,entityCount: entities.totalCount,equipment:equipment,entityClass:getEntityClass()]
		def listHtml = g.render(template:"/entity/equipmentStatus/equipmentStatusList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}
}
