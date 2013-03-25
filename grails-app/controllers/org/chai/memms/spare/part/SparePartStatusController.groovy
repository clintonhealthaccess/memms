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
package org.chai.memms.spare.part

import org.chai.memms.AbstractEntityController;
import org.chai.memms.spare.part.SparePartStatus;
import org.chai.memms.spare.part.SparePart;
import org.chai.memms.spare.part.SparePartStatus.StatusOfSparePart;
import org.chai.memms.inventory.Equipment;
/**
 * @author Aphrodice Rwagaju
 *
 */
class SparePartStatusController extends AbstractEntityController{
	def sparePartStatusService
	def sparePartService
	def usedOnEquipment

	
	def bindParams(def entity) {
		if(log.isDebugEnabled()) log.debug("Spare part status params: "+params)
		if (entity.id != null)
			response.sendError(404)
		else{
			entity.changedBy = user
		}
		if(!entity.sparePart?.statusOfSparePart?.equals(StatusOfSparePart.DISPOSED))
			entity.properties = params
	}

	
	def getModel(def entity) {
		def equipments=[];
		if (entity.sparePart.usedOnEquipment!=null) equipments << entity.sparePart.usedOnEquipment
		[
			status:entity,
			sparePart:entity.sparePart,
			equipments:equipments,
			numberOfStatusToDisplay: grailsApplication.config.status.to.display.on.sparePart.form,
			now:now
		]
	}

	
	def getEntity(Object id) {
		return SparePartStatus.get(id);
	}

	
	def createEntity() {
		return new SparePartStatus();
	}

	
	def getTemplate() {
		return "/entity/sparePartStatus/createSparePartStatus";
	}

	
	def getLabel() {
		return "spare.part.status.label";
	}

	
	def getEntityClass() {
		return SparePartStatus.class;
	}
	
	def deleteEntity(def entity) {
		def sparePart = entity.sparePart
		def usedOnEquipment = Equipment.get(params.int("usedOnEquipment.id"))
		if(sparePart.status && sparePart.status.size()==1)
			flash.message = message(code: "spare.part.without.status", args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Status {0} cannot be deleted')
		else{
			sparePart.status.remove(entity)
			super.deleteEntity(entity);
			sparePartService.updateCurrentSparePartStatus(sparePart,null,user, usedOnEquipment)
		}
	}
	
	def saveEntity(def entity) {
		def usedOnEquipment = Equipment.get(params.int("usedOnEquipment.id"))
		if(!entity.sparePart?.statusOfSparePart?.equals(StatusOfSparePart.DISPOSED)){
		if(log.isDebugEnabled()) log.debug("EQUIPMENT AFTER BINDING:"+usedOnEquipment)
			sparePartService.updateCurrentSparePartStatus(entity.sparePart,entity,user, usedOnEquipment)
		}
		else flash.message = message(code: "error.cannot.modify.disposed.spare.part", default: 'Cannot modify a disposed spare part, please change its status first')
	}
	
	
	def list={
		adaptParamsForList()
		def sparePart = SparePart.get(params.int("sparePart.id"))
		
		if (sparePart == null)
			response.sendError(404)
		else{
			def sparePartStatus  = sparePartStatusService.getSparePartStatusBySparePart(sparePart,params)
			if(request.xhr)
				this.ajaxModel(sparePart,sparePartStatus)
			else{
				render(view:"/entity/list", model:[
					template: "sparePartStatus/sparePartStatusList",
					listTop:"sparePartStatus/listTop",
					sparePart: sparePart,
					entities: sparePartStatus,
					entityCount: sparePartStatus.totalCount,
					code: getLabel(),
					entityClass: getEntityClass()
					])
			}
		}
	}
	def ajaxModel(def sparePart,def entities) {
		def model = [entities: entities,entityCount: entities.totalCount,sparePart:sparePart,entityClass:getEntityClass()]
		def listHtml = g.render(template:"/entity/sparePartStatus/sparePartStatusList",model:model)
		render(contentType:"text/json") { results = [listHtml] }
	}
}
